package com.lovearthstudio.duaui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.DuaCallback;
import com.lovearthstudio.duasdk.DuaConfig;
import com.lovearthstudio.duaui.base.BackHandledFragment;
import com.lovearthstudio.duaui.util.AlertUtil;
import com.lovearthstudio.duaui.util.InfoUtil;
import com.lovearthstudio.intlphoneinput.IntlPhoneInput;

import java.util.Locale;

public class DuaFragmentLogin extends BackHandledFragment implements OnClickListener, TextWatcher {
    private static final int STOP_ANIM = 1;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private TextView mTvSignIn;
    InputMethodManager imm;

    private IntlPhoneInput phoneInputView;
    private EditText mPasswordView;

    public DuaActivityLogin activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dua_fragment_login, container, false);
        activity = (DuaActivityLogin) getActivity();
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mEtUsername = (EditText) view.findViewById(R.id.et_phone);
        mEtUsername.addTextChangedListener(new PhoneNumberWatcher(DEFAULT_COUNTRY));
        mEtPassword = (EditText) view.findViewById(R.id.et_password);
        mEtPassword.addTextChangedListener(new PasswordTextWathcer());
        mTvSignIn = (TextView) view.findViewById(R.id.dua_login_button_sign_in);
        mTvSignIn.setOnClickListener(this);

        mPasswordView = (EditText) view.findViewById(R.id.dua_login_input_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.dua_action_ime_login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        phoneInputView = (IntlPhoneInput) view.findViewById(R.id.dua_login_input_phone);
        Dua.DuaUser user = Dua.getInstance().getCurrentDuaUser();
        phoneInputView.setNumber(user.tel);
        phoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                if (isValid) {
                    //  mSignInButton.setEnabled(true);
                    // mSignInButton.setBgColor(Color.parseColor("#A2E08D"));
                    mPasswordView.requestFocus();
                } else {
                    // mSignInButton.setEnabled(false);
                    // mSignInButton.setBgColor(Color.parseColor("#DDE08D"));
                    AlertUtil.showToast(activity, "手机号格式不正确");
                }
            }
        });
        TextView textView_register = (TextView) view.findViewById(R.id.dua_login_button_sign_up);
        textView_register.setOnClickListener(this);
        TextView textView_reset_pwd = (TextView) view.findViewById(R.id.dua_login_button_forget_pwd);
        textView_reset_pwd.setOnClickListener(this);
        return view;
    }

    private void attemptLogin() {
        String phone = "";
        /*if (phoneInputView.isValid()) {
            phone = phoneInputView.getUstr();
        }*/
        if (TextUtils.isEmpty(mEtUsername.getText().toString().trim())) {
            InfoUtil.showDialog(getActivity(), "注意", "请输入手机号码", "知道了");
            mEtUsername.requestFocus();
            return;
        }

        if (isValid()) {
            phone = "+86-" + mEtUsername.getText().toString().trim();
        } else {
            InfoUtil.showDialog(getActivity(), "注意", "电话号码错误", "知道了");
            return;
        }

        String password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
           /* new PromptDialog(activity)
                    .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                    .setAnimationEnable(true)
                    .setTitleText("提示")
                    .setContentText("请输入密码")
                    .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();*/
            InfoUtil.showDialog(getActivity(), "注意", "请输入密码", "知道了");
            // mPasswordView.setError("请输入密码");
            //mPasswordView.requestFocus();
            mEtPassword.requestFocus();
            return;
        }
        // mSignInButton.startAnim();
        Dua.getInstance().login(phone, password, "Dua.Default", new DuaCallback() {
            @Override
            public void onSuccess(Object s) {
                // handler.sendEmptyMessage(STOP_ANIM);
                activity.onLoginOk();
            }

            @Override
            public void onError(int status, String s) {
                // handler.sendEmptyMessage(STOP_ANIM);
                String reason = DuaConfig.errCode.get(status);
                if (reason == null) {
                    reason = s;
                }
                /*new PromptDialog(activity)
                        .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                        .setAnimationEnable(true)
                        .setTitleText("提示")
                        .setContentText(s)
                        .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();*/
                InfoUtil.showDialog(getActivity(), "注意", s, "知道了");

                // AlertUtil.showToast(activity, reason);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.isRootFragment = true;
        // activity.leftIcon.setVisibility(View.INVISIBLE);
        // activity.leftTitle.setVisibility(View.INVISIBLE);
        activity.centerTitle.setText("登录");

        if (activity.ustr != null) {
            setPhoneNumber(activity.ustr);
        }
        try {
            if (isValid()) {
                mEtPassword.requestFocus();
            } else {
                mEtUsername.requestFocus();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (phoneInputView.isValid()) {
            mPasswordView.requestFocus();
        } else {
            phoneInputView.requestFocus();
        }*/
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dua_login_button_sign_in) {
            attemptLogin();
        } else if (i == R.id.dua_login_button_sign_up) {
            DuaFragmentGetVfCode duaFragmentGetVfCode = new DuaFragmentGetVfCode();
            Bundle args1 = new Bundle();
            args1.putString("mode", "register");
            duaFragmentGetVfCode.setArguments(args1);
            activity.setCurrentFragment(duaFragmentGetVfCode, null);
        } else if (i == R.id.dua_login_button_forget_pwd) {
            activity.isRootFragment = false;
            activity.rightTitle.setText("取消");
            activity.setCurrentFragment(new DuaFragmentGetVfCode(), null);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    public void setPhoneNumber(String ustr) {
        int index = ustr.indexOf("-");
        String number = ustr.substring(index + 1);
        //phoneInputView.setNumber(number);
        mEtUsername.setText(number);
        String prefix = ustr.substring(1, index);
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
                mEtPassword.requestFocus();
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
            phoneNumber = mPhoneUtil.parse(mEtUsername.getText().toString(), DEFAULT_COUNTRY);
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
                    mEtPassword.requestFocus();
                } else {
                    mTvSignIn.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 密码数据观察者
     */
    private class PasswordTextWathcer implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            System.out.println("===============:PasswordTextWathcer:" + isValid() + ":" + (s.length() > 0));
            if (isValid() && s.length() > 0) {
                mTvSignIn.setEnabled(true);
            } else {
                mTvSignIn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}

