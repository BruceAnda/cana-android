package cn.ac.ict.canalib.modules.sound

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.common.Tremor
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_sound_test.*
import java.io.File
import omrecorder.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.util.*


/**
 * 语言测试
 */
class SoundTestActivity : AppCompatActivity() {

    private var path: String? = null

    private var currentQustion = 1

    private lateinit var recorderWav: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_test)

        path = "${UUID.randomUUID()}.wav"

        iv_sound_mic.setBackgroundResource(R.drawable.mic_animation)
        (iv_sound_mic.background as AnimationDrawable).start()

        recorderWav = OmRecorder.wav(PullTransport.Default(mic(), PullTransport.OnAudioChunkPulledListener { audioChunk ->

        }), file())

    }

    override fun onResume() {
        super.onResume()
        recorderWav.startRecording()
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
        return File(filesDir, path)
    }

    fun nextQuestion(view: View) {
        when (currentQustion) {
            1 -> {
                tv_sound_title.text = "1、请大声读出一下文字"
                tv_sount_tip.text = "请您告诉我，您昨天晚上在什么地方吃的什么？"
                btn_sound.text = "完成"
            }
            2 -> {
                finishTesting()
            }
        }
        currentQustion++
    }

    fun finishTesting() {
        recorderWav.stopRecording()
        writeData()
        val intent = Intent(this@SoundTestActivity, ScoreActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_SOUND)
        startActivity(intent)
        finish()
    }


    /**
     * 把数据写入文件
     */
    private fun writeData() {
        doAsync {
            val other = JSONObject()
            val tone = tone()
            val volume = volume()
            other.put("tone", tone)
            other.put("volume", volume)

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "$filesDir${File.separator}$path", "0", ModuleHelper.MODULE_DATATYPE_SOUND, "", other.toString())
            insertDB(historyData)
        }
    }

    /**
     * 计算音量
     */
    private fun volume(): Float {

        return 0.5F
    }

    /**
     * 计算音调
     */
    private fun tone(): Float {

        return 0.5F
    }

    /**
     * 把数据文件路径插入到数据库
     */
    private fun insertDB(historyData: HistoryData) {
        database.use {
            // 历史数据
            val values = ContentValues()
            values.put(HistoryData.BATCH, historyData.batch)
            values.put(HistoryData.USERID, historyData.userID)
            values.put(HistoryData.TYPE, historyData.type)
            values.put(HistoryData.FILEPATH, historyData.filePath)
            values.put(HistoryData.MARK, historyData.mark)
            values.put(HistoryData.ISUPLOAD, historyData.isUpload)
            values.put(HistoryData.OTHER, historyData.other)
            // 插入数据库
            insert(HistoryData.TABLE_NAME, null, values)
        }
    }
}
