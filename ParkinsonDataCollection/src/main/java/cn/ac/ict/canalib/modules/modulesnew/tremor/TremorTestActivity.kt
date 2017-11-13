package cn.ac.ict.canalib.modules.modulesnew.tremor

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
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.Tremor
import cn.ac.ict.canalib.common.TremorData
import cn.ac.ict.canalib.common.XYZ
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_tremor_test.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * 震颤检测
 */
class TremorTestActivity : AudioBaseActivity() {

    // log 使用
    private val TAG = TremorTestActivity::class.java.simpleName

    // 测试等级
    private var mLevel = 0   // 操作等级
    private var mMaxLevel = 3   // 最大等级

    // 测试提示语
    private val mTremorTitles = arrayOf(
            "右手静止性震颤",
            "左手静止性震颤",
            "右手动作性震颤",
            "左手动作性震颤"
    )
    private val mTremorTips = arrayOf(
            "点击开始测试后，右手手持设备，倒计时5秒钟后，测试正式开始，请保持选择的姿势，等待15秒钟。",
            "点击开始测试后，左手手持设备，倒计时5秒钟后，测试正式开始，请保持选择的姿势，等待15秒钟。",
            "点击开始测试后，右手手持设备，倒计时5秒钟后，测试正式开始，请保持选择的姿势，等待15秒钟。",
            "点击开始测试后，左手手持设备，倒计时5秒钟后，测试正式开始，请保持选择的姿势，等待15秒钟。"
    )
    // 倒计时提示语
    private val mTremorCountDownTips = arrayOf(
            "右手", "预备", "预备", "预备", "开始",
            "左手", "预备", "预备", "预备", "开始",
            "右手", "预备", "预备", "预备", "开始",
            "左手", "预备", "预备", "预备", "开始"
    )
    private var mTremorCountDownTipsIndex = 0
    // 倒计时
    private val mCountDown = CountDown(5000, 1000)

    // 测试提示语
    private val mTremorTestTips = arrayOf("右手", "左手", "右手", "左手")

    // audio资源id
    private val mAudioId = arrayOf(
            R.raw.tremor_test_guide_one,
            R.raw.tremor_test_guide_two,
            R.raw.tremor_test_guide_three,
            R.raw.tremor_test_guide_four,
            R.raw.hand_count_down_r,
            R.raw.hand_count_down_l
    )
    private var loadAudioCount = 0

    // 操作按钮
    private val mOperatorBtnText = arrayOf("开始测试", "开始测试", "开始测试", "开始测试", "完成震颤测试")


    /**
     * 页面创建的时候调用
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tremor_test)
        init()
    }

    /**
     * 初始界面
     */
    private fun init() {
        loadAudio()
        updateUI(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE)
    }

    /**
     * 加载语音
     */
    private fun loadAudio() {
        createSoundPool(mAudioId.size)
        for (i in 0 until mAudioId.size) {
            soundID.put(i, mSoundPool?.load(this, mAudioId[i], 1)!!)
        }
        mSoundPool?.setOnLoadCompleteListener { soundPool, sampleId, status ->
            loadAudioCount++
            if (loadAudioCount == mAudioId.size) {
                soundID[mLevel]?.let { playSound(it) }
            }
        }
    }

    /**
     * 根据Level更新UI的显示内容
     */
    private fun updateUI(tremorTipsVisibility: Int, tremorCountDownVisibility: Int, tremorTestCountDownVisibility: Int, operatorBtnVisibility: Int) {
        ll_tremor_tips.visibility = tremorTipsVisibility
        ll_tremor_count_down.visibility = tremorCountDownVisibility
        ll_tremor_test_count_down.visibility = tremorTestCountDownVisibility
        btn_tremor_operator.visibility = operatorBtnVisibility
        if (mLevel <= mMaxLevel) {
            tv_tremor_title.text = mTremorTitles[mLevel]
            tv_tremor_tips.text = mTremorTips[mLevel]
            ll_tremor_test_count_down_tips.text = mTremorTestTips[mLevel]
        }
        btn_tremor_operator.text = mOperatorBtnText[mLevel]
    }

    /**
     * 震颤检测操作
     */
    fun tremorOperator(view: View) {
        if (mLevel <= mMaxLevel) {
            updateUI(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.GONE)
            startCountDown()
        } else {
            toScore()
        }
    }

    /**
     * 开始倒计时
     */
    private fun startCountDown() {
        mCountDown.start()
        // 根据level播放倒计时语音
        // 停止当前正在播放的声音
        when (mLevel) {
            0, 2 -> {
                // 右手
                soundID[4]?.let { playSound(it) }
            }
            1, 3 -> {
                // 左手
                soundID[5]?.let { playSound(it) }
            }
        }
    }

    /**
     * 关闭倒计时
     */
    private fun cancelCountDown() {
        mCountDown.cancel()
    }

    /**
     * 倒计时类
     */
    inner class CountDown(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            vibrartor()
            initTickTockView()
            updateUI(View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.GONE)
            startSensors()
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_tremor_count_down.text = (millisUntilFinished / 1000).toString()
            tv_tremor_count_down_tips.text = mTremorCountDownTips[mTremorCountDownTipsIndex++]
        }
    }

    /**
     * 初始化测试倒计时
     */
    fun initTickTockView() {
        val start = Calendar.getInstance()
        start.add(Calendar.SECOND, -1)
        val end = Calendar.getInstance()
        end.add(Calendar.SECOND, 15)
        ll_tremor_test_count_down_ttv.setOnTickListener { timeRemainingInMillis ->
            if (timeRemainingInMillis <= 0) {
                /* vibrator.vibrate(pattern, -1)*/
                vibrartor()
                stopSensors()
                // 开始左腿倒计时
                // 倒计时完成后，更新测试level
                mLevel++
                if (mLevel <= 3) {
                    playGuideTips()
                    updateUI(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE)
                } else {
                    updateUI(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE)
                }
            }
            (timeRemainingInMillis / 1000 + 1).toString()
        }
        ll_tremor_test_count_down_ttv.start(start, end)
    }

    /**
     * 播放引导语
     */
    private fun playGuideTips() {
        soundID[mLevel]?.let { playSound(it) }
    }

    /**
     * 界面销毁的时候调用
     */
    override fun onDestroy() {
        super.onDestroy()
        cancelCountDown()
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
        when (mLevel) {
            0 -> {
                // 右手运动
                FileUtils.tremorRP.data = FileUtils.tremorData
            }
            1 -> {
                // 左手运动
                FileUtils.tremorLP.data = FileUtils.tremorData
            }
            2 -> {
                // 右手静止
                FileUtils.tremorRR.data = FileUtils.tremorData
            }
            3 -> {
                // 左手静止
                FileUtils.tremorLR.data = FileUtils.tremorData
            }
        }
        if (sm != null) {
            sm.unregisterListener(mAccEventListener)
            sm.unregisterListener(mGyroEventListener)
        }
    }

    /**
     * 跳转到评分界面
     */
    fun toScore() {
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

}
