package cn.ac.ict.cana.duaui;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.DuaCallback;
import com.lovearthstudio.duasdk.util.ComUtil;
import com.lovearthstudio.duasdk.util.TimeUtil;
import com.lovearthstudio.duasdk.util.encryption.MD5;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.duaui.base.BackHandledFragment;
import cn.ac.ict.cana.duaui.base.BackHandledInterface;
import cn.ac.ict.cana.duaui.util.AlertUtil;
import cn.ac.ict.cana.duaui.util.FileUtil;
import cn.ac.ict.cana.duaui.util.IntentUtil;
import cn.ac.ict.cana.newversion.activities.MainActivityNew_;
import pub.devrel.easypermissions.EasyPermissions;
import zhaoliang.com.uploadfile.UploadUtils;

import static cn.ac.ict.cana.duaui.util.IntentUtil.PHOTO_REQUEST_TAKEPHOTO;

public class DuaActivityLogin extends Activity implements View.OnClickListener, BackHandledInterface, EasyPermissions.PermissionCallbacks {

    private static final String TAG = DuaActivityLogin.class.getName();
    public static final String LOGIN_SUCCESS = "LoginSuccess";

    public ImageView leftIcon;
    public TextView leftTitle;
    public TextView centerTitle;
    public TextView rightTitle;

    public String ustr;
    public String vf_code;
    public String pwd;
    public String repwd;
    public String name = "匿名";
    public String sex = "U";
    public String birthday = "未设置";
    public String avatar_url = "";

