package cn.ac.ict.canalib.common.audio

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import cn.ac.ict.canalib.R

/**
 * Created by zhaoliang on 2017/11/11.
 */
object audioManager {

    val mGuideAudioId = arrayOf(0, 1, 2, 3, 4, 5, 6, 7)
    private val mMemoryGuideAudioId = arrayOf(8, 9, 10, 11)
    private val mMemoryNumberAudioId = arrayOf(12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23)

    private val mAudioId = arrayOf(
            R.raw.guide,    // 数字记忆引导页引导语
            R.raw.guide2,   // 震颤情况引导页引导语
            R.raw.guide3,   // 语言能力引导页引导语
            R.raw.guide4,   // 站立平衡引导页引导语
            R.raw.guide5,   // 行走平衡引导页引导语
            R.raw.guide6,   // 手指灵敏引导页引导语
            R.raw.guide7,   // 面部表情引导页引导语
            R.raw.guide8    // 手臂下垂引导页引导语
    )

    private lateinit var sharedPreference: SharedPreferences
    private var audio_is_open: Boolean = true
    private lateinit var mContext: Application

    open var mSoundPool: SoundPool? = null
    open val mSoundID = HashMap<Int, Int>()
    open var mCurrentPalyAudioId: Int? = 0

    /**
     * 初始化声音管理器
     */
    fun init(context: Application) {
        this.mContext = context
        readSetting()

        createSoundPool(mAudioId.size)

        for (i in 0 until mAudioId.size) {
            mSoundID.put(i, mSoundPool?.load(mContext, mAudioId[i], 1)!!)
        }
    }

    /**
     * 读取设置
     */
    private fun readSetting() {
        sharedPreference = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE)
    }

    /**
     * 读取声音设置
     */
    private fun readAudioSetting() {
        audio_is_open = sharedPreference.getBoolean("audio_is_open", true)
    }

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

    /**
     * 播放声音
     */
    fun playSound(soundId: Int) {
        readAudioSetting()
        if (audio_is_open) {
            stopSound()
            mCurrentPalyAudioId = mSoundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    /**
     * 暂停声音
     */
    fun pasueSound() {
        readAudioSetting()
        if (audio_is_open) {
            mCurrentPalyAudioId?.let { mSoundPool?.pause(it) }
        }
    }

    /**
     * 停止声音
     */
    fun stopSound() {
        readAudioSetting()
        if (audio_is_open && mSoundPool != null) {
            mCurrentPalyAudioId?.let { mSoundPool?.stop(it) }
        }
    }

    /**
     * 释放资源
     */
    fun releaseSound() {
        readAudioSetting()
        if (audio_is_open) {
            mSoundPool?.release()
        }
    }
}