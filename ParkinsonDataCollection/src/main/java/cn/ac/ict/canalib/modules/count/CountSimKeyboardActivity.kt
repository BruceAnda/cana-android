package cn.ac.ict.canalib.modules.count

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity2
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.common.MemoryData
import cn.ac.ict.canalib.common.extensions.toReport
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_count_simkeyboard.*
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * 数字记忆录入模块
 */
open class CountSimKeyboardActivity : AudioBaseActivity() {

    private lateinit var randomStr: String
    private var version: String? = null
    private var times: Int = 0
    private var isRight: Boolean = false
    private var result: ArrayList<String>? = null

    private var chars: Array<String>? = null

    private var source: IntArray? = null
    private var pool: SoundPool? = null
    private var poolMap: Map<String, Int>? = null
    private var isLoad: Boolean = false
    var isMusic: Boolean = false
    private var musicBtn: Button? = null
    private var isNotFull: Boolean = false
    private var level: Int = 0
    private val inputNum = 2
    private val maxTestNum = 6

    private lateinit var memoryData: MemoryData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_simkeyboard)

        init()
    }

    fun init() {

        isMusic = true
        isNotFull = true
        pool = SoundPool(11, AudioManager.STREAM_MUSIC, 0)
        poolMap = HashMap()
        source = intArrayOf(R.raw.count0, R.raw.count1, R.raw.count2, R.raw.count3, R.raw.count4, R.raw.count5, R.raw.count6, R.raw.count7, R.raw.count8, R.raw.count9, R.raw.delete)

        for (i in 0..10) {
            //   poolMap.put("index" + i, pool.load(this, source[i], 1));
        }
        pool!!.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (sampleId == poolMap!!.size) {
                isLoad = true
            }
        }

        count_voice!!.setOnClickListener {
            if (isMusic) {
                musicBtn!!.setBackgroundResource(R.drawable.count_keymusic_close)
                isMusic = false
            } else {
                musicBtn!!.setBackgroundResource(R.drawable.count_keymusic_open)
                isMusic = true
            }
        }

        chars = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", applicationContext.getString(R.string.count_sim_clear), application.getString(R.string.count_sim_delete))

        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        val screenWidth = size.x
        val oneQuarterWidth = (screenWidth * 0.30).toInt()

        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            btn_continue.text = "无法记住，结束测试"
        }

        // 正确答案
        randomStr = intent.getStringExtra("data")
        version = intent.getStringExtra("version")
        // 等级
        level = intent.getIntExtra("level", 3)
        randomStr = randomStr!!.substring(0, level)
        // 记忆模块数据
        memoryData = MemoryData(randomStr, level, "", "null")

        tv_grade!!.text = "请输入刚才屏幕上出现的" + level + "个数字"

        count_simkeyboard_tv.inputType = EditorInfo.TYPE_CLASS_PHONE

        // 确定按钮点击
        count_simkeyboard_confirmBtn!!.setOnClickListener {
            val str = count_simkeyboard_tv.text.toString().trim() // 去除左右空格
            // 答案正确
            if (str == randomStr) {
                // 第1次就回答正确
                if (times == 0) {
                    memoryData.reply = str
                } else {
                    // 第二次回答正确
                    memoryData.reply2 = str
                }
                if (level < 6) {
                    AlertDialog.Builder(this@CountSimKeyboardActivity).setTitle("提示！").setMessage("恭喜，回答正确!难度升级，下 将进 " + (level + 1) + "个数字的 记忆游戏!").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> saveAndContinue() }.show()
                } else {
                    saveAndContinue()
                }
                // 答案错误
            } else {
                times++
                AlertDialog.Builder(this@CountSimKeyboardActivity)
                        .setTitle("提示！")
                        .setMessage("回答错误！，您还有一次机会")
                        .setPositiveButton("确定", null)
                        .show()
                if (times >= inputNum) {
                    AlertDialog.Builder(this@CountSimKeyboardActivity)
                            .setTitle("提示！")
                            .setMessage("很遗憾，回答错误!再来 次吧，下 将进  " + level + "个数字的 记忆游戏!")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定") { dialog, which -> nextTest() }
                            .show()
                }
            }
        }

        result = ArrayList()

        //        confirmBtn = (Button)findViewById(R.id.count_simkeyboard_confirmBtn);
        for (i in chars!!.indices) {
            val btn = TextView(this)
            btn.setBackgroundResource(R.drawable.count_key_button_bg)
            btn.gravity = Gravity.CENTER
            btn.text = chars!![i]
            btn.setTextColor(resources.getColor(R.color.mainColor))
            if (i > 9) {
                btn.textSize = 30f
            } else {
                btn.textSize = 40f
            }
            btn.setOnClickListener(View.OnClickListener { arg0 ->
                var btnText = btn.text.toString().trim { it <= ' ' }

                if (!isNotFull && btnText.length < 2) {
                    Toast.makeText(this@CountSimKeyboardActivity, "有效数字是" + level + "位数，您的输入已满。\n请点击确定验证答案或删除重新输入。", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                val str = count_simkeyboard_tv!!.text.toString()
                if (btnText == "删除" || btnText == "delete") {
                    deleteText(arg0)
                    isNotFull = true
                } else if (btnText == "清空" || btnText == "clear") {
                    clearText(arg0)
                    isNotFull = true
                } else {
                    btnText = str + btnText
                    count_simkeyboard_tv!!.text = btnText
                    isNotFull = btnText.length <= level - 1
                }
            })
            val rowSpec = GridLayout.spec(i / 3)
            val colSpec = GridLayout.spec(i % 3)
            val params = GridLayout.LayoutParams(rowSpec, colSpec)
            params.setGravity(Gravity.FILL)
            coutn_gridlayout_root.addView(btn, params)
            params.width = oneQuarterWidth
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

        if (!isNotFull && btnText.length < 2) {
            Toast.makeText(this@CountSimKeyboardActivity, "有效数字是" + level + "位数，您的输入已满。\n请点击确定验证答案或删除重新输入。", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        val str = count_simkeyboard_tv!!.text.toString()

        btnText = str + btnText
        count_simkeyboard_tv!!.text = btnText
        isNotFull = btnText.length <= level - 1
    }

    private fun nextTest() {
        FileUtils.memory.data.add(memoryData)
        val intent = Intent(this@CountSimKeyboardActivity, CountGameActivity::class.java)
        intent.putExtra("level", level)
        startActivity(intent)
        finish()
    }

    private fun saveAndContinue() {
        var content = randomStr
        content += if (isRight) {
            ";1"
        } else {
            ";0"
        }
        content += if (version == "sound") {
            ";1"
        } else {
            ";0"
        }
        for (x in result!!) {
            content += ";" + x
        }

        level++
        if (level <= maxTestNum) {
            nextTest()
        } else {
            FileUtils.memory.data.add(memoryData)
            // 存储数据，数字模块不需要打分

            val data = "{\"score\":\"${0}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
            insertDB(data)
            var filePath = FileUtils.filePath
            // 单项测试
            if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                countSingleTestFinish(filePath)
            } else {
                countGlobleTestFinish(filePath)
            }
        }
    }

    /**
     * 把数据写入文件
     */
    private fun writeData(filePath: String) {
        doAsync {
            val data = JSON.toJSONString(FileUtils.memory)
            Log.i(ModuleHelper.MODULE_DATATYPE_MEMORY, data)
            FileUtils.writeToFile(data, filePath)
        }
    }

    /**
     * 把数据文件路径插入到数据库
     */
    private fun insertDB(mark: String) {
        database.use {
            // 历史数据
            val values = ContentValues()
            values.put(HistoryData.BATCH, FileUtils.batch)
            values.put(HistoryData.USERID, Dua.getInstance().currentDuaId)
            values.put(HistoryData.TYPE, ModuleHelper.MODULE_COUNT)
            values.put(HistoryData.FILEPATH, FileUtils.filePath)
            values.put(HistoryData.MARK, mark)
            values.put(HistoryData.ISUPLOAD, "0")
            values.put(HistoryData.OTHER, "{}")
            // 插入数据库
            insert(HistoryData.TABLE_NAME, null, values)
        }
    }

    fun clearText(v: View) {
        count_simkeyboard_tv!!.text = ""
        isNotFull = true
    }

    fun deleteText(v: View) {
        var str = count_simkeyboard_tv!!.text.toString().trim { it <= ' ' }
        if (str == "") {
            return
        }
        str = str.substring(0, str.length - 1)
        count_simkeyboard_tv!!.text = str
        isNotFull = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this@CountSimKeyboardActivity, ModelGuideActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        if (pool != null) {
            pool!!.release()
            pool = null
        }
        super.onDestroy()
    }

    fun next(view: View) {
        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {

        } else {
            startActivity(Intent(this, ModelGuideActivity2::class.java))
        }
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity::class.java))
        finish()
    }

    /**
     * 完成测试记忆模块完成
     */
    private fun countGlobleTestFinish(filePath: String) {
        AlertDialog.Builder(this@CountSimKeyboardActivity).setTitle("提示").setMessage("即将进入震颤测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
            // 进入下一项测试，保存数据
            startActivity(Intent(this, ModelGuideActivity2::class.java))
            writeData(filePath)
            finish()
        }).setCancelable(false).show()
    }

    /**
     * 记忆模块单项测试完成
     */
    private fun countSingleTestFinish(filePath: String) {
        //startActivity(Intent(this@CountSimKeyboardActivity, UploadActivity::class.java))
        toReport()
        writeData(filePath)
        finish()
    }
}
