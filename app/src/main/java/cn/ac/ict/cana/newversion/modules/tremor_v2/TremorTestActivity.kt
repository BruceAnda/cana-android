package cn.ac.ict.cana.newversion.modules.tremor_v2

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.activities.FeedBackActivity
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.mode.AccData
import cn.ac.ict.cana.newversion.mode.GyroData
import cn.ac.ict.cana.newversion.utils.FileUtils
import kotlinx.android.synthetic.main.activity_stand_test.*
import java.util.*

class TremorTestActivity : YouMengBaseActivity() {

    private var isRight = true
    private val tips = arrayOf("右手", "预备", "预备", "预备", "开始", "左手", "预备", "预备", "预备", "开始")
    private var index = 0
    private var isAction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tremor_test_v2)

        isAction = intent.extras.getBoolean("isAction")
        CountDown(6000, 1000).start()
    }

    fun initTickTockView() {
        val start = Calendar.getInstance()
        start.add(Calendar.SECOND, -1)
        val end = Calendar.getInstance()
        end.add(Calendar.SECOND, 20)
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
                    CountDown(6000, 1000).start()
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
            if (isAction) {
                if (isRight) {
                    // 右手运动
                    FileUtils.tremor_rp_accdatalist.add(AccData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                } else {
                    // 左手运动
                    FileUtils.tremor_lp_accdatalist.add(AccData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                }
            } else {
                if (isRight) {
                    // 右手静止
                    FileUtils.tremor_rr_accdatalist.add(AccData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                } else {
                    // 左手静止
                    FileUtils.tremor_lr_accdatalist.add(AccData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                }
            }
        }
    }

    private val mGyroEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if (isAction) {
                if (isRight) {
                    // 右手运动
                    FileUtils.tremor_rp_gyrodatalist.add(GyroData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                } else {
                    // 左手运动
                    FileUtils.tremor_lp_gyrodatalist.add(GyroData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                }
            } else {
                if (isRight) {
                    // 右手静止
                    FileUtils.tremor_rr_gyrodatalist.add(GyroData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                } else {
                    // 左手静止
                    FileUtils.tremor_lr_gyrodatalist.add(GyroData(System.currentTimeMillis(), event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble()))
                }
            }
        }
    }

    fun initSensors() {
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.registerListener(mAccEventListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(mGyroEventListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME)
    }

    private fun stopSensors() {
        if (sm != null) {
            sm.unregisterListener(mAccEventListener)
            sm.unregisterListener(mGyroEventListener)
        }
    }

    inner class CountDown(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 右手倒计时结束
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

    fun finishTest(view: View) {
        val intent = Intent(this@TremorTestActivity, FeedBackActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_TREMOR)
        startActivity(intent)
        finish()
    }
}