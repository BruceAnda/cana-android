package cn.ac.ict.cana.newversion.modules.count

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.DataBaseHelper
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.contant.GlobleData
import cn.ac.ict.cana.newversion.db.database
import cn.ac.ict.cana.newversion.mode.CountData
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity2
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity
import cn.ac.ict.cana.newversion.pagers.ExamPageFragment
import cn.ac.ict.cana.newversion.provider.HistoryProvider
import cn.ac.ict.cana.newversion.utils.FileUtils
import cn.ac.ict.cana.newversion.utils.GzipUtil
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_count_simkeyboard.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.util.*

/**
 * 数字记忆录入模块
 */
open class CountSimKeyboardActivity : YouMengBaseActivity() {

    private var randomStr: String? = null
    private var version: String? = null
    private var nextet: TextView? = null
    private var nextbtn: TextView? = null
    private var times: Int = 0
    private var isRight: Boolean = false
    private var result: ArrayList<String>? = null

    private var gridLayout: GridLayout? = null
    private var tv: TextView? = null

    private var chars: Array<String>? = null

    private var source: IntArray? = null
    private var pool: SoundPool? = null
    private var poolMap: Map<String, Int>? = null
    private var isLoad: Boolean = false
    var isMusic: Boolean = false
    private var musicBtn: Button? = null
    private var isNotFull: Boolean = false
    private var grade: Int = 0
    private var tvGrade: TextView? = null
    private val inputNum = 2
    private val maxTestNum = 6
    private var countData: CountData? = null

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
        source = intArrayOf(R.raw.counts0, R.raw.counts1, R.raw.counts2, R.raw.counts3, R.raw.counts4, R.raw.counts5, R.raw.counts6, R.raw.counts7, R.raw.counts8, R.raw.counts9, R.raw.counts_del)

