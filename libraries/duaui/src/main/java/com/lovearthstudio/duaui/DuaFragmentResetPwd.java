package com.lovearthstudio.duaui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.DuaCallback;
import com.lovearthstudio.duasdk.DuaConfig;
import com.lovearthstudio.duaui.base.BackHandledFragment;
import com.lovearthstudio.duaui.base.PasswordEdit;
import com.lovearthstudio.duaui.util.AlertUtil;
import com.lovearthstudio.duaui.util.InfoUtil;
import com.xwray.passwordview.PasswordView;

public class DuaFragmentResetPwd extends BackHandledFragment implements View.OnClickListener {
    private PasswordEdit editText_pwd;
    private PasswordEdit editText_repwd;
    private GridPasswordView pswView;
    private PasswordView passwordview;
    private ImageView ivShowPwd;
    private boolean isShowPwd;
    //  private Switch aSwitch;
    private Button button_next_step;

    private String mode;
    private DuaActivityLogin activity;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dua_fragment_resetpwd, container, false);

        pswView = (GridPasswordView) view.findViewById(R.id.pswView);
        passwordview = (PasswordView) view.findViewById(R.id.passwordview);
        passwordview.addTextChangedListener(new PasswordWatcher());
        ivShowPwd = (ImageView) view.findViewById(R.id.iv_show_paw);
        ivShowPwd.setOnClickListener(this);
        //  aSwitch = (Switch) view.findViewById(R.id.switch_pass);
        // aSwitch.setOnCheckedChangeListener(this);

        button_next_step = (Button) view.findViewById(R.id.dua_register_button_next_step);
        button_next_step.setOnClickListener(this);
        editText_pwd = (PasswordEdit) view.findViewById(R.id.dua_et_password);
        editText_pwd.requestFocus();
        editText_repwd = (PasswordEdit) view.findViewById(R.id.dua_et_repwd);
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
        if (mode != null && mode.equals("ResetPwd")) {
            button_next_step.setText("确定");
            activity.centerTitle.setText("重置密码");
        } else {
            activity.centerTitle.setText("注册");
        }
        activity.isRootFragment = false;
        activity.leftIcon.setVisibility(View.VISIBLE);
        activity.leftTitle.setVisibility(View.VISIBLE);
//        button_next_step.setEnabled(!inputIsEmpty(editText_pwd)&&!inputIsEmpty(editText_repwd));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dua_register_button_next_step) {
//            String pwd = editText_pwd.getText().toString().trim();
//            String prompt=PasswordChecker.checkPwd(pwd,6,10,1,1,1,false);
//            if(!prompt.equals(PasswordChecker.OK_STR)){
//                editText_pwd.setError(prompt);
//                editText_pwd.requestFocus();
//                return;
//            }
           /* if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                //editText_pwd.setError("请输入至少6位密码");
                new PromptDialog(activity)
                        .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                        .setAnimationEnable(true)
                        .setTitleText("提示")
                        .setContentText("请输入至少6位密码")
                        .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                editText_pwd.requestFocus();
                return;
            }
            String repwd = editText_repwd.getText().toString().trim();
            if (!pwd.equals(repwd)) {
                // editText_repwd.setError("两次密码必须相同");
                new PromptDialog(activity)
                        .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                        .setAnimationEnable(true)
                        .setTitleText("提示")
                        .setContentText("两次密码必须相同")
                        .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                editText_repwd.requestFocus();
                return;
            }*/
            //activity.pwd = pwd;
            //activity.repwd = repwd;
            //String passWord = pswView.getPassWord();
            String passWord = passwordview.getText().toString();
            if (TextUtils.isEmpty(passWord)) {
                InfoUtil.showDialog(getActivity(), "注意", "请输入密码", "知道了");
                return;
            }
            activity.pwd = passWord;
            activity.repwd = passWord;
            //activity.repwd = pswView.getPassWord();
            if (mode != null && mode.equals("ResetPwd")) {
                startResetPwd();
            } else {
                activity.setCurrentFragment(new DuaFragmentProfileSex(), null);
            }
        } else if (i == R.id.iv_show_paw) {
            if (isShowPwd) {
                ivShowPwd.setImageResource(R.drawable.ic_eye_disable);
            } else {
                ivShowPwd.setImageResource(R.drawable.ic_eye_enable);
            }
            pswView.togglePasswordVisibility();
            isShowPwd = !isShowPwd;
        }
    }

    public void startResetPwd() {
        DuaCallback callback = new DuaCallback() {
            @Override
            public void onSuccess(Object result) {

                InfoUtil.showDialog(getActivity(), "注意", "密码设置成功", "知道了");
                activity.onDismissPressed();
            }

            @Override
            public void onError(int status, String s) {
                String reason = DuaConfig.errCode.get(status);
                if (reason == null) {
                    reason = s;
                }
                AlertUtil.showToast(activity, reason);
            }
        };
        Dua.getInstance().resetPwd(activity.ustr, activity.vf_code, activity.pwd, callback);
    }

    class PasswordWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
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