    private Intent okIntent;
    private Intent cancelIntent;
    private BackHandledFragment mBackHandedFragment;
    public FragmentManager fragmentManager;
    public boolean isRootFragment = true;
    private DuaFragmentProfileAvata fragmentProfileAvata;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRootFragment) {
            rightTitle.setText("注册");
            leftTitle.setText("退出");
            leftIcon.setVisibility(View.GONE);
        } else {
            rightTitle.setText("取消");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dua.init(getApplication());
        LOCAL_IMG_PATH = getExternalCacheDir().getPath() + File.separator;
        //       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dua_activity_login);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.dua_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
        leftIcon = (ImageView) findViewById(R.id.dua_bar_left_icon);
        leftIcon.setOnClickListener(this);
        leftTitle = (TextView) findViewById(R.id.dua_bar_left_text);
        leftTitle.setText("退出");
        leftIcon.setVisibility(View.GONE);
        leftTitle.setOnClickListener(this);
        rightTitle = (TextView) findViewById(R.id.dua_bar_right_text);
        rightTitle.setText("取消");
        rightTitle.setOnClickListener(this);
        centerTitle = (TextView) findViewById(R.id.dua_bar_center_title);

        fragmentManager = getFragmentManager();
        setCurrentFragment(new DuaFragmentLogin(), null);

        try {
            cancelIntent = IntentUtil.makeCallbackIntent(this, getIntent().getStringExtra("CancelActivity"));
            okIntent = IntentUtil.makeCallbackIntent(this, getIntent().getStringExtra("OkActivity"));
        } catch (Exception e) {
        }

        // DuaPermissionUtil.requestDuaPermissions(this);
    }

    public void showLoginDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("手机号已经注册，是否直接登陆？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onDismissPressed();
//                        Dua.getInstance().vfcodeLogin(ustr, vf_code, new DuaCallback() {
//                            @Override
//                            public void onSuccess(Object result) {
//                                finish();
//                            }
//
//                            @Override
//                            public void onError(int status, String reason) {
//                                AlertUtil.showToast(DuaActivityLogin.this,status+" "+reason);
//                            }
//                        },pwd);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private String LOCAL_IMG_PATH;
    private Uri uri;
    private String imageName;
    private ImageView iv_avatar;
    private int RESOURCE_ID_START = View.generateViewId();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        IntentUtil.startPhotoShot(DuaActivityLogin.this, uri);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        new AlertDialog.Builder(this).setTitle("提示！").setMessage("没有拍照权限将不能打开相机").setPositiveButton("去打开权限", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //EasyPermissions.requestPermissions(DuaActivityLogin.this, "请求权限", 0x00, camera);
                ActivityCompat.requestPermissions(DuaActivityLogin.this, camera, 1000);
            }
        }).show();
    }

    String[] camera = {Manifest.permission.CAMERA};

    public void showPhotoDialog(ImageView userAvatar) {
        iv_avatar = userAvatar;
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        final int startId = RESOURCE_ID_START + 1;
        String[] content = {"拍照", "相册"};
        AlertUtil.showDialog(dlg, "", startId, content, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == startId) {
                } else if (id == startId + 1) {
                    imageName = TimeUtil.getCurrentTimeString("yyyyMMddHHmmssSS") + ".png";
                    uri = Uri.fromFile(FileUtil.newFile(LOCAL_IMG_PATH, imageName));
                    if (EasyPermissions.hasPermissions(DuaActivityLogin.this, camera)) {
                        IntentUtil.startPhotoShot(DuaActivityLogin.this, uri);
                    } else {
                        EasyPermissions.requestPermissions(DuaActivityLogin.this, "请求权限", 0x00, camera);
                    }

                } else if (id == startId + 2) {
                    imageName = TimeUtil.getCurrentTimeString("yyyyMMddHHmmssSS") + ".png";
                    uri = Uri.fromFile(FileUtil.newFile(LOCAL_IMG_PATH, imageName));
                    IntentUtil.startPhotoGallery(DuaActivityLogin.this);
                }
                dlg.cancel();
            }
        });
    }

    public void setCurrentFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.dua_fragment_root, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        // 头像固定512*512
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputUri(uri)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:
                    //file:///storage/emulated/0/Android/data/cn.ac.ict.cana/cache/2017042102363193.png
                    // imageName = TimeUtil.getCurrentTimeString("yyyyMMddHHmmssSS") + ".png";
                    // IntentUtil.startPhotoZoom(this, uri, Uri.fromFile(FileUtil.newFile(LOCAL_IMG_PATH, imageName)), 480);
                    startCropImageActivity(uri);
                    break;

                case IntentUtil.PHOTO_REQUEST_GALLERY:
                    if (data != null) {
                        //IntentUtil.startPhotoZoom(this, data.getData(), uri, 480);
                        startCropImageActivity(data.getData());
                    }
                    break;

                // handle result of CropImageActivity
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    // CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    //Uri uri = result.getUri();
                    if (resultCode == RESULT_OK) {
                        iv_avatar.setImageURI(uri);
                        //   Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        //  Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                    }
                    UploadUtils.initOSS();
                    String filePath = uri.toString();
                    String fileName = MD5.md5(ustr + System.currentTimeMillis()) + filePath.substring(filePath.lastIndexOf(".") - 1, filePath.length());
                    avatar_url = "http://files.xdua.org/Avatar/" + fileName;
                    UploadUtils.asyncPutFile("Avatar/" + fileName, filePath.replace("file://", ""), new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                            Toast.makeText(DuaActivityLogin.this, "头像上传成功！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                            Toast.makeText(DuaActivityLogin.this, "头像上传失败！", Toast.LENGTH_SHORT).show();
                        }
                    }, null);


             /*   case IntentUtil.PHOTO_REQUEST_CUT:
                    // BitmapFactory.Options options = new BitmapFactory.Options();
                    //
                    // *//**
                 // * 最关键在此，把options.inJustDecodeBounds = true;
                 // * 这里再decodeFile()，返回的bitmap为空
                 // * ，但此时调用options.outHeight时，已经包含了图片的高了
                 // *//*
                    // options.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(LOCAL_IMG_PATH + imageName);
                    iv_avatar.setImageBitmap(bitmap);
                    fragmentProfileAvata = ((DuaFragmentProfileAvata) fragmentManager.findFragmentByTag("Avatar"));
                    fragmentProfileAvata.setNextStepEnable(false);
                    DuaCallback callback = new DuaCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            try {
                                JSONObject jo = (JSONObject) result;
                                final String url = jo.getString("url");
                                avatar_url = url;
                                fragmentProfileAvata.setNextStepEnable(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                                AlertUtil.showToast(DuaActivityLogin.this, "发生未知错误，请更改图片或忽略");
                                fragmentProfileAvata.setNextStepEnable(true);
                            }

                        }

                        @Override
                        public void onError(int status, String reason) {
                            LogUtil.e(status + " " + reason);
                            AlertUtil.showToast(DuaActivityLogin.this, "图片上传失败，请更改图片或忽略");
                            fragmentProfileAvata.setNextStepEnable(true);
                        }
                    };
                    Dua.getInstance().uploadAvatar(LOCAL_IMG_PATH + imageName, callback);
                    break;
*/
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        if (isRootFragment) {
            rightTitle.setText("取消");
            // leftTitle.setText("退出");
            // leftIcon.setVisibility(View.GONE);
        } else {
            rightTitle.setText("注册");
        }
        int i = v.getId();
        if (i == R.id.dua_bar_left_icon || i == R.id.dua_bar_right_text) {
            if (isRootFragment) {
                DuaFragmentGetVfCode duaFragmentGetVfCode = new DuaFragmentGetVfCode();
                Bundle args1 = new Bundle();
                args1.putString("mode", "register");
                duaFragmentGetVfCode.setArguments(args1);
                setCurrentFragment(duaFragmentGetVfCode, null);
            } else {
                onDismissPressed();
            }
        } else if (i == R.id.dua_bar_left_text) {
            if (leftTitle.getText().equals("退出")) {
                new AlertDialog.Builder(this).setTitle("提示！").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            } else {
                onBackPressed();
            }
        }
    }

    public void onDismissPressed() {
        if (isRootFragment) {
            onLoginCancel();
        } else {
            fragmentManager.popBackStackImmediate(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (isRootFragment) {
                onLoginCancel();
            } else {
                fragmentManager.popBackStack();
            }
        }
    }

    public void onLoginCancel() {
        setResult(Activity.RESULT_CANCELED);
        if (cancelIntent != null) startActivity(cancelIntent);
        finish();
    }

    public void onLoginOk() {
        setResult(RESULT_OK);
        if (okIntent != null) startActivity(okIntent);
        //EventBus.getDefault().post(LOGIN_SUCCESS);
        //finish();
        updateProfile();
    }

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

                    /*JSONObject event = new JSONObject();
                    event.put("type", "update_profile");
                    EventBus.getDefault().post(event);*/
                    startActivity(new Intent(DuaActivityLogin.this, MainActivityNew_.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int status, String reason) {
                startActivity(new Intent(DuaActivityLogin.this, MainActivityNew_.class));
                finish();
            }
        });
    }

}
