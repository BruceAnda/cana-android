package cn.ac.ict.canalib.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.FragmentActivity
import cn.ac.ict.canalib.R
import com.umeng.analytics.MobclickAgent
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

open class AudioBaseActivity : FragmentActivity() {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var sharedPreference: SharedPreferences
    private var audio_is_open: Boolean = true

    open var mSoundPool: SoundPool? = null
    open val soundID = HashMap<Int, Int>()
    open var mCurrentPalyAudioId: Int? = 0

    private lateinit var vibrator: Vibrator

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
        if (audio_is_open) {
            mCurrentPalyAudioId?.let { stopSound(it) }
            mCurrentPalyAudioId = mSoundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    fun pasueSound(soundId: Int) {
        if (audio_is_open) {
            mSoundPool?.pause(soundId)
        }
    }

    fun stopSound(soundId: Int) {
        if (audio_is_open && mSoundPool != null) {
            mSoundPool?.stop(soundId)
        }
    }

    fun releaseSound() {
        if (audio_is_open) {
            mSoundPool?.release()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    fun createMediaPlayer(resId: Int) {
        mMediaPlayer = MediaPlayer.create(this, resId)
    }

    fun play() {
        if (audio_is_open) {
            mMediaPlayer.start()
        }
    }

    fun pasue() {
        if (audio_is_open) {
            mMediaPlayer.pause()
        }
    }

    fun stop() {
        if (audio_is_open) {
            mMediaPlayer.stop()
        }
    }

    fun release() {
        if (audio_is_open) {
            mMediaPlayer.release()
        }
    }


    /**
     * 初始化操作
     */
    private fun init() {
        customFont()
        readAudioSetting()
        getVibrator()
    }

    private fun getVibrator() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    open fun vibrartor() {
        vibrator.vibrate(longArrayOf(100, 1000), -1)
    }

    /**
     * 读取声音设置
     */
    private fun readAudioSetting() {
        sharedPreference = getSharedPreferences("setting", Context.MODE_PRIVATE)
        audio_is_open = sharedPreference.getBoolean("audio_is_open", true)
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
        mCurrentPalyAudioId?.let { stopSound(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCurrentPalyAudioId?.let { stopSound(it) }
        releaseSound()
    }
}
