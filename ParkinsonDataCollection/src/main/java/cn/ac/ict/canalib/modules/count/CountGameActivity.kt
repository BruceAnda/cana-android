package cn.ac.ict.canalib.modules.count

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
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import kotlinx.android.synthetic.main.activity_count_game.*
import java.util.*

/**
 * 产生数字的界面
 */
class CountGameActivity : AudioBaseActivity() {

    private val TAG = CountGameActivity::class.java.simpleName

    private val mHandler = Handler()
    private var random: Random? = null
    private var set: AnimationSet? = null
    private var myHandler: Handler? = null
    private var count: Int = 0
    private var tempRandom: Int = 0
    private var delayMillis: Int = 0
    private var randomStr = ""

    private var alphaAnimation: AlphaAnimation? = null

    private var dataPool: MutableSet<Int>? = null

    private var level: Int = 0      // 等级从3开始直到6，回答机会2次，失败重新进行该等级测试

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delayMillis = 200
        random = Random()
        setContentView(R.layout.activity_count_game)

        level = intent.getIntExtra("level", 3)

        set = AnimationSet(true)

        dataPool = HashSet()

        alphaAnimation = AlphaAnimation(1f, 1f)
        alphaAnimation!!.duration = 2000
        alphaAnimation!!.interpolator = AccelerateInterpolator()
        alphaAnimation!!.fillAfter = true

        set!!.addAnimation(alphaAnimation)

        alphaAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                tv_num.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                if (count < level) {
                    genRandomNumber()
                } else {

                    dataPool!!.clear()

                    val intent = Intent()
                    intent.setClass(this@CountGameActivity, CountSimKeyboardActivity::class.java)
                    intent.putExtra("data", randomStr)
                    intent.putExtra("level", level)
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

        tv_num.text = tempRandom.toString()

        myHandler = Handler()
        mHandler.postDelayed({
            count++
            tv_num.startAnimation(set)
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
}