package cn.ac.ict.canalib.modules.tremor

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.common.Tremor
import cn.ac.ict.canalib.common.TremorData
import cn.ac.ict.canalib.common.XYZ
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_tremor_test_v2.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class TremorTestActivity : AudioBaseActivity() {

    private var isRight = true
    private val tips = arrayOf("右手", "预备", "预备", "预备", "开始", "左手", "预备", "预备", "预备", "开始")
    private var index = 0
    private var isAction = false
    // 倒计时
    private val countDown = CountDown(5000, 1000)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tremor_test_v2)

        isAction = intent.extras.getBoolean("isAction")
        countDown.start()
    }

    fun initTickTockView() {
        val start = Calendar.getInstance()
        start.add(Calendar.SECOND, -1)
        val end = Calendar.getInstance()
        end.add(Calendar.SECOND, 15)
        ttv.setOnTickListener { timeRemainingInMillis ->
            if (timeRemainingInMillis <= 0) {
                /* vibrator.vibrate(pattern, -1)*/
                stopSensors()
                // 开始左腿倒计时
                if (isRight) {
                    isRight = false
                    ttv.visibility = View.INVISIBLE
                    tv_tip.visibility = View.INVISIBLE
                    tv_tip.text = "左手"
                    ll_count_down.visibility = View.VISIBLE
                    countDown.start()
                } else {
                    if (isAction) {
                        btn_finish.visibility = View.VISIBLE
                    } else {
                        val intent = Intent(this@TremorTestActivity, TremorMainActivity::class.java)
                        intent.putExtra("isAction", true)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            (timeRemainingInMillis / 1000 + 1).toString()
        }
        ttv.start(start, end)
    }

    private lateinit var sm: SensorManager

    private val mAccEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            FileUtils.tremorData.acc.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
        }
    }

    private val mGyroEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            FileUtils.tremorData.gyro.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
        }
    }

    /**
     * 启动传感器
     */
    fun startSensors() {
        FileUtils.tremorData = TremorData(ArrayList(), ArrayList())
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.registerListener(mAccEventListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(mGyroEventListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME)
    }

    /**
     * 停止传感器
     */
    private fun stopSensors() {
        if (isAction) {
            if (isRight) {
                // 右手运动
                FileUtils.tremorRP.data = FileUtils.tremorData
            } else {
                // 左手运动
                FileUtils.tremorLP.data = FileUtils.tremorData
            }
        } else {
            if (isRight) {
                // 右手静止
                FileUtils.tremorRR.data = FileUtils.tremorData
            } else {
                // 左手静止
                FileUtils.tremorLR.data = FileUtils.tremorData
            }
        }
        if (sm != null) {
            sm.unregisterListener(mAccEventListener)
            sm.unregisterListener(mGyroEventListener)
        }
    }

    inner class CountDown(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 右手倒计时结束
            displayViews()

            startSensors()
            initTickTockView()
        }

        override fun onTick(p0: Long) {
            updateTips(p0)
        }


    }

    /**
     * 控制View的显示
     */
    private fun displayViews() {
        ll_count_down.visibility = View.GONE
        ttv.visibility = View.VISIBLE
        tv_tip.visibility = View.VISIBLE
    }

    /**
     * 更新倒计时和提示
     */
    private fun updateTips(p0: Long) {
        tv_count_down.text = (p0 / 1000).toString()
        tv_tips.text = tips[index++]
    }

    /**
     * 跳转到评分界面
     */
    fun toScore(view: View) {
        writeTremorData()
        val intent = Intent(this@TremorTestActivity, ScoreActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_TREMOR)
        startActivity(intent)
        finish()
    }

    /**
     * 写入tremor数据
     */
    private fun writeTremorData() {
        writeData(FileUtils.tremorLR)
        writeData(FileUtils.tremorLP)
        writeData(FileUtils.tremorRR)
        writeData(FileUtils.tremorRP)
    }

    /**
     * 把数据写入文件
     */
    private fun writeData(tremor: Tremor) {
        doAsync {

            val other = JSONObject()
            val frequency = frequency()
            val amplitude = amplitude()
            other.put("frequency", frequency)
            other.put("amplitude", amplitude)

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "$filesDir${File.separator}${UUID.randomUUID()}.txt", "0", tremor.type, "", other.toString())
            val data = JSON.toJSONString(tremor)
            Log.i("Tremor", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    /**
     * 计算振幅
     */
    private fun amplitude(): Float {

        return 33F
    }

    /**
     * 计算频率
     */
    private fun frequency(): Float {

        return 44F
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

    /**
     * 销毁的时候回收资源
     */
    override fun onDestroy() {
        super.onDestroy()
        // 关闭倒计时
        countDown.cancel()
        ttv.stop()
    }
}