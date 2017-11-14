package cn.ac.ict.canalib.modules.modulesnew.memory

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.MemoryData
import cn.ac.ict.canalib.common.extensions.toReport
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity2
import cn.ac.ict.canalib.utils.FileUtils
import kotlinx.android.synthetic.main.activity_memory_test.*
import java.util.*

/**
 * 数字记忆检测模块
 */
class MemoryTestActivity : AudioBaseActivity() {

    private val TAG = MemoryTestActivity::class.java.simpleName

    private var mLevel: Int = 3      // 等级从3开始直到6，回答机会2次，失败重新进行该等级测试
    private val mMaxLevel = 6
    private val mGuideAudioId = arrayOf(0, 1, 2, 12, 13, 14, 15)

    // audio资源id
    private val mAudioId = arrayOf(
            R.raw.count0,
            R.raw.count1,
            R.raw.count2,
            R.raw.count3,
            R.raw.count4,
            R.raw.count5,
            R.raw.count6,
            R.raw.count7,
            R.raw.count8,
            R.raw.count9,
            R.raw.delete,
            R.raw.clear,
            R.raw.memory_guide_one,
            R.raw.memory_guide_two,
            R.raw.memory_guide_three,
            R.raw.memory_guide_four
    )
    private var loadAudioCount = 0

    private val mHandler = Handler()
    private var random: Random = Random()
    private var count: Int = 0
    private var tempRandom: Int = 0
    private var delayMillis: Long = 200
    private var randomStr = ""

    private var alphaAnimation: AlphaAnimation = AlphaAnimation(1f, 1f)

    private var dataPool: MutableSet<Int> = HashSet()

