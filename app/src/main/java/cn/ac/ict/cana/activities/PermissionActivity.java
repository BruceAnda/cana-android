package cn.ac.ict.cana.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;
import com.lovearthstudio.duasdk.Dua;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.adapters.PermissionAdapter;
import cn.ac.ict.cana.base.YouMengBaseActivity;
import cn.ac.ict.cana.duaui.DuaActivityLogin;
import cn.ac.ict.cana.helpers.ToastManager;
import cn.ac.ict.cana.models.Permission;

/**
 * Author: saukymo
 * Date: 9/12/16
 */
@EActivity(R.layout.activity_permission)
public class PermissionActivity extends YouMengBaseActivity {

    @ViewById(R.id.bt_permission_pass)
    Button button;
    @ViewById(R.id.tv_permission_info)
    TextView tvPermissionInfo;

    @ViewById(R.id.lv_permission_not_granted)
    ListView lvPermission;

    @ViewById(R.id.lv_permission_granted)
    ListView lvPermissionGranted;
    @ViewById(R.id.layout_permission)
    LinearLayout lyPermission;

    @Bean
    ToastManager toastManager;
    @Bean
    PermissionAdapter mPermissionAdapter;
    @Bean
    PermissionAdapter mPermissionGrantedAdapter;

    private ArrayList<Permission> mPermissionSet = new ArrayList<>();
    private ArrayList<Permission> mPermissionGrantedSet = new ArrayList<>();

    @AfterViews
    protected void init() {
        EventBus.getDefault().register(this);
        InitialPermissionCheckList();
        PermissionCheckStatus();
        //如果程序开始运行时,所有的权限都已经开通了的话,直接跳过这个界面;
        if (mPermissionAdapter.getCount() == 0) {
            Log.d("Permission", "Skip permission");
            startOurApp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @AfterInject
    public void GeneratePermissionSet() {
        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(new Permission(Manifest.permission.CAMERA));
        permissions.add(new Permission(Manifest.permission.VIBRATE));
        permissions.add(new Permission(Manifest.permission.INTERNET));
        permissions.add(new Permission(Manifest.permission.RECORD_AUDIO));
       // permissions.add(new Permission(Manifest.permission.ACCESS_NETWORK_STATE));
       // permissions.add(new Permission(Manifest.permission.ACCESS_WIFI_STATE));
       // permissions.add(new Permission(Manifest.permission.READ_PHONE_STATE));
       // permissions.add(new Permission(Manifest.permission.ACCESS_FINE_LOCATION));

        for (Permission permission : permissions) {
            permission.permissionStatus = getPackageManager().checkPermission(permission.permissionName, getPackageName()) == PackageManager.PERMISSION_GRANTED;
            if (permission.permissionStatus) {
                mPermissionGrantedSet.add(permission);
            } else {
                mPermissionSet.add(permission);
            }
        }
    }

    public void InitialPermissionCheckList() {
        mPermissionAdapter.setList(mPermissionSet);
        mPermissionGrantedAdapter.setList(mPermissionGrantedSet);

        lvPermission.setAdapter(mPermissionAdapter);
        lvPermissionGranted.setAdapter(mPermissionGrantedAdapter);
    }

    @Click(R.id.bt_permission_pass)
    public void startOurApp() {
        Log.d("PermissionActivity", "Start Main activity");
//        toastManager.show("Start main activity.");
        // Intent intent = new Intent(PermissionActivity.this, MainActivity_.class);

        Dua.DuaUser duaUser = Dua.getInstance().getCurrentDuaUser();
        if (duaUser.logon) {
            startActivity(new Intent(this, MainActivity_.class));
            finish();
        } else {
            startActivityForResult(new Intent(this, DuaActivityLogin.class), 10086);
            //finish();
        }
        // startActivity(intent);
        // finish();
    }

    /**
     * 登录成功调用
     *
     * @author zhaoliang
     * create at 16/11/8 下午3:06
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String operator) {
        if (operator.equals(DuaActivityLogin.LOGIN_SUCCESS)) {
            //updateProfile();
            startActivity(new Intent(this, MainActivity_.class));
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            toastManager.show("Permission Granted");
            Permission permission = mPermissionAdapter.getItem(requestCode);
            permission.permissionStatus = true;

            mPermissionAdapter.deleteItem(requestCode);
            mPermissionGrantedAdapter.addItem(permission);

        } else {
            toastManager.show(getResources().getString(R.string.permission_denied));
        }
        mPermissionAdapter.notifyDataSetChanged();
        mPermissionGrantedAdapter.notifyDataSetChanged();

        PermissionCheckStatus();
    }

    public void PermissionCheckStatus() {
        boolean is_passed = mPermissionAdapter.getCount() == 0;
        if (is_passed) {
//            tvPermissionInfo.setVisibility(View.GONE);
//            lvPermission.setVisibility(View.GONE);
            lyPermission.setVisibility(View.GONE);
            button.setText(R.string.btn_continue);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.freebie_4));
        } else {
            button.setText(String.format(Locale.CHINA, getResources().getString(R.string.permission_remained), mPermissionAdapter.getCount()));
        }
        button.setEnabled(is_passed);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlatUI.initDefaultValues(this);
    }
}