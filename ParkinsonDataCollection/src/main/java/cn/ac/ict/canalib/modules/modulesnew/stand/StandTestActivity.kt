package cn.ac.ict.canalib.modules.modulesnew.stand

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
import cn.ac.ict.canalib.common.Stand
import cn.ac.ict.canalib.common.StandData
import cn.ac.ict.canalib.common.XYZ
import cn.ac.ict.canalib.common.extensions.toScore
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_stand_test2.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.File
import java.util.*

class StandTestActivity : AudioBaseActivity() {

    // log 使用
    private val TAG = StandTestActivity::class.java.simpleName

    // 测试等级
    private var mLevel = 0   // 操作等级
    private var mMaxLevel = 1   // 最大等级

    // 测试提示语
    private val mTremorTitles = arrayOf(
            "右腿站立平衡",
            "左腿站立平衡"
    )
    private val mTremorTips = arrayOf(
            "点击开始测试后，手持设备，双手交叉抱肩，倒计时5秒钟后，测试正式开始，请保持右腿站立姿势，单腿站立15秒钟。",
            "点击开始测试后，手持设备，双手交叉抱肩，倒计时5秒钟后，测试正式开始，请保持左腿站立姿势，单腿站立15秒钟。"
    )
    // 倒计时提示语
    private val mTremorCountDownTips = arrayOf(
            "右腿", "预备", "预备", "预备", "开始",
            "左腿", "预备", "预备", "预备", "开始"
    )
    private var mTremorCountDownTipsIndex = 0
    // 倒计时
    private val mCountDown = CountDown(5000, 1000)

    // 测试提示语
    private val mTremorTestTips = arrayOf("右腿", "左腿")

    // audio资源id
    private val mAudioId = arrayOf(
            R.raw.stand_test_guide_one,
            R.raw.stand_test_guide_two,
            R.raw.stand_count_down_r,
            R.raw.leg_count_down_l
    )
    private var loadAudioCount = 0

    // 操作按钮
    private val mOperatorBtnText = arrayOf("开始测试", "开始测试", "完成站立平衡测试")


    /**
     * 页面创建的时候调用
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stand_test2)
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
     * 站立平衡检测操作
     */
    fun tremorOperator(view: View) {
        if (mLevel <= mMaxLevel) {
            updateUI(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.GONE)
            startCountDown()
        } else {
            toScore(ModuleHelper.MODULE_STAND)
            FileUtils.hasTestFour = true
            finish()
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
            0 -> {
                // 右退
                soundID[2]?.let { playSound(it) }
            }
            1 -> {
                // 左腿
                soundID[3]?.let { playSound(it) }
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
                if (mLevel <= mMaxLevel) {
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
            FileUtils.standData.acc.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
        }
    }

    private val mGyroEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            FileUtils.standData.gyro.add(XYZ(System.currentTimeMillis(), x, y, z, Math.sqrt(x * x + y * y + z * z)))
        }
    }

    /**
     * 启动传感器
     */
    fun startSensors() {
        FileUtils.standData = StandData(ArrayList(), ArrayList())
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
                FileUtils.standR.data = FileUtils.standData
            }
            1 -> {
                FileUtils.standL.data = FileUtils.standData
            }
        }
        if (sm != null) {
            sm.unregisterListener(mAccEventListener)
            sm.unregisterListener(mGyroEventListener)
        }
    }
}
