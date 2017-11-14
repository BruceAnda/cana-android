package cn.ac.ict.canalib.modules.modulesnew.sound

import android.graphics.drawable.AnimationDrawable
import android.media.AudioFormat
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.common.extensions.toScore
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.utils.FileUtils
import kotlinx.android.synthetic.main.activity_sound_test2.*
import omrecorder.*
import java.io.File
import java.util.*

/**
 * 语言能力
 */
class SoundTestActivity : AppCompatActivity() {

    private var mLevel = 0
    private val mMaxLevel = 1

    private val mSoundTestTips = arrayOf("点击下面按钮开始录音", "正在录音...")
    private val mSoundBtnText = arrayOf("开始录音", "完成")

    private lateinit var recorderWav: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_test2)

        updateUI()

    }

    override fun onResume() {
        super.onResume()
        FileUtils.soundFilePath = "${filesDir}${File.separator}${UUID.randomUUID()}.wav"
    }

    private fun startRecorder() {
        iv_sound_mic.setBackgroundResource(R.drawable.mic_animation)
        (iv_sound_mic.background as AnimationDrawable).start()

        recorderWav = OmRecorder.wav(PullTransport.Default(mic(), PullTransport.OnAudioChunkPulledListener { audioChunk ->

        }), file())
        recorderWav.startRecording()
    }

    private fun updateUI() {
        if (mLevel <= mMaxLevel) {
            if (mLevel == 0) {
                iv_sound_mic.setBackgroundResource(R.drawable.microphone18)
            }
            tv_sound_test_tips.text = mSoundTestTips[mLevel]
            btn_sound.text = mSoundBtnText[mLevel]
        }
    }

    private fun mic(): PullableSource {
        return PullableSource.Default(
                AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 44100
                )
        )
    }

    private fun file(): File {
        return File(FileUtils.soundFilePath)
    }

    fun startRecord(view: View) {
        when (mLevel) {
            0 -> {
                startRecorder()
            }
            1 -> {
                finishTesting()
            }
        }
        mLevel++
        updateUI()
    }

    fun finishTesting() {
        recorderWav.stopRecording()
        toScore(ModuleHelper.MODULE_SOUND)
        finish()
    }
}
