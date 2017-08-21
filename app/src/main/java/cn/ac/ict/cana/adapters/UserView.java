package cn.ac.ict.cana.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.models.User;

/**
 * Author: saukymo
 * Date: 9/13/16
 */

@EViewGroup(R.layout.adapter_user)
public class UserView extends LinearLayout {

    @ViewById(R.id.tv_user_name) public TextView tvUserName;
    @ViewById(R.id.tv_user_age) public TextView tvUserAge;
    @ViewById(R.id.tv_user_gender) public TextView tvUserGender;
    public Context mContext;

    public UserView(Context context) {
        super(context);
        mContext = context;
    }

    public void bind(User user) {
        Log.d("UserView", String.valueOf(tvUserName));
        tvUserName.setText("姓名: " + user.name);
        tvUserAge.setText("年龄: " + String.valueOf(user.age));
        if (user.gender) {
            tvUserGender.setText("性别: 女");
        } else {
            tvUserGender.setText("性别: 男");
        }
    }
}


