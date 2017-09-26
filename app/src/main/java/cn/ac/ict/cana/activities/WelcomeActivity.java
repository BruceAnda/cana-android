package cn.ac.ict.cana.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.DuaCallback;
import com.lovearthstudio.duasdk.util.ComUtil;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.parkionsdatacollection.base.YouMengBaseActivity;
import cn.ac.ict.cana.login.LandPageActivity;
import cn.refactor.lib.colordialog.ColorDialogPermission;
import cn.refactor.lib.colordialog.ColorDialogPermissionDead;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * <pre>
 * 第一个界面，欢迎界面
 * 需求：
 *      1. 控制音量
 *      2. 2.5秒后进入权限页面，如果所有权限都已经通过，进入主程序界面
 * </pre>
 */
@RuntimePermissions
public class WelcomeActivity extends YouMengBaseActivity implements Animation.AnimationListener {
    private final static String TAG = "welcome";
    private AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
    private LinearLayout activity_welcome;
    private ColorDialogPermission colorDialog;
    private TextView tv_welcome_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //  EventBus.getDefault().register(this);
        setContentView(R.layout.activity_welcome);
        activity_welcome = (LinearLayout) findViewById(R.id.activity_welcome);
        tv_welcome_name = (TextView) findViewById(R.id.tv_welcome_name);
        tv_welcome_name.setText(getVersionName());

