package cn.ac.ict.canalib.base

import android.content.Context
import android.media.SoundPool
import android.os.Bundle
import android.support.v4.app.FragmentActivity

import com.umeng.analytics.MobclickAgent

import cn.ac.ict.canalib.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import android.media.AudioManager
import android.media.AudioAttributes
import android.os.Build


/**
 * 友盟统计BaseActivity
 */
open class BaseActivity : FragmentActivity() {

    open lateinit var mSoundPool: SoundPool
    open val soundID = HashMap<Int, Int>()

    open fun createSoundPool(maxStream: Int) {
        //当前系统的SDK版本大于等于21(Android 5.0)时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder = SoundPool.Builder()
            //传入音频数量
            builder.setMaxStreams(maxStream)
            //AudioAttributes是一个封装音频各种属性的方法
            val attrBuilder = AudioAttributes.Builder()
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build())
            mSoundPool = builder.build()
        } else {//设置最多可容纳2个音频流，音频的品质为5
            mSoundPool = SoundPool(maxStream, AudioManager.STREAM_SYSTEM, 5)
        }//当系统的SDK版本小于21时
    }

    fun playSound(soundId: Int) {
        mSoundPool.play(soundId, 1F, 1F, 0, 0, 1F)
    }

    fun pasueSound(soundId: Int) {
        mSoundPool.pause(soundId)
    }

    fun stopSound(soundId: Int) {
        mSoundPool.stop(soundId)
    }

    fun releaseSound() {
        mSoundPool.release()
    }

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
