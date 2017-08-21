package cn.ac.ict.cana.duaui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.DuaCallback;
import com.lovearthstudio.duasdk.DuaConfig;
import com.lovearthstudio.intlphoneinput.IntlPhoneInput;

import org.json.JSONObject;

import java.util.Locale;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.duaui.base.BackHandledFragment;
import cn.ac.ict.cana.duaui.util.InfoUtil;
import cn.ac.ict.cana.duaui.util.ThreadUtil;

public class DuaFragmentGetVfCode extends BackHandledFragment implements View.OnClickListener, TextWatcher {
    private IntlPhoneInput intlPhoneInput;
    private EditText etPhone;
    private EditText editText_vf_code;
    private TextView button_get_vf_code;
    private Button button_next_step;
    private DuaActivityLogin activity;
    private static final int UPDATE_TIMER_TICK = 10010;
    private static final int UPDATE_TIMER_FINISH = 10086;
    private String mode;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TIMER_TICK:
                    button_get_vf_code.setEnabled(false);
                    button_get_vf_code.setText((String) msg.obj);
                    break;
                case UPDATE_TIMER_FINISH:
                    button_get_vf_code.setEnabled(true);
                    button_get_vf_code.setText(R.string.dua_button_get_vf_code);
                    intlPhoneInput.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dua_fragment_getvfcode, container, false);
        intlPhoneInput = (IntlPhoneInput) view.findViewById(R.id.dua_register_input_phone);

        etPhone = (EditText) view.findViewById(R.id.et_regist_phone);
        etPhone.addTextChangedListener(new PhoneNumberWatcher(DEFAULT_COUNTRY));
        editText_vf_code = (EditText) view.findViewById(R.id.dua_register_input_vf_code);
        editText_vf_code.addTextChangedListener(new VfCodeWatcher());

        button_get_vf_code = (TextView) view.findViewById(R.id.dua_register_button_get_vf_code);
        button_next_step = (Button) view.findViewById(R.id.dua_register_button_next_step);
        try {
            mode = getArguments().getString("mode");
        } catch (Exception e) {

        }
        activity = (DuaActivityLogin) getActivity();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //boolean bl = intlPhoneInput.isValid();
        button_get_vf_code.setEnabled(isValid());
        button_get_vf_code.setOnClickListener(this);
        button_next_step.setOnClickListener(this);

        activity.isRootFragment = false;
        activity.leftIcon.setVisibility(View.VISIBLE);
        activity.leftTitle.setVisibility(View.VISIBLE);
        activity.leftTitle.setText("返回");
        if (mode != null && mode.equals("register")) {
            activity.centerTitle.setText("注册");
        } else {
            activity.centerTitle.setText("重置密码");
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dua_register_button_get_vf_code) {
            if (checkUstr()) {
                intlPhoneInput.setEnabled(false);
                button_get_vf_code.setEnabled(false);
                Dua.getInstance().getVfCode(activity.ustr, new DuaCallback() {
                    @Override
                    public void onSuccess(Object str) {
                        ThreadUtil.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                startCountDownTimer();
                            }
                        });
                    }

                    @Override
                    public void onError(int status, String str) {
                        updateUI(UPDATE_TIMER_FINISH, null);
                        InfoUtil.showDialog(getActivity(), "注意", status + " " + str, "知道了");
                    }
                });
            }
        } else if (i == R.id.dua_register_button_next_step) {
            if (checkUstr()) {
                String vf_code = editText_vf_code.getText().toString().trim();
                if (TextUtils.isEmpty(vf_code)) {
                    InfoUtil.showDialog(getActivity(), "注意", "请输入短信验证码", "知道了");
                    editText_vf_code.requestFocus();
                    return;
                }
                activity.vf_code = vf_code;
                checkVfCode(activity.ustr, vf_code);
            }
        }
    }

    public void checkVfCode(String ustr, String vf_code) {
        Dua.getInstance().checkVfCode(ustr, vf_code, new DuaCallback() {
            @Override
            public void onSuccess(Object result) {
                if (mode != null && mode.equals("register")) {
                    try {
                        JSONObject jo = (JSONObject) result;
                        if (jo.getInt("ustr_exist") > 0) {
                            activity.showLoginDialog();
                        } else {
                            activity.setCurrentFragment(new DuaFragmentResetPwd(), null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    DuaFragmentResetPwd duaFragmentResetPwd = new DuaFragmentResetPwd();
                    Bundle args = new Bundle();
                    args.putString("mode", "ResetPwd");
                    duaFragmentResetPwd.setArguments(args);
                    activity.setCurrentFragment(duaFragmentResetPwd, null);
                }
            }

            @Override
            public void onError(int status, String s) {
                String reason = DuaConfig.errCode.get(status);
                if (reason == null) {
                    reason = s;
                }
                InfoUtil.showDialog(getActivity(), "注意", reason, "知道了");
            }
        });
    }

    public boolean checkUstr() {
        String phone = "";
        if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            InfoUtil.showDialog(getActivity(), "注意", "请输入手机号码", "知道了");
            etPhone.requestFocus();
            return false;
        }

        if (isValid()) {
            phone = "+86-" + etPhone.getText().toString().trim();
        } else {
            InfoUtil.showDialog(getActivity(), "注意", "电话号码错误", "知道了");
            return false;
        }
        activity.ustr = phone;
        return true;
    }

    private CountDownTimer timer;

    private void startCountDownTimer() {
        if (timer == null) {
            timer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateUI(UPDATE_TIMER_TICK, millisUntilFinished / 1000 + "秒");
                }

                @Override
                public void onFinish() {
                    updateUI(UPDATE_TIMER_FINISH, null);
                }
            };
        }
        timer.start();
    }

    private void updateUI(int what, Object obj) {
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        msg.sendToTarget();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    private final String DEFAULT_COUNTRY = Locale.CHINA.getCountry();
    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();
    Phonenumber.PhoneNumber phoneNumber = null;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            if (isValid()) {
                editText_vf_code.requestFocus();
                button_get_vf_code.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean isValid() {
        try {
            phoneNumber = mPhoneUtil.parse(etPhone.getText().toString(), DEFAULT_COUNTRY);
            return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Phone number watcher
     */
    private class PhoneNumberWatcher extends PhoneNumberFormattingTextWatcher {

        @SuppressWarnings("unused")
        public PhoneNumberWatcher() {
            super();
        }

        //TODO solve it! support for android kitkat
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public PhoneNumberWatcher(String countryCode) {
            super(countryCode);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            try {
                if (isValid()) {
                    editText_vf_code.requestFocus();
                    button_get_vf_code.setEnabled(true);
                } else {
                    button_get_vf_code.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 验证码验证Watcher类
     */
    private class VfCodeWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            System.out.println("=======================VfCodeWatcher:beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // System.out.println("=======================VfCodeWatcher:" + s + ":" + s.length() + ":" + start + ":" + before + ":" + count);
            if (s.length() >= 6) {
                button_next_step.setEnabled(true);
            } else {
                button_next_step.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