        alphaAnimation.setAnimationListener(this);
        alphaAnimation.setDuration(2500);
        activity_welcome.startAnimation(alphaAnimation);
        //音量控制,初始化定义  
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //最大音量  
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量  
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "currentVolume: " + currentVolume);
        Log.d(TAG, "maxVolume: " + maxVolume);

        if (currentVolume < maxVolume / 2) {
            Toast.makeText(this, getString(R.string.change_volume), Toast.LENGTH_SHORT).show();
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }

    public String getVersionName() {
        String versionName = "中科 宣武\n v1.2";
        try {
            String pkName = this.getPackageName();
            versionName = "中科 宣武\n " + this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;

        } catch (Exception e) {
        }
        return versionName;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        colorDialog = new ColorDialogPermission(WelcomeActivity.this);
        colorDialog.setCancelable(false);
        colorDialog.setListener(new ColorDialogPermission.OnClickListener() {
            @Override
            public void cancel(ColorDialogPermission dialog2) {
                dialog2.dismiss();
                ColorDialogPermissionDead colorDialogPermissionDead = new ColorDialogPermissionDead(WelcomeActivity.this);
                colorDialogPermissionDead.setCancelable(false);
                colorDialogPermissionDead.setListener(new ColorDialogPermissionDead.OnClickListener() {
                    @Override
                    public void cancel(ColorDialogPermissionDead dialog) {
                        colorDialog.show();
                    }

                    @Override
                    public void determine(ColorDialogPermissionDead dialog) {
                        finish();
                    }
                }).show();
            }

            @Override
            public void start(ColorDialogPermission dialog2) {
                WelcomeActivityPermissionsDispatcher.showPermissionsWithCheck(WelcomeActivity.this);
                dialog2.dismiss();
            }
        });
        //selectPager();
        /*colorDialog = new ColorDialogPermission(WelcomeActivity.this);
        colorDialog.setColor(WelcomeActivity.this.getResources().getColor(R.color.colorAccent));
        colorDialog.setContentTextSize(WelcomeActivity.this.getResources().getDimension(R.dimen.info_content_text_size));
        colorDialog.setTitleTextSize(WelcomeActivity.this.getResources().getDimension(R.dimen.info_title_text_size));
        colorDialog.setNegativeTextSize(WelcomeActivity.this.getResources().getDimension(R.dimen.info_button_text_size));
        colorDialog.setPositiveTextSize(WelcomeActivity.this.getResources().getDimension(R.dimen.info_button_text_size));
        colorDialog.setAnimationEnable(true);
        colorDialog.setTitle("PD助手需要以下权限才能正常运行");
        colorDialog.setContentText("拍照权限 \n存储权限");
        colorDialog.setCancelable(false);
        colorDialog.setPositiveListener("开启", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                WelcomeActivityPermissionsDispatcher.showPermissionsWithCheck(WelcomeActivity.this);
                dialog.dismiss();
            }
        }).setNegativeListener("取消", new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
                new AlertDialog.Builder(WelcomeActivity.this).setTitle("提示！").setMessage("不开启权限将关闭PD助手，确定不开启吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        colorDialog.show();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });*/
        if (EasyPermissions.hasPermissions(WelcomeActivity.this, perms)) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
                selectPager();
            } else {
                Toast.makeText(this, "手机没有网络，数据将无法上传！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomeActivity.this, MainActivityNew.class));
                finish();
            }
        } else {
            colorDialog.show();
        }
    }

    private void selectPager() {
        PgyUpdateManager.register(this, "cn.ac.ict.cana.fileprovider", new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                Dua.DuaUser duaUser = Dua.getInstance().getCurrentDuaUser();
                if (duaUser.logon) {
                     startActivity(new Intent(WelcomeActivity.this, MainActivityNew.class));
                    //startActivity(new Intent(WelcomeActivity.this, CountSimKeyboardActivity.class));
                    finish();
                } else {
                    startActivityForResult(new Intent(WelcomeActivity.this, LandPageActivity.class), 10086);
                    finish();
                }
            }

            @Override
            public void onUpdateAvailable(String s) {
                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(s);
                new AlertDialog.Builder(WelcomeActivity.this)
                        .setTitle("更新")
                        .setMessage(appBean.getReleaseNote())
                        .setNegativeButton(
                                "确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        startDownloadTask(
                                                WelcomeActivity.this,
                                                appBean.getDownloadURL());
                                    }
                                }).show();
            }
        });
       /* Dua.DuaUser duaUser = Dua.getInstance().getCurrentDuaUser();
        if (duaUser.logon) {
            startActivity(new Intent(this, MainActivityNew_.class));
            finish();
        } else {
            startActivityForResult(new Intent(this, DuaActivityLogin.class), 10086);
            finish();
        }*/
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
            PgyUpdateManager.unregister();
        }
    }

   /* *//**
     * 登录成功调用
     *
     * @author zhaoliang
     * create at 16/11/8 下午3:06
     *//*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String operator) {
        if (operator.equals(DuaActivityLogin.LOGIN_SUCCESS)) {
            updateProfile();
            startActivity(new Intent(this, MainActivityNew.class));
            finish();
        }
    }*/


    /**
     * 更新本地SharedPerferences用户信息
     *
     * @author zhaoliang
     * create at 16/11/8 下午3:06
     */
    private void updateProfile() {
        JSONArray array = ComUtil.listToJSONArray("bday", "sex", "name", "avatar", "height", "weight", "saying");
        Dua.getInstance().getUserProfile(array, new DuaCallback() {
            @Override
            public void onSuccess(Object result) {
                try {
                    String bday = ((JSONObject) result).optString("bday");
                    String sex = ((JSONObject) result).optString("sex");
                    String name = ((JSONObject) result).optString("name");
                    String avatar = ((JSONObject) result).optString("avatar");
                    String height = ((JSONObject) result).optString("height");
                    String weight = ((JSONObject) result).optString("weight");
                    String saying = ((JSONObject) result).optString("saying");

                    getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("bday", bday)
                            .putString("sex", sex)
                            .putString("name", name)
                            .putString("avatar", avatar)
                            .putString("height", height)
                            .putString("weight", weight)
                            .putString("saying", saying).commit();

                    JSONObject event = new JSONObject();
                    event.put("type", "update_profile");
                    EventBus.getDefault().post(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int status, String reason) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WelcomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    })
    public void showPermissions() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
            selectPager();
        } else {
            Toast.makeText(this, "手机没有网络，数据将无法上传！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WelcomeActivity.this, MainActivityNew.class));
            finish();
        }
    }

    private String[] perms = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @OnShowRationale({
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    })
    void showRationaleForContact(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_contacts_rationale, request);
    }

    @OnPermissionDenied({
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    })
    void onPermissionsDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        //Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
        colorDialog.show();
    }

    /*@OnNeverAskAgain({
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
    })
    void onPermissionsNeverAskAgain() {
        Toast.makeText(this, R.string.permission_camera_never_askagain, Toast.LENGTH_SHORT).show();
    }*/

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }
}
