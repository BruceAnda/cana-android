package cn.ac.ict.cana.base;

import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public class YouMengBaseActivity extends FragmentActivity {

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
