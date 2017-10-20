package cn.ac.ict.canalib.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity

import com.umeng.analytics.MobclickAgent

import cn.ac.ict.canalib.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * 友盟统计BaseActivity
 */
open class BaseActivity : FragmentActivity() {

    /**
     * 界面创建的时候调用这个方法
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    /**
     * 初始化操作
     */
    private fun init() {
        customFont()
    }

    /**
     * 自定义字体
     */
    private fun customFont() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MFShangHei_Noncommercial-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    /**
     * 自定义字体需要重载这个方法
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    /**
     * 界面处于可交互状态的时候调用这个方法
     */
    override fun onResume() {
        super.onResume()
        // 友盟的onResume
        MobclickAgent.onResume(this)
    }

    /**
     * 界面暂停的时候调用这个方法
     */
    override fun onPause() {
        super.onPause()
        // 友盟的onPause
        MobclickAgent.onPause(this)
    }
}
