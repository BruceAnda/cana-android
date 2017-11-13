package cn.ac.ict.canalib.modules.armdroop

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
import cn.ac.ict.canalib.common.ArmDroop
import cn.ac.ict.canalib.common.ArmDroopData
import cn.ac.ict.canalib.common.XYZ
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_arm_droop_test.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ArmDroopTestActivity : AudioBaseActivity() {

    private var isRight = true
    private val tips = arrayOf("右手", "预备", "预备", "预备", "开始", "左手", "预备", "预备", "预备", "开始")
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arm_droop_test)

        CountDown(5000, 1000).start()
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
                    CountDown(5000, 1000).start()
                } else {
                    ll_bottom.visibility = View.GONE
                    btn_finish.visibility = View.VISIBLE
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
            FileUtils.armDroopData.acc.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
            /*if (isRight) {
                FileUtils.accArmDroopRDatalist.add(AccData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
            } else {
                FileUtils.accArmDroopLDatalist.add(AccData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
            }*/
        }
    }

    private val mGyroEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            FileUtils.armDroopData.gyro.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
            /*if (isRight) {
                FileUtils.gyroArmDroopRDataList.add(GyroData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
            } else {
                FileUtils.gyroArmDroopLDataList.add(GyroData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
            }*/
        }
    }

    fun initSensors() {
        FileUtils.armDroopData = ArmDroopData(ArrayList(), ArrayList())
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.registerListener(mAccEventListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(mGyroEventListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME)
    }

    private fun stopSensors() {
        if (isRight) {
            FileUtils.armDroopR.data = FileUtils.armDroopData
        } else {
            FileUtils.armDroopL.data = FileUtils.armDroopData
        }
        if (sm != null) {
            sm.unregisterListener(mAccEventListener)
            sm.unregisterListener(mGyroEventListener)
        }
    }

    inner class CountDown(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 右腿倒计时结束
            ll_count_down.visibility = View.GONE
            ttv.visibility = View.VISIBLE
            tv_tip.visibility = View.VISIBLE

            initSensors()
            initTickTockView()
        }

        override fun onTick(p0: Long) {
            tv_count_down.text = (p0 / 1000).toString()
            tv_tips.text = tips[index++]
        }
    }

    /**
     * 写入Stand数据
     */
    private fun writeArmDroopData() {
        writeData(FileUtils.armDroopL)
        writeData(FileUtils.armDroopR)
    }

    /**
     * 把数据写入文件
     */
    private fun writeData(armDroop: ArmDroop) {
        doAsync {
            val other = JSONObject()
            val armDroopCount = armDroopCount()
            other.put("armDroopCount", armDroopCount)

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "${filesDir}${File.separator}${UUID.randomUUID()}.txt", "0", armDroop.type, "", other.toString())
            val data = JSON.toJSONString(armDroop)
            Log.i("ArmDroop", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    /**
     * 计算手臂下摆次数
     */
    private fun armDroopCount(): Float {

        return 10F
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
            // 插入数据库
            insert(HistoryData.TABLE_NAME, null, values)
        }
    }

    fun toScore(view: View) {
        writeArmDroopData()
        val intent = Intent(this@ArmDroopTestActivity, ScoreActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_ARM_DROOP)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ttv.stop()
    }
}