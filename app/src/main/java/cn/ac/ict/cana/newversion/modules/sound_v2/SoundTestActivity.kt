package cn.ac.ict.cana.newversion.modules.sound_v2

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.activities.FeedBackActivity
import cn.ac.ict.cana.newversion.utils.FileUtils
import kotlinx.android.synthetic.main.activity_sound_test.*

/**
 * 语言测试
 */
class SoundTestActivity : AppCompatActivity() {

    private lateinit var recorder: MediaRecorder
    private var path: String? = null

    private var currentQustion = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_test)

        path = FileUtils.filePath

        iv_sound_mic.setBackgroundResource(R.drawable.mic_animation)
        (iv_sound_mic.background as AnimationDrawable).start()

        prepareRecorder()
    }

    fun nextQuestion(view: View) {
        when (currentQustion) {
            1 -> {
                tv_sound_title.text = "1、请大声读出一下文字"
                tv_sount_tip.text = "今天你在哪吃的晚餐，吃了些什么？"
                btn_sound.text = "完成"
            }
            2 -> {
                finishTesting()
            }
        }
        currentQustion++
    }

    fun prepareRecorder() {
        try {
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setOutputFile(path)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder.prepare()
            recorder.start()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    fun finishTesting() {
        releaseRecorder()
        val intent = Intent(this@SoundTestActivity, FeedBackActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_SOUND)
        startActivity(intent)
        finish()
    }

    fun releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop()
                recorder.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
