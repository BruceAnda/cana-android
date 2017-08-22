package cn.ac.ict.cana.newversion.activities;

import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import com.pushlink.android.PushLink;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.base.YouMengBaseActivity;
import cn.ac.ict.cana.events.CheckedItemChangedEvent;
import cn.ac.ict.cana.events.ResponseEvent;
import cn.ac.ict.cana.helpers.ToastManager;
import cn.ac.ict.cana.newversion.adapter.MainAdapterNew;
import dmax.dialog.SpotsDialog;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
@EActivity(R.layout.activity_main)
public class MainActivityNew extends YouMengBaseActivity {

    @ViewById(R.id.ntb)
    NavigationTabBar navigationTabBar;
    @ViewById(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    //@Bean MainAdapter mMainAdapter;
    private MainAdapterNew mMainAdapterNew;
    @Bean
    ToastManager toastManager;
    Timer timer;
    int back = 1;
    //  public ArrayList<Call> callArrayList;
    private SpotsDialog mProgressDialog;
    private int success, failed;
    private LocalActivityManager mactivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mactivityManager = new LocalActivityManager(this, true);
        mactivityManager.dispatchCreate(savedInstanceState);
        mMainAdapterNew = new MainAdapterNew(getSupportFragmentManager());
    }

    @AfterViews
    public void init() {
        timer = new Timer(false);
        viewPager.setAdapter(mMainAdapterNew);
        //   callArrayList = new ArrayList<>();
        //int color = Color.parseColor("#68BED9");
        int color = getResources().getColor(R.color.mainColor2);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_test),
                        color
                ).title(getResources().getString(R.string.page_exam))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_user),
                        color
                ).title(getResources().getString(R.string.page_user))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_history),
                        color
                ).title(getResources().getString(R.string.page_history))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_settings),
                        color
                ).title(getResources().getString(R.string.page_setting))
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setBgColor(Color.parseColor("#F3F5F7"));
        navigationTabBar.setTitleSize(40);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseEvent(ResponseEvent event) {
        if (event.success) {
            success += 1;
        } else {
            failed += 1;
        }
        if (success + failed >= event.total) {
            finishUpload();
        }
    }

    private void finishUpload() {
        if (failed + success == 0) {
            return;
        }
        showProgressBar(false, "");
        if (failed > 0) {
            toastManager.show(String.format(Locale.CHINA, getResources().getString(R.string.upload_failed), success, failed));
        } else {
            toastManager.show(getResources().getString(R.string.upload_success));
        }
        success = 0;
        failed = 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckedItemChangedEvent(CheckedItemChangedEvent event) {
        Button uploadButton = (Button) findViewById(R.id.bt_upload);
        if (event.count == 0) {
            uploadButton.setEnabled(false);
        } else {
            uploadButton.setEnabled(true);
        }
    }

    public void cancelUpload() {
      /*  Log.d("cancelUpload", "Number of upload call: " + callArrayList.size());
        for (Call call : callArrayList) {
            call.cancel();
        }*/
    }

    //ProgressBar
    private void initProgressBar() {
        if (mProgressDialog == null) {
            mProgressDialog = new SpotsDialog(this, R.style.Custom);
//            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });
        }
    }

    public void showProgressBar(boolean show, String message) {
        initProgressBar();
        if (show) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Log.d("onBackPressed", back + "Function entered");
        if (back == 1) {
            Toast.makeText(MainActivityNew.this, getString(R.string.second_back), Toast.LENGTH_SHORT).show();
            back++;
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    back = 1;
                }
            };
            timer.schedule(tt, 2000);
        } else {
            cancelUpload();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PushLink.setCurrentActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // DuaActivityProfile duaActivityProfile = (DuaActivityProfile) mactivityManager.getActivity("one");
        //  duaActivityProfile.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // DuaActivityProfile duaActivityProfile = (DuaActivityProfile) mactivityManager.getActivity("one");
        // duaActivityProfile.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