        for (i in 0..10) {
            //   poolMap.put("index" + i, pool.load(this, source[i], 1));
        }
        pool!!.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (sampleId == poolMap!!.size) {
                isLoad = true
            }
        }

        musicBtn = findViewById(R.id.count_voice) as Button
        musicBtn!!.setOnClickListener {
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

        randomStr = intent.getStringExtra("data")
        version = intent.getStringExtra("version")
        grade = intent.getIntExtra("grade", 3)
        randomStr = randomStr!!.substring(0, grade)
        tvGrade = findViewById(R.id.tv_grade) as TextView
        tvGrade!!.text = "请输入刚才屏幕上出现的" + grade + "个数字"
        nextet = findViewById(R.id.count_simkeyboard_tv) as TextView
        nextet!!.inputType = EditorInfo.TYPE_CLASS_PHONE
        nextbtn = findViewById(R.id.count_simkeyboard_confirmBtn) as TextView

        countData = CountData()
        // 确定按钮点击
        nextbtn!!.setOnClickListener {
            val str = nextet!!.text.toString().trim { it <= ' ' }
            result!!.add(str)
            if (str == randomStr) {
                isRight = true
                if (times == 0) {
                    countData!!.answer = str
                } else {
                    countData!!.reply = str
                }
                if (grade < 6) {
                    AlertDialog.Builder(this@CountSimKeyboardActivity).setTitle("提示！").setMessage("恭喜，回答正确!难度升级，下 将进 " + (grade + 1) + "个数字的 记忆游戏!").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> saveAndContinue() }.show()
                } else {
                    saveAndContinue()
                }
            } else {
                if (!isRight) {
                    times++
                    AlertDialog.Builder(this@CountSimKeyboardActivity)
                            .setTitle("提示！")
                            .setMessage("回答错误！，您还有一次机会")
                            .setPositiveButton("确定", null)
                            .show()
                    if (times >= inputNum) {
                        AlertDialog.Builder(this@CountSimKeyboardActivity)
                                .setTitle("提示！")
                                .setMessage("很遗憾，回答错误!再来 次吧，下 将进  " + grade + "个数字的 记忆游戏!")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定") { dialog, which -> nextTest() }
                                .show()
                    }
                }
            }
        }

        result = ArrayList()

        //        confirmBtn = (Button)findViewById(R.id.count_simkeyboard_confirmBtn);
        tv = findViewById(R.id.count_simkeyboard_tv) as TextView
        gridLayout = findViewById(R.id.coutn_gridlayout_root) as GridLayout
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
                    Toast.makeText(this@CountSimKeyboardActivity, "有效数字是" + grade + "位数，您的输入已满。\n请点击确定验证答案或删除重新输入。", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                val str = tv!!.text.toString()
                if (btnText == "删除" || btnText == "delete") {
                    deleteText(arg0)
                    isNotFull = true
                } else if (btnText == "清空" || btnText == "clear") {
                    clearText(arg0)
                    isNotFull = true
                } else {
                    btnText = str + btnText
                    tv!!.text = btnText
                    isNotFull = btnText.length <= grade - 1
                }
            })
            val rowSpec = GridLayout.spec(i / 3)
            val colSpec = GridLayout.spec(i % 3)
            val params = GridLayout.LayoutParams(rowSpec, colSpec)
            params.setGravity(Gravity.FILL)
            gridLayout!!.addView(btn, params)
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
            Toast.makeText(this@CountSimKeyboardActivity, "有效数字是" + grade + "位数，您的输入已满。\n请点击确定验证答案或删除重新输入。", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        val str = tv!!.text.toString()

        btnText = str + btnText
        tv!!.text = btnText
        isNotFull = btnText.length <= grade - 1
    }

    private fun nextTest() {
        FileUtils.countDataList.add(countData)
        val intent = Intent(this@CountSimKeyboardActivity, CountGameActivity::class.java)
        intent.putExtra("grade", grade)
        startActivity(intent)
        finish()
    }

    private fun saveAndContinue() {
        var content = randomStr
        if (isRight) {
            content += ";1"
        } else {
            content += ";0"
        }
        if (version == "sound") {
            content += ";1"
        } else {
            content += ";0"
        }
        for (x in result!!) {
            content += ";" + x
        }

        grade++
        if (grade <= maxTestNum) {
            nextTest()
        } else {
            FileUtils.countDataList.add(countData)
            /*val intent = Intent(this@CountSimKeyboardActivity, FeedBackActivity_v2::class.java)
            intent.putExtra("modelName", ModuleHelper.MODULE_COUNT)
            startActivity(intent)
            finish()*/
            // 存储数据，数字模块不需要打分
            val suffix = FileUtils.filePath.substring(FileUtils.filePath.lastIndexOf("."), FileUtils.filePath.length)
            val fileName = "Parkins/" + MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}") + suffix
            val data = "{\"score\":\"${0}\"," +
                    "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_medicine\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"switching_period\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"file\":\"${fileName}\"}"
            insertDB(data)
            var filePath = FileUtils.filePath
            // 单项测试
            if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                startActivity(Intent(this@CountSimKeyboardActivity, UploadActivity::class.java))
                writeData(filePath)
                finish()
            } else {
                AlertDialog.Builder(this@CountSimKeyboardActivity).setTitle("提示").setMessage("即将进入震颤测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    // 进入下一项测试，保存数据
                    startActivity(Intent(this, ModelGuideActivity2::class.java))
                    writeData(filePath)
                    finish()
                }).setCancelable(false).show()
            }
        }
    }

    /**
     * 把数据写入文件
     */
    private fun writeData(filePath: String) {
        doAsync {
            var jo = JSONObject()
            jo.put("type", "count")
            jo.put("data", JSON.toJSONString(FileUtils.countDataList))
            val toString = jo.toString()
            println("countData:" + toString)
            FileUtils.writeToFile(toString, filePath)

            GzipUtil.compressForZip(filePath, filePath + ".gz")
        }
    }

    /**
     * 把数据文件路径插入到数据库
     */
    private fun insertDB(mark: String) {
        /*val historyProvider = HistoryProvider(DataBaseHelper.getInstance(this))
        val history = History(Dua.getInstance().currentDuaId, ModuleHelper.MODULE_COUNT, FileUtils.filePath + ".gz", mark)
        historyProvider.InsertHistory(history)*/
        database.use {
            // 历史数据
            val values = ContentValues()
            values.put(cn.ac.ict.cana.newversion.db.bean.History.BATCH, FileUtils.batch)
            values.put(cn.ac.ict.cana.newversion.db.bean.History.USERID, Dua.getInstance().currentDuaId)
            values.put(cn.ac.ict.cana.newversion.db.bean.History.TYPE, ModuleHelper.MODULE_COUNT)
            values.put(cn.ac.ict.cana.newversion.db.bean.History.FILEPATH, FileUtils.filePath + ".gz")
            values.put(cn.ac.ict.cana.newversion.db.bean.History.MARK, mark)
            values.put(cn.ac.ict.cana.newversion.db.bean.History.ISUPLOAD, false)
            // 插入数据库
            insert(cn.ac.ict.cana.newversion.db.bean.History.TABLE_NAME, null, values)
        }
    }

    fun clearText(v: View) {
        tv!!.text = ""
        isNotFull = true
    }

    fun deleteText(v: View) {
        var str = tv!!.text.toString().trim { it <= ' ' }
        if (str == "") {
            return
        }
        str = str.substring(0, str.length - 1)
        tv!!.text = str
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
        startActivity(Intent(this, ModelGuideActivity2::class.java))
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity::class.java))
        finish()
    }
}
