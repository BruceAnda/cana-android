package cn.ac.ict.cana.newversion.activities

import android.app.LocalActivityManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast

import com.gigamole.navigationtabbar.ntb.NavigationTabBar
import com.pushlink.android.PushLink

import org.androidannotations.annotations.Bean
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.events.CheckedItemChangedEvent
import cn.ac.ict.cana.events.ResponseEvent
import cn.ac.ict.cana.helpers.ToastManager
import cn.ac.ict.cana.newversion.adapter.MainAdapterNew
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Author: saukymo
 * Date: 9/13/16
 */
open class MainActivityNew : YouMengBaseActivity() {


    private var mMainAdapterNew: MainAdapterNew? = null
    @Bean
    private var toastManager: ToastManager? = null
    private lateinit var timer: Timer
    internal var back = 1
    //  public ArrayList<Call> callArrayList;
    private var mProgressDialog: SpotsDialog? = null
    private var success: Int = 0
    private var failed: Int = 0
    private var mactivityManager: LocalActivityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mactivityManager = LocalActivityManager(this, true)
        mactivityManager!!.dispatchCreate(savedInstanceState)
        mMainAdapterNew = MainAdapterNew(supportFragmentManager)
        init()
    }

    fun init() {
        timer = Timer(false)
        vp_horizontal_ntb.adapter = mMainAdapterNew

        //   callArrayList = new ArrayList<>();
        //int color = Color.parseColor("#68BED9");
        val color = resources.getColor(R.color.mainColor2)

        val models = ArrayList<NavigationTabBar.Model>()
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_test),
                        color
                ).title(resources.getString(R.string.page_exam))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_user),
                        color
                ).title(resources.getString(R.string.page_user))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_history),
                        color
                ).title(resources.getString(R.string.page_history))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_settings),
                        color
                ).title(resources.getString(R.string.page_setting))
                        .build()
        )
        ntb.models = models
        ntb.setViewPager(vp_horizontal_ntb, 0)
        ntb.bgColor = Color.parseColor("#F3F5F7")
        ntb.titleSize = 40f
        if (intent.extras != null) {
            val int = intent.extras.getInt("page", 0)
            vp_horizontal_ntb.currentItem = int
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResponseEvent(event: ResponseEvent) {
        if (event.success) {
            success += 1
        } else {
            failed += 1
        }
        if (success + failed >= event.total) {
            finishUpload()
        }
    }

    private fun finishUpload() {
        if (failed + success == 0) {
            return
        }
        showProgressBar(false, "")
        if (failed > 0) {
            toastManager!!.show(String.format(Locale.CHINA, resources.getString(R.string.upload_failed), success, failed))
        } else {
            toastManager!!.show(resources.getString(R.string.upload_success))
        }
        success = 0
        failed = 0
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCheckedItemChangedEvent(event: CheckedItemChangedEvent) {
        val uploadButton = findViewById(R.id.bt_upload) as Button
        if (event.count == 0) {
            uploadButton.isEnabled = false
        } else {
            uploadButton.isEnabled = true
        }
    }

    fun cancelUpload() {
        /*  Log.d("cancelUpload", "Number of upload call: " + callArrayList.size());
        for (Call call : callArrayList) {
            call.cancel();
        }*/
    }

    //ProgressBar
    private fun initProgressBar() {
        if (mProgressDialog == null) {
            mProgressDialog = SpotsDialog(this, R.style.Custom)
            //            mProgressDialog.setCancelable(false);
            mProgressDialog!!.setOnCancelListener { onBackPressed() }
        }
    }

    fun showProgressBar(show: Boolean, message: String) {
        initProgressBar()
        if (show) {
            mProgressDialog!!.setMessage(message)
            mProgressDialog!!.show()
        } else {
            mProgressDialog!!.dismiss()
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onBackPressed() {
        Log.d("onBackPressed", back.toString() + "Function entered")
        if (back == 1) {
            Toast.makeText(this@MainActivityNew, getString(R.string.second_back), Toast.LENGTH_SHORT).show()
            back++
            val tt = object : TimerTask() {
                override fun run() {
                    back = 1
                }
            }
            timer.schedule(tt, 2000)
        } else {
            cancelUpload()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        PushLink.setCurrentActivity(this)
    }

    private val TAG = MainActivityNew::class.java.simpleName

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult")
        // DuaActivityProfile duaActivityProfile = (DuaActivityProfile) mactivityManager.getActivity("one");
        //  duaActivityProfile.onActivityResult(requestCode, resultCode, data);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // DuaActivityProfile duaActivityProfile = (DuaActivityProfile) mactivityManager.getActivity("one");
        // duaActivityProfile.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