    private lateinit var memoryData: MemoryData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_test)

        init()
    }

    private fun init() {
        loadAudio()
        initKeyboard()
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
                initAnimation()
            }
        }
    }

    fun releaseGenRandom() {
        dataPool.clear()
        count = 0
        randomStr = ""
    }

    fun releaseKeyboard() {
        // 记忆模块数据
        memoryData = MemoryData(randomStr, mLevel, "", "null")
        clearText()
        times = 0
    }

    /**
     * 初始化动画
     */
    private fun initAnimation() {
        alphaAnimation.duration = 2000
        alphaAnimation.fillAfter = true
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                tv_num.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                if (count < mLevel) {
                    genRandomNumber()
                } else {

                    // 本次level等级结束，开始输入
                    releaseKeyboard()
                    updateUI(View.INVISIBLE, View.VISIBLE)
                    soundID[mGuideAudioId[mLevel]]?.let { playSound(it) }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        genRandomNumber()
    }

    fun genRandomNumber() {
        tempRandom = random.nextInt(10)
        while (dataPool.contains(tempRandom)) {
            tempRandom = random.nextInt(10)
        }

        soundID[tempRandom]?.let { playSound(it) }

        dataPool.add(tempRandom)

        randomStr += tempRandom

        tv_num.text = "$tempRandom"

        mHandler.postDelayed({
            count++
            tv_num.startAnimation(alphaAnimation)
        }, delayMillis)
    }

    fun updateUI(numVisibility: Int, simKeybordvisibility: Int) {
        fl_memory_num.visibility = numVisibility
        sv_memory_keyboard.visibility = simKeybordvisibility
        tv_level.text = "请输入刚才屏幕上出现的${mLevel}个数字"
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity::class.java))
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
        AlertDialog.Builder(this).setTitle("提示").setMessage("测试失败,重新开始").setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@MemoryTestActivity, ModelGuideActivity::class.java))
            finish()
        }.setCancelable(false).show()
    }

    // 回答次数
    private var times: Int = 0
    private val inputNum = 1

    private var isLoad: Boolean = false
    private var isNotFull: Boolean = true

    private fun initKeyboard() {

        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            btn_continue.text = "无法记住，结束测试"
        }

        count_simkeyboard_tv.inputType = EditorInfo.TYPE_CLASS_PHONE

        // 确定按钮点击
        count_simkeyboard_confirmBtn.setOnClickListener {
            val str = count_simkeyboard_tv.text.toString().trim() // 去除左右空格
            // 答案正确
            if (str == randomStr) {
                // 第一次回答正确
                if (times == 0) {
                    memoryData.reply = str
                } else {    // 第二次回答正确
                    memoryData.reply2 = str
                }
                FileUtils.memory.data.add(memoryData)
                if (mLevel >= mMaxLevel) {
                    // 单项测试
                    finishMemory()
                } else {
                    AlertDialog.Builder(this@MemoryTestActivity).setTitle("提示！").setMessage("恭喜，回答正确!难度升级，下次将进 " + (mLevel + 1) + "个数字的记忆游戏!").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
                        nextTest()
                    }.show()
                }
                mLevel++
                // 答案错误
            } else {
                if (times < inputNum) {
                    memoryData.reply = str
                    AlertDialog.Builder(this@MemoryTestActivity)
                            .setTitle("提示！")
                            .setMessage("回答错误！，您还有一次机会")
                            .setPositiveButton("确定", null)
                            .show()
                } else {
                    AlertDialog.Builder(this@MemoryTestActivity)
                            .setTitle("提示！")
                            .setMessage("很遗憾，回答错误!再来次吧，下次将进  " + mLevel + "个数字的记忆游戏!")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定") { dialog, which -> nextTest() }
                            .show()
                }
                times++
            }
        }

        tv_count_0.setOnClickListener(countKeyClickListener)
        tv_count_1.setOnClickListener(countKeyClickListener)
        tv_count_2.setOnClickListener(countKeyClickListener)
        tv_count_3.setOnClickListener(countKeyClickListener)
        tv_count_4.setOnClickListener(countKeyClickListener)
        tv_count_5.setOnClickListener(countKeyClickListener)
        tv_count_6.setOnClickListener(countKeyClickListener)
        tv_count_7.setOnClickListener(countKeyClickListener)
        tv_count_8.setOnClickListener(countKeyClickListener)
        tv_count_9.setOnClickListener(countKeyClickListener)
    }

    private val countKeyClickListener = View.OnClickListener { p0 ->
        var btn = p0 as TextView
        var btnText = btn.text.toString().trim { it <= ' ' }

        soundID[btnText.toInt()]?.let { playSound(it) }

        if (!isNotFull && btnText.length < 2) {
            Toast.makeText(this@MemoryTestActivity, "有效数字是" + mLevel + "位数，您的输入已满。\n请点击确定验证答案或删除重新输入。", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        val str = count_simkeyboard_tv.text.toString()

        btnText = str + btnText
        count_simkeyboard_tv!!.text = btnText
        isNotFull = btnText.length <= mLevel - 1
    }

    /**
     * 下一个等级的测试
     */
    private fun nextTest() {
        releaseGenRandom()
        updateUI(View.VISIBLE, View.INVISIBLE)
        initAnimation()
    }

    fun clearText(v: View) {
        clearText()
    }

    private fun clearText() {
        count_simkeyboard_tv!!.text = ""
        isNotFull = true
        soundID[11]?.let { playSound(it) }
    }

    fun deleteText(v: View) {
        var str = count_simkeyboard_tv!!.text.toString().trim { it <= ' ' }
        if (str == "") {
            return
        }
        soundID[10]?.let { playSound(it) }
        str = str.substring(0, str.length - 1)
        count_simkeyboard_tv!!.text = str
        isNotFull = true
    }

    override fun onDestroy() {
        releaseSound()
        super.onDestroy()
    }

    /**
     * 结束的时候设置标志
     */
    private fun setFlag() {
        FileUtils.hasTestOne = true
        FileUtils.memory.level = mLevel
    }

    fun next(view: View) {
        finishMemory()
    }

    /**
     * 完成记忆模块测试
     */
    private fun finishMemory() {
        setFlag()
        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            countSingleTestFinish()
        } else {
            //countSingleTestFinish()
            countGlobleTestFinish()
        }
    }

    /**
     * 完成测试记忆模块完成
     */
    private fun countGlobleTestFinish() {
        AlertDialog.Builder(this@MemoryTestActivity).setTitle("提示").setMessage("即将进入震颤测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
            // 进入下一项测试，保存数据
            startActivity(Intent(this, ModelGuideActivity2::class.java))
            finish()
        }).setCancelable(false).show()
    }

    /**
     * 记忆模块单项测试完成
     */
    private fun countSingleTestFinish() {
        toReport()
        finish()
    }
}
