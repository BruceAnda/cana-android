package cn.ac.ict.canalib.modules.guide

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.mode.History
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.Stride
import cn.ac.ict.canalib.common.StrideData
import cn.ac.ict.canalib.common.XYZ
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_model_guide5.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 行走平衡
 */
class ModelGuideActivity5 : AudioBaseActivity() {

    override fun onPause() {
        super.onPause()

        pasue()
    }

    override fun onStop() {
        super.onStop()

        stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide5)

        init()
    }

    private fun init() {
        handlerMenu()
        handlerSound()
    }

    private fun handlerMenu() {
        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            btn_pre.visibility = View.GONE
            btn_skip.visibility = View.GONE
        }
    }

    private fun handlerSound() {
        createMediaPlayer(R.raw.guide5)
    }

    override fun onResume() {
        super.onResume()
        handlerFile()
        play()
    }

    private fun handlerFile() {
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_STRIDE)
        FileUtils.stride = Stride("Stride", StrideData(ArrayList(), ArrayList()))
        FileUtils.strideData = StrideData(ArrayList(), ArrayList())
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity5, ModelGuideActivity4::class.java))
            finish()
        }.setCancelable(false).show()
    }

    fun next(view: View) {
        startActivity(Intent(this, ModelGuideActivity6::class.java))
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }

    /**
     * 开始测试
     */
    fun start(view: View) {
        FileUtils.hasTestFive = true
        // 启动倒计时
        stop()
        ll_controller_button.visibility = View.GONE
        tv_count_down.visibility = View.VISIBLE
        CountDown(5000, 1000).start()
    }

    private lateinit var sm: SensorManager

    private val mAccEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            FileUtils.strideData.acc.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
        }
    }

    private val mGyroEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            FileUtils.strideData.gyro.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
        }
    }

    private val mStepEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.values[0] == 1.0F) {
                mDetector++
            }
        }
    }

    private var mDetector: Float = 0.toFloat()//步行探测器

    fun initSensors() {
        FileUtils.strideData = StrideData(ArrayList(), ArrayList())
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.registerListener(mAccEventListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(mGyroEventListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(mStepEventListener,
                sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopSensors() {
        FileUtils.stride.data = FileUtils.strideData
        if (sm != null) {
            sm.unregisterListener(mAccEventListener)
            sm.unregisterListener(mGyroEventListener)
        }
    }

    /**
     * 写入Stride数据
     */
    private fun writeStrideData() {
        writeData(FileUtils.stride)
    }

    /**
     * 把数据写入文件
     */
    private fun writeData(stride: Stride) {
        doAsync {
            val other = JSONObject()
            other.put("step", mDetector)
            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "${filesDir}${File.separator}${UUID.randomUUID()}.txt", "0", stride.type, "", other.toString())
            val data = JSON.toJSONString(stride)
            Log.i("Stride", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    private val TAG = ModelGuideActivity5::class.java.simpleName

    /**
     * 把数据文件路径插入到数据库
     */
    private fun insertDB(historyData: HistoryData) {
        Log.i(TAG, "$historyData")
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
     * 完成测试
     */
    fun toScore(view: View) {
        stopSensors()
        writeStrideData()
        val intent = Intent(this@ModelGuideActivity5, ScoreActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_STRIDE)
        startActivity(intent)
        finish()
    }

    inner class CountDown(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 开始测试，记录数据
            tv_count_down.visibility = View.GONE
            ll_finish.visibility = View.VISIBLE
            tv_stride_tips.text = "坐在椅子上，将设备放于胸前，完成起立、行走、转身、行走、坐下"
            initSensors()
        }

        override fun onTick(p0: Long) {
            tv_count_down.text = (p0 / 1000).toString()
        }
    }
}
