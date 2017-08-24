package cn.ac.ict.cana.newversion.modules.count

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.TextView
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity
import java.util.*

/**
 * 产生数字的界面
 */
class CountGameActivity : YouMengBaseActivity() {

    private val mHandler = Handler()
    private var tvnum: TextView? = null
    private var width: Int = 0
    private var height: Int = 0
    private var random: Random? = null
    private var tvnumWidth: Int = 0
    private var tvnumHeight: Int = 0
    private var set: AnimationSet? = null
    private var myHandler: Handler? = null
    private var count: Int = 0
    private var tempRandom: Int = 0
    private var delayMillis: Int = 0
    private var randomStr = ""

    private var alphaAnimation: AlphaAnimation? = null

    private var dataPool: MutableSet<Int>? = null

    private var grade: Int = 0      // 等级从3开始直到6，回答机会2次，失败重新进行该等级测试

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultDisplay = windowManager.defaultDisplay
        width = defaultDisplay.width - 300
        height = defaultDisplay.height - 300
        delayMillis = 200
        random = Random()
        setContentView(R.layout.activity_count_game)

        grade = intent.getIntExtra("grade", 3)

        tvnum = findViewById(R.id.tvnum) as TextView
        tvnumWidth = 200
        tvnumHeight = 200
        set = AnimationSet(true)

        dataPool = HashSet()

        alphaAnimation = AlphaAnimation(1f, 1f)
        alphaAnimation!!.duration = 2000
        alphaAnimation!!.interpolator = AccelerateInterpolator()
        alphaAnimation!!.fillAfter = true

        set!!.addAnimation(alphaAnimation)

        alphaAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                tvnum!!.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                if (count < grade) {
                    genRandomNumber()
                } else {

                    dataPool!!.clear()

                    val intent = Intent()
                    intent.setClass(this@CountGameActivity, CountSimKeyboardActivity::class.java)
                    intent.putExtra("data", randomStr)
                    intent.putExtra("grade", grade)
                    intent.putExtra("version", "picture")
                    startActivity(intent)
                    finish()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        count = 0
        genRandomNumber()
    }

    fun genRandomNumber() {
        tempRandom = random!!.nextInt(10)
        while (dataPool!!.contains(tempRandom)) {
            tempRandom = random!!.nextInt(10)
        }
        dataPool!!.add(tempRandom)

        randomStr += tempRandom

        tvnum!!.text = tempRandom.toString()

        myHandler = Handler()
        mHandler.postDelayed({
            count++
            tvnum!!.startAnimation(set)
        }, delayMillis.toLong())
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity::class.java))
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
        AlertDialog.Builder(this).setTitle("提示").setMessage("测试失败,重新开始").setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@CountGameActivity, ModelGuideActivity::class.java))
            finish()
        }.setCancelable(false).show()
    }

    companion object {
        private val TAG = CountGameActivity::class.java.simpleName
    }
}