package cn.ac.ict.cana.duaui;


import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;
import com.jungly.gridpasswordview.GridPasswordView;

import java.util.Calendar;
import java.util.Date;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.duaui.base.BackHandledFragment;
import cn.refactor.lib.colordialog.PromptDialog;


public class DuaFragmentProfileBirthday extends BackHandledFragment implements View.OnClickListener, WheelDatePicker.OnDateSelectedListener {
    private WheelDatePicker picker;
    private TextView curBirthday;
    private EditText etBirthday;
    private Button button_last_step;
    private Button button_complete;

    private GridPasswordView psw_birthday;

    private DuaActivityLogin activity;

    public DuaFragmentProfileBirthday() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dua_fragment_profile_birthday, container, false);
        etBirthday = (EditText) view.findViewById(R.id.et_birthday);
        psw_birthday = (GridPasswordView) view.findViewById(R.id.psw_birthday);
        psw_birthday.setPasswordVisibility(true);
        button_last_step = (Button) view.findViewById(R.id.dua_register_button_last_step);
        button_last_step.setOnClickListener(this);
        button_complete = (Button) view.findViewById(R.id.dua_register_button_next_step);
        button_complete.setOnClickListener(this);

        curBirthday = (TextView) view.findViewById(R.id.dua_register_tv_curBirthday);
        picker = (WheelDatePicker) view.findViewById(R.id.dua_register_date_picker);
        TextView yearTv = (TextView) view.findViewById(R.id.wheel_date_picker_year_tv);
        yearTv.setTextColor(Color.parseColor("#55000000"));
        yearTv.setTextSize(25);
        TextView monthTv = (TextView) view.findViewById(R.id.wheel_date_picker_month_tv);
        monthTv.setTextColor(Color.parseColor("#55000000"));
        monthTv.setTextSize(25);
        TextView dayTv = (TextView) view.findViewById(R.id.wheel_date_picker_day_tv);
        dayTv.setTextColor(Color.parseColor("#55000000"));
        dayTv.setTextSize(25);
        picker.setOnDateSelectedListener(this);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        picker.setYearStart(year - 100);
        picker.setYearEnd(year);
        picker.setIndicator(true);
        picker.setIndicatorColor(Color.parseColor("#55000000"));
        picker.setSelectedYear(year - 18);
        picker.setCurved(true);
        picker.setCyclic(true);
        picker.setVisibleItemCount(5);
        picker.setAtmospheric(true);
        picker.setItemTextSize(100);
        picker.setItemAlignYear(100);
        picker.setItemAlignMonth(100);
        picker.setItemAlignDay(100);
//        setCurBirthday(year-18,picker.getCurrentMonth(),picker.getCurrentDay());
        activity = (DuaActivityLogin) getActivity();
        activity.leftTitle.setVisibility(View.VISIBLE);

        if (activity.birthday != null) {
            try {
                String y = activity.birthday.substring(0, 4);
                String m = activity.birthday.substring(4, 6);
                String d = activity.birthday.substring(6);
                curBirthday.setText(y + "年" + m + "月" + d + "日");
                picker.setSelectedYear(Integer.parseInt(y));
                picker.setSelectedMonth(Integer.parseInt(m));
                picker.setSelectedDay(Integer.parseInt(d));
            } catch (Exception e) {

            }
        }

        psw_birthday.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                if (psw.length() >= 8) {
                    button_complete.setEnabled(true);
                } else {
                    button_complete.setEnabled(false);
                }
            }

            @Override
            public void onInputFinish(String psw) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dua_register_button_last_step) {
            activity.onBackPressed();
        } else if (i == R.id.dua_register_button_next_step) {
            //activity.birthday = etBirthday.getText().toString().trim();
            activity.birthday = psw_birthday.getPassWord();
            if (TextUtils.isEmpty(activity.birthday)) {
                //AlertUtil.showToast(getContext(),"请选择生日");
                new PromptDialog(activity)
                        .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                        .setAnimationEnable(true)
                        .setTitleText("提示")
                        .setContentText("请选择生日")
                        .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                return;
            }
            activity.setCurrentFragment(new DuaFragmentProfileAvata(), "Avatar");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.isRootFragment = false;
        activity.leftIcon.setVisibility(View.VISIBLE);
        activity.leftTitle.setVisibility(View.VISIBLE);
        activity.centerTitle.setText("注册");
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDateSelected(WheelDatePicker picker, Date date) {
        // activity.birthday= TimeUtil.toTimeString(date.getTime(),"yyyyMMdd");
        activity.birthday = etBirthday.getText().toString().trim();
        setCurBirthday(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public void setCurBirthday(int year, int mon, int date) {
        String month = mon + "";
        if (month.length() < 2) {
            month = "0" + month;
        }
        String day = date + "";
        if (day.length() < 2) {
            day = "0" + day;
        }
        curBirthday.setText(year + "年" + month + "月" + day + "日");
    }
}
