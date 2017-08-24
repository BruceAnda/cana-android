package cn.ac.ict.cana.newversion.modules.tremor

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.TextView
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity2
import cn.pedant.SweetAlert.SweetAlertDialog

/**
 * 震颤主页面
 */
class TremorMainActivity : YouMengBaseActivity() {

    private val isRight = true
    private val isStatic = true
    private var tvTitle: TextView? = null
    private var modelMainPage: TextView? = null
    private var titiles: Array<String>? = null
    private var tips: Array<String>? = null

    internal var mp: MediaPlayer? = null
    private var grade: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tremor_main)
        titiles = resources.getStringArray(R.array.tremor_title)
        tips = resources.getStringArray(R.array.tremor_tips)
        initWidget()
    }

    /**
     * 开始测试
     */
    fun test(view: View) {
        if (mp != null) {
            mp!!.stop()
            mp!!.release()
            mp = null

        }
        val intent = Intent(this@TremorMainActivity, TremorTestingActivity::class.java)
        intent.putExtra("isRight", isRight)
        intent.putExtra("isStatic", isStatic)
        intent.putExtra("grade", grade)
        startActivity(intent)
        finish()
    }

    private fun initWidget() {
        tvTitle = findViewById(R.id.tv_termor_title) as TextView
        modelMainPage = findViewById(R.id.tv_tremor_tips) as TextView
        grade = intent.getIntExtra("grade", 0)
        modelMainPage!!.text = tips!![grade]
        tvTitle!!.text = titiles!![grade]


        val sm = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (acc == null || gyro == null) {
            val sweetAlertDialog = SweetAlertDialog(this@TremorMainActivity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.attention))
                    .setContentText(getString(R.string.not_support))
                    .setConfirmText(getString(R.string.btn_confirm))
                    .setConfirmClickListener {
                        //startActivity(Intent(this@TremorMainActivity, MainActivityNew_::class.java))
                    }
            sweetAlertDialog.show()
        } else {
            mp = MediaPlayer.create(applicationContext, R.raw.stand_guide)
            // mp.start();
        }
    }

    override fun onPause() {
        if (mp != null) {
            mp!!.stop()
            mp!!.release()
            mp = null

        }
        finish()
        super.onPause()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity2::class.java))
        finish()
    }
}
