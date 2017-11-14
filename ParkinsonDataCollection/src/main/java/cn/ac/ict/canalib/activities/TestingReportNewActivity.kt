package cn.ac.ict.canalib.activities

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.common.*
import cn.ac.ict.canalib.constant.Constant
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.db.parser.HistoryParser
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.codbking.widget.DatePickDialog
import com.codbking.widget.bean.DateType
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.EntryXComparator
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.upload.UploadUtils
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_testing_report_new.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 测试报告
 */
class TestingReportNewActivity : AppCompatActivity() {

    private val TAG = Context.TELECOM_SERVICE::class.java.simpleName

    private val CODE_UPLOAD = 0
    private val CODE_UPLOAD_FINISH = 1
    private val CODE_DISMISS_DIAOLG = 2
    private val CODE_FINNISH_UI = 3

    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CODE_UPLOAD -> {
                    progressDialog!!.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
                    upload()
                }
                CODE_UPLOAD_FINISH -> {
                    progressDialog!!.setMessage("上传完成！")
                    sendEmptyMessageDelayed(CODE_DISMISS_DIAOLG, 1000)
                }
                CODE_DISMISS_DIAOLG -> {
                    if (progressDialog != null && progressDialog!!.isShowing)
                        progressDialog!!.dismiss()
                    ParkinsDataCollection.uiIntent.uploadFinish(this@TestingReportNewActivity)
                    sendEmptyMessage(CODE_FINNISH_UI)
                    // 检测以前有没有未上传的数据
                }
                CODE_FINNISH_UI -> {
                    finish()
                }
            }
        }
    }

    private var progressDialog: ProgressDialog? = null
    private var fileNum: Int = 0
    private var currentFile = 1

    private var numCount: Int = 0
    private var numTremor: Int = 0
    private var numSound: Int = 0
    private var numStand: Int = 0
    private var numStride: Int = 0
    private var numTapper: Int = 0
    private var numFace: Int = 0
    private var numArmDroop: Int = 0

    lateinit var list: List<HistoryData>


    private val mChartData = ArrayList<XYZ>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_report_new)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    fun init() {
        saveAndShowData()
    }

    /**
     * 检测项数据入库
     */
    private fun saveAndShowData() {
        //showFalseData()
        setRemarkCursor()
        showPatientInfoData()
        if (FileUtils.hasTestOne) {     // 保存数字记忆模块数据
            if (FileUtils.isTestingEnter) {
                writeMemoryData()
            }
            showMemoryData()
        }
        if (FileUtils.hasTestTwo) {     // 保存震颤模块数据
            if (FileUtils.isTestingEnter) {
                writeTremorData()
            }
            showTremorData()
        }
        if (FileUtils.hasTestThree) {   // 保存语言能力数据
            if (FileUtils.isTestingEnter) {
                writeSoundData()
            }
            showSoundData()
        }
        if (FileUtils.hasTestFour) {    // 保存站立平衡数据
            if (FileUtils.isTestingEnter) {
                writeStandData()
            }
            showStandData()
        }
        if (FileUtils.hasTestFive) {    // 保存行走平衡数据
            if (FileUtils.isTestingEnter) {
                writeStrideData()
            }
            showStrideData()
        }
        if (FileUtils.hasTestSix) {     // 保存Tapping数据
            if (FileUtils.isTestingEnter) {
                writeTappingData()
            }
            showTappingData()
        }
        if (FileUtils.hasTestSeven) {     // 保存面具脸数据
            if (FileUtils.isTestingEnter) {
                writeFaceData()
            }
            showFaceData()
        }
        if (FileUtils.hasTestEight) {       // 保存手臂下垂数据
            if (FileUtils.isTestingEnter) {
                writeArmDroopData()
            }
            showArmDroopData()
        }
    }

    private fun writeArmDroopData() {
        writeArmDroopData(FileUtils.armDroopL)
        writeArmDroopData(FileUtils.armDroopR)
    }

    /**
     * 把数据写入文件
     */
    private fun writeArmDroopData(armDroop: ArmDroop) {
        doAsync {
            val other = JSONObject()
            val armDroopCount = armDroopCount()
            other.put("armDroopCount", armDroopCount)

            var score = "0"
            when (armDroop.type) {
                ModuleHelper.MODULE_DATATYPE_ARMDROOP_R -> {
                    score = FileUtils.armDroopRScore
                }
                ModuleHelper.MODULE_DATATYPE_ARMDROOP_L -> {
                    score = FileUtils.armDroopLScore
                }
            }

            val armDroopMark = "{\"score\":\"${score}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "${filesDir}${File.separator}${UUID.randomUUID()}.txt", "0", armDroop.type, armDroopMark, other.toString())
            val data = JSON.toJSONString(armDroop)
            Log.i("ArmDroop", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    /**
     * 计算手臂下摆次数
     */
    private fun armDroopCount(): Float {

        return 10F
    }

    private fun writeFaceData() {
        doAsync {
            val other = JSONObject()
            val blinkTimes = blinkTimes()
            val smileAngle = smileAngle()
            other.put("blinkTimes", blinkTimes)
            other.put("smileAngle", smileAngle)

            val faceMark = "{\"score\":\"${FileUtils.faceScore}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", FileUtils.faceFilePath, "0", ModuleHelper.MODULE_DATATYPE_FACE, faceMark, other.toString())
            insertDB(historyData)
        }
    }

    /**
     * 计算嘴角微笑角度
     */
    private fun smileAngle(): Float {

        return 30F
    }


    /**
     * 计算瞬目次数
     */
    private fun blinkTimes(): Float {

        return 3F
    }

    private fun writeTappingData() {
        writeTappingData(FileUtils.tappingR)
        writeTappingData(FileUtils.tappingL)
    }

    /**
     * 把数据写入文件
     */
    private fun writeTappingData(tapper: Tapping) {
        doAsync {
            var successNum: Float = 0F
            var firstBtn = ""
            var secondBtn = ""
            var totalNum: Float = 0F
            // 计算交替比率
            totalNum = tapper.data.size.toFloat()
            val first = tapper.data[0].btn
            // 判断第一个是L的话，后面每个奇数都是L
            if ("L" == first) {
                firstBtn = "L"
                secondBtn = "R"
            } else {
                firstBtn = "R"
                secondBtn = "L"
            }

            for (i in 0 until totalNum.toInt()) {
                val item = tapper.data[i]
                if (i % 2 == 0) {
                    // 偶数 0 2 4 位置的都是L
                    if (item.btn == firstBtn) {
                        successNum++
                    }
                } else {
                    // 奇数 1 3 5 位置的都是R
                    if (item.btn == secondBtn) {
                        successNum++
                    }
                }
            }
            val alternatingRatio = successNum / totalNum

            // 计算平均速率
            val firstTime = tapper.data[0].time
            val lastTime = tapper.data[totalNum.toInt() - 1].time

            val avgspeed = totalNum / (lastTime - firstTime)

            val other = JSONObject()
            other.put("alternatingRatio", alternatingRatio)
            other.put("avgspeed", avgspeed)

            var score = "0"
            when (tapper.type) {
                ModuleHelper.MODULE_DATATYPE_TAPPING_R -> {
                    score = FileUtils.tappingRScore
                }
                ModuleHelper.MODULE_DATATYPE_TAPPING_L -> {
                    score = FileUtils.tappingLScore
                }
            }

            val tappingMark = "{\"score\":\"${score}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"


            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "$filesDir${File.separator}${UUID.randomUUID()}.txt", "0", tapper.type, tappingMark, other.toString())
            val data = JSON.toJSONString(tapper)
            Log.i(TAG, data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    private fun writeStrideData() {
        writeStrideData(FileUtils.stride)
    }

    /**
     * 把数据写入文件
     */
    private fun writeStrideData(stride: Stride) {
        doAsync {
            val other = JSONObject()
            other.put("step", FileUtils.step)

            val strideMark = "{\"score\":\"${FileUtils.strideScore}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "${filesDir}${File.separator}${UUID.randomUUID()}.txt", "0", stride.type, strideMark, other.toString())
            val data = JSON.toJSONString(stride)
            Log.i(TAG, data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    private fun writeStandData() {
        writeStandData(FileUtils.standR)
        writeStandData(FileUtils.standL)
    }

    /**
     * 把数据写入文件
     */
    private fun writeStandData(stand: Stand) {
        doAsync {
            val other = JSONObject()
            val variance = variance()
            val time = time()
            other.put("variance", variance)
            other.put("time", time)

            val standMark = "{\"score\":\"${FileUtils.standScore}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "${filesDir}${File.separator}${UUID.randomUUID()}.txt", "0", stand.type, standMark, other.toString())
            val data = JSON.toJSONString(stand)
            Log.i("Stand", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    /**
     * 计算时间
     */
    private fun time(): Float {

        return 66F
    }

    private fun variance(): Double {
        return 55.5
    }

    /**
     * 计算方差
     * 方差s^2=[(x1-x)^2 +...(xn-x)^2]/n
     */
    private fun variance(x: DoubleArray): Double {
        val m = x.size
        var sum = 0.0
        for (i in 0 until m) {//求和
            sum += x[i]
        }
        val dAve = sum / m//求平均值
        var dVar = 0.0
        for (i in 0 until m) {//求方差
            dVar += (x[i] - dAve) * (x[i] - dAve)
        }
        return dVar / m
    }

    private fun writeSoundData() {
        doAsync {
            val other = JSONObject()
            val tone = tone()
            val volume = volume()
            other.put("tone", tone)
            other.put("volume", volume)

            val soundMark = "{\"score\":\"${FileUtils.soundScore}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", FileUtils.soundFilePath, "0", ModuleHelper.MODULE_DATATYPE_SOUND, soundMark, other.toString())
            insertDB(historyData)
        }
    }

    /**
     * 计算音量
     */
    private fun volume(): Float {

        return 0.5F
    }

    /**
     * 计算音调
     */
    private fun tone(): Float {

        return 0.5F
    }

    private fun writeTremorData() {
        writeTremorData(FileUtils.tremorLR)
        writeTremorData(FileUtils.tremorLP)
        writeTremorData(FileUtils.tremorRR)
        writeTremorData(FileUtils.tremorRP)
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
     * 写入震颤模块数据
     */
    private fun writeTremorData(tremor: Tremor) {
        doAsync {

            val other = JSONObject()
            val frequency = frequency()
            val amplitude = amplitude()
            other.put("frequency", frequency)
            other.put("amplitude", amplitude)

            var score = "0"
            when (tremor.type) {
                ModuleHelper.MODULE_DATATYPE_TREMOR_RR -> {
                    score = FileUtils.tremorRRScore
                }
                ModuleHelper.MODULE_DATATYPE_TREMOR_LR -> {
                    score = FileUtils.tremorLRScore
                }
                ModuleHelper.MODULE_DATATYPE_TREMOR_RP -> {
                    score = FileUtils.tremorRPScore
                }
                ModuleHelper.MODULE_DATATYPE_TREMOR_LP -> {
                    score = FileUtils.tremorLPScore
                }
            }

            val tremorMark = "{\"score\":\"${score}\"," +
                    "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                    "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                    "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                    "\"filever\":\"${1}\"," +
                    "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "$filesDir${File.separator}${UUID.randomUUID()}.txt", "0", tremor.type, tremorMark, other.toString())
            val data = JSON.toJSONString(tremor)
            Log.i("Tremor", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }


    /**
     * 写入记忆模块数据
     */
    private fun writeMemoryData() {
        writeMemoryData(FileUtils.memory)
    }

    /**
     * 写入记忆模块数据
     */
    private fun writeMemoryData(memory: Memory) {
        val memoryMark = "{\"score\":\"${0}\"," +
                "\"doctor\":\"${FileUtils.DOCTOR}\"," +
                "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                "\"filever\":\"${1}\"," +
                "\"file\":\"Parkins/${MD5.md5("${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
        val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "$filesDir${File.separator}${UUID.randomUUID()}.txt", "0", memory.type, memoryMark, "")
        insertDB(historyData)
        doAsync {
            val data = JSON.toJSONString(memory)
            Log.i(TAG, data)
            FileUtils.writeToFile(data, historyData.filePath)
        }
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
            // 添加到上传列表
            addToUploadList(historyData)
        }
    }

    private var dataList = ArrayList<HistoryData>()
    /**
     * 添加到上传列表
     */
    private fun addToUploadList(historyData: HistoryData) {
        dataList.add(historyData)
    }

    /**
     * 展示假数据，测试用
     */
    private fun showFalseData() {
        mChartData.add(XYZ(System.currentTimeMillis(), -1.3052856922149658, 4.150568962097168, 8.250843048095703, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), -5.170847415924072, 1.4202466011047363, 5.075046539306641, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), -8.150251388549805, 0.1293310672044754, 7.949069976806641, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), -9.007668495178223, 0.06227051094174385, 10.16446304321289, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), -7.292834758758545, 0.3544628918170929, 11.508069038391113, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), -5.01038122177124, 1.686093807220459, 10.64346694946289, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), -1.0705738067626953, 4.033213138580322, 9.189690589904785, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), 5.077441692352295, 6.804250717163086, 8.832832336425781, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), 9.2112455368042, 9.048383712768555, 10.05908203125, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), 8.861572265625, 10.245894432067871, 10.183623313903809, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), 6.68689489364624, 9.987232208251953, 8.559800148010254, 9.327775964919754))
        mChartData.add(XYZ(System.currentTimeMillis(), 4.263134956359863, 8.22449779510498, 5.771997451782227, 9.327775964919754))
    }

    /**
     * 设置备注光标
     */
    private fun setRemarkCursor() {
        et_testing_report_remark.isCursorVisible = false
        et_testing_report_remark.setOnClickListener {
            et_testing_report_remark.isCursorVisible = true
        }
    }

    /**
     * 展示病人数据
     */
    private fun showPatientInfoData() {
        cv_testing_patient_info.visibility = View.VISIBLE
        tv_testing_report_patient_name.text = "姓名：${FileUtils.PATIENT_NAME}"
        tv_testing_report_patient_sex.text = "性别：${FileUtils.PATIENT_SEX}"
        tv_testing_report_patient_age.text = "年龄：${FileUtils.PATIENT_AGE}"
        tv_testing_report_patient_medicine.text = "使用药物：${FileUtils.PATIENT_MEDICINE}"
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        tv_testing_report_patient_last_use_medicine_time.text = "上次用药时间：${year}-${month + 1}-${day} ${hour}:${minute}"
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        tv_testing_report_patient_last_use_medicine_time.setOnClickListener {
            val dialog = DatePickDialog(this@TestingReportNewActivity)
            // 设置上下年份限制
            dialog.setYearLimt(5)
            // 设置标题
            dialog.setTitle("选择上次用药时间")
            //设置类型
            dialog.setType(DateType.TYPE_ALL)
            //设置消息体的显示格式，日期格式
            dialog.setMessageFormat("yyyy-MM-dd HH:mm")
            //设置选择回调
            dialog.setOnChangeLisener(null)
            //设置点击确定按钮回调
            dialog.setOnSureLisener({ date ->
                tv_testing_report_patient_last_use_medicine_time.text = "上次用药时间：${format.format(date)}"
            })
            dialog.show()
        }
        val open = arrayOf("-1", "0", "1")
        sp_switching_period.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                FileUtils.SWITCHING_PERIOD = open[position]
            }
        }
    }

    private fun showArmDroopData() {
        cv_armdroop_r_testing_report.visibility = View.VISIBLE
        cv_armdroop_l_testing_report.visibility = View.VISIBLE
        // 右手手臂下垂
        initLineChar(arm_droop_r_acc, "加速度")
        addEntry(FileUtils.armDroopR.data.acc, arm_droop_r_acc)
        initLineChar(arm_droop_r_gyro, "陀螺仪")
        addEntry(FileUtils.armDroopR.data.gyro, arm_droop_r_gyro)

        // 左手手臂下垂
        initLineChar(arm_droop_l_acc, "加速度")
        addEntry(FileUtils.armDroopL.data.acc, arm_droop_l_acc)
        initLineChar(arm_droop_l_gyro, "陀螺仪")
        addEntry(FileUtils.armDroopL.data.gyro, arm_droop_l_gyro)

        tv_armdroop_r_testing_report_upds.text = "UPDS:${FileUtils.armDroopRScore}"
        tv_armdroop_l_testing_report_upds.text = "UPDS:${FileUtils.armDroopLScore}"
    }

    private fun showFaceData() {
        cv_face_testing_report.visibility = View.VISIBLE

        tv_face_testing_report_upds.text = "UPDS:${FileUtils.faceScore}"
    }

    private fun showTappingData() {
        cv_tapping_r_testing_report.visibility = View.VISIBLE
        cv_tapping_l_testing_report.visibility = View.VISIBLE

        tv_tapping_r_testing_report_upds.text = "UPDS:${FileUtils.tappingRScore}"
        tv_tapping_l_testing_report_upds.text = "UPDS:${FileUtils.tappingLScore}"
    }

    private fun showStrideData() {
        cv_stride_testing_report.visibility = View.VISIBLE

        // 行走平衡
        initLineChar(stride_chart_acc, "加速度")
        addEntry(FileUtils.strideData.acc, stride_chart_acc)
        initLineChar(stride_chart_gyro, "陀螺仪")
        addEntry(FileUtils.strideData.gyro, stride_chart_gyro)
        tv_stride_testing_report_upds.text = "UPDS:${FileUtils.strideScore}"
    }

    private fun showStandData() {
        cv_stand_r_testing_report.visibility = View.VISIBLE
        cv_stand_l_testing_report.visibility = View.VISIBLE
        // 右腿站立
        initLineChar(stand_chart_r_acc, "加速度")
        addEntry(FileUtils.standR.data.acc, stand_chart_r_acc)
        initLineChar(stand_chart_r_gyro, "陀螺仪")
        addEntry(FileUtils.standR.data.gyro, stand_chart_r_gyro)
        tv_stand_r_upds.text = "UPDS:${FileUtils.standScore}"
        // 左腿站立
        initLineChar(stand_chart_l_acc, "加速度")
        addEntry(FileUtils.standL.data.acc, stand_chart_l_acc)
        initLineChar(stand_chart_l_gyro, "陀螺仪")
        addEntry(FileUtils.standL.data.gyro, stand_chart_l_gyro)
        tv_stand_l_upds.text = "UPDS:${FileUtils.standScore}"

    }

    private fun showSoundData() {
        cv_sound_testing_report.visibility = View.VISIBLE
        // initLineChar(sound_chart, "语言能力")
        // addEntry(FileUtils.tremorRR.data.acc, sound_chart)

        tv_sound_testing_report_upds.text = "${FileUtils.soundScore}"
    }

    private fun showTremorData() {
        cv_tremor_rr_testing_report.visibility = View.VISIBLE
        cv_tremor_lr_testing_report.visibility = View.VISIBLE
        cv_tremor_rp_testing_report.visibility = View.VISIBLE
        cv_tremor_lp_testing_report.visibility = View.VISIBLE
        // 右手静止性震颤
        initLineChar(tremor_chart_rr_acc, "加速度")
        addEntry(FileUtils.tremorRR.data.acc, tremor_chart_rr_acc)
        initLineChar(tremor_chart_rr_gyro, "陀螺仪")
        addEntry(FileUtils.tremorRR.data.gyro, tremor_chart_rr_gyro)
        tv_tremor_rr_upds.text = "UPDS:${FileUtils.tremorRRScore}"
        // 左手静止性震颤
        initLineChar(tremor_chart_lr_acc, "加速度")
        addEntry(FileUtils.tremorLR.data.acc, tremor_chart_lr_acc)
        initLineChar(tremor_chart_lr_gyro, "陀螺仪")
        addEntry(FileUtils.tremorLR.data.gyro, tremor_chart_lr_gyro)
        tv_tremor_lr_upds.text = "UPDS:${FileUtils.tremorLRScore}"
        // 右手动作性震颤
        initLineChar(tremor_chart_rp_acc, "加速度")
        addEntry(FileUtils.tremorRP.data.acc, tremor_chart_rp_acc)
        initLineChar(tremor_chart_rp_gyro, "陀螺仪")
        addEntry(FileUtils.tremorRP.data.gyro, tremor_chart_rp_gyro)
        tv_tremor_rp_upds.text = "UPDS:${FileUtils.tremorRPScore}"
        // 左手动作性震颤
        initLineChar(tremor_chart_lp_acc, "加速度")
        addEntry(FileUtils.tremorLP.data.acc, tremor_chart_lp_acc)
        initLineChar(tremor_chart_lp_gyro, "陀螺仪")
        addEntry(FileUtils.tremorLP.data.gyro, tremor_chart_lp_gyro)
        tv_tremor_lp_upds.text = "UPDS:${FileUtils.tremorLPScore}"
    }

    /**
     * 展示记忆模块数据
     */
    private fun showMemoryData() {
        cv_testing_report_memory.visibility = View.VISIBLE
        tv_memory_level.text = "记忆等级：${FileUtils.memory.level}"
    }

    /**
     * 查询数据库
     */
    private fun findHistory() {
        database.use {

            // 查询本次测试
            list = select(HistoryData.TABLE_NAME).where("(${HistoryData.BATCH} = {batch} and ${HistoryData.ISUPLOAD} = {isupload})",
                    "batch" to FileUtils.batch,
                    "isupload" to "0").parseList(HistoryParser<HistoryData>())

            // list = select(HistoryData.TABLE_NAME).whereSimple(HistoryData.BATCH + " = ?", FileUtils.batch).parseList(HistoryParser<HistoryData>())
            fileNum = list.size
            for (history in list) {
                when {
                    history.type.contains(ModuleHelper.MODULE_COUNT) -> numCount++
                    history.type.contains(ModuleHelper.MODULE_TREMOR) -> numTremor++
                    history.type.contains(ModuleHelper.MODULE_DATATYPE_SOUND) -> numSound++
                    history.type.contains(ModuleHelper.MODULE_STAND) -> numStand++
                    history.type.contains(ModuleHelper.MODULE_STRIDE) -> numStride++
                    history.type.contains(ModuleHelper.MODULE_TAPPER) -> numTapper++
                    history.type.contains(ModuleHelper.MODULE_DATATYPE_FACE) -> numFace++
                    history.type.contains(ModuleHelper.MODULE_ARM_DROOP) -> numArmDroop++
                }
            }
        }
    }

    private fun initLineChar(lineChart: LineChart, desc: String) {
        // no description text
        val description = Description()
        description.text = desc
        description.textColor = Color.BLACK
        lineChart.description = description
        // lineChart.setNoDataTextDescription("You need to provide data for the chart.")

        // enable touch gestures
        lineChart.setTouchEnabled(true)

        // enable scaling and dragging
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setDrawGridBackground(false)

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true)

        // set an alternative background color
        // lineChart.setBackgroundColor(Color.LTGRAY)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        lineChart.data = data

        //   val tf = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Regular.ttf")

        // get the legend (only possible after setting data)
        val l = lineChart.legend

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.form = Legend.LegendForm.LINE
        //  l.typeface = tf
        l.textColor = Color.WHITE

        val xl = lineChart.xAxis
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        // xl.setSpaceBetweenLabels(5)
        xl.isEnabled = true

        val leftAxis = lineChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setAxisMaxValue(15f)
        leftAxis.setAxisMinValue(-15f)
        leftAxis.setDrawGridLines(true)

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        lineChart.setMaxVisibleValueCount(20)
    }

    /**
     * 添加图表上的Entry
     */
    private fun addEntry(datas: ArrayList<XYZ>, lineChart: LineChart) {
        if (datas.size <= 0) {
            return
        }
        val x = ArrayList<Entry>()
        val y = ArrayList<Entry>()
        val z = ArrayList<Entry>()
        // 创建xyz的dataset
        for (i in 0 until datas.size) {
            x.add(Entry(i.toFloat(), datas[i].x.toFloat()))
            y.add(Entry(i.toFloat(), datas[i].y.toFloat()))
            z.add(Entry(i.toFloat(), datas[i].z.toFloat()))
        }
        Collections.sort(x, EntryXComparator())
        Collections.sort(y, EntryXComparator())
        Collections.sort(z, EntryXComparator())

        val data = lineChart.data

        if (data != null) {

            var setX: ILineDataSet? = data.getDataSetByIndex(0)
            var setY: ILineDataSet? = data.getDataSetByIndex(1)
            var setZ: ILineDataSet? = data.getDataSetByIndex(2)
            // set.addEntry(...); // can be called as well

            if (setX == null) {
                setX = createSetX(x)
                data.addDataSet(setX)
            }

            if (setY == null) {
                setY = createSetY(y)
                data.addDataSet(setY)
            }

            if (setZ == null) {
                setZ = createSetZ(z)
                data.addDataSet(setZ)
            }

            // add a new x-value first
            //  data.addXValue(i++.toString())
            // data.addEntry(new Entry((float) (Math.random() * 40) + 30f, setX.getEntryCount()), 0);

            /* setX?.addEntry(Entry(x.toFloat(), setX.entryCount.toFloat()))
             setY?.addEntry(Entry(y.toFloat(), setY.entryCount.toFloat()))
             setZ?.addEntry(Entry(z.toFloat(), setZ.entryCount.toFloat()))*/

            // let the chart know it's data has changed
            lineChart.notifyDataSetChanged()

            // limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(20f)
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            // lineChart.moveViewToX(data.getXValCount() - 121)

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private fun createSetX(x: ArrayList<Entry>): LineDataSet {
        val set = LineDataSet(x, "X")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = ColorTemplate.getHoloBlue()
        set.lineWidth = 1f
        set.setCircleColor(Color.WHITE)
        set.circleRadius = 0f
        set.fillAlpha = 65
        set.fillColor = ColorTemplate.getHoloBlue()
        set.highLightColor = Color.rgb(244, 117, 117)
        set.valueTextColor = Color.WHITE
        set.valueTextSize = 9f
        set.setDrawValues(false)
        return set
    }

    private fun createSetY(y: ArrayList<Entry>): LineDataSet {
        val set = LineDataSet(y, "Y")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = Color.RED
        set.setCircleColor(Color.WHITE)
        set.lineWidth = 1f
        set.circleRadius = 0f
        set.fillAlpha = 65
        set.fillColor = ColorTemplate.getHoloBlue()
        set.highLightColor = Color.rgb(244, 117, 117)
        set.valueTextColor = Color.WHITE
        set.valueTextSize = 9f
        set.setDrawValues(false)
        return set
    }

    private fun createSetZ(z: ArrayList<Entry>): LineDataSet {
        val set = LineDataSet(z, "Z")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = Color.GREEN
        set.setCircleColor(Color.WHITE)
        set.lineWidth = 1f
        set.circleRadius = 0f
        set.fillAlpha = 65
        set.fillColor = ColorTemplate.getHoloBlue()
        set.highLightColor = Color.rgb(244, 117, 117)
        set.valueTextColor = Color.WHITE
        set.valueTextSize = 9f
        set.setDrawValues(false)
        return set
    }

    private fun upload() {
        try {
            val history = dataList[currentFile - 1]

            val dbMark = history.mark
            val jsonObject = JSONObject(dbMark)
            val fileName = jsonObject.optString("file")
            Log.i(TAG, fileName)

            val url = "http://api.ivita.org/event"

            val params = HttpParams()
            params.put("uid", history.userID)
            params.put("data", history.mark)
            params.put("type", history.type)
            params.put("tag", "Parkinson")
            params.put("batch", history.batch)

            Log.i(TAG, params.toString())

            val localFile = File(history.filePath)
            if (!localFile.exists()) {
                // Toast.makeText(this@UploadActivity, "数据文件丢失", Toast.LENGTH_SHORT).show()
                // 本地数据文件不存在，更新数据库上传状态
                database.use {
                    update(HistoryData.TABLE_NAME, HistoryData.ISUPLOAD to "1").whereSimple(HistoryData.FILEPATH + " = ?", history.filePath).exec()
                }
                progressDialog?.dismiss()
                return
            }

            UploadUtils.asyncPutFile(Constant.DATA_FILE_BUCKET, fileName, history.filePath, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                    Log.d("PutObject", "UploadSuccess")
                    RxVolley.post(url, params, object : HttpCallback() {
                        override fun onSuccess(t: String?) {
                            Log.i(TAG, "上传成功！" + t)
                            database.use {
                                update(HistoryData.TABLE_NAME, HistoryData.ISUPLOAD to "1").whereSimple(HistoryData.FILEPATH + " = ?", history.filePath).exec()
                            }
                        }

                        override fun onFailure(errorNo: Int, strMsg: String?) {
                            Log.i(TAG, "上传失败" + errorNo.toString() + "------" + strMsg)
                            android.support.v7.app.AlertDialog.Builder(this@TestingReportNewActivity).setTitle("提示").setMessage("数据有错误！$errorNo#$strMsg").show()
                        }
                    })
                    currentFile++
                    if (currentFile <= fileNum) {
                        mHandler.sendEmptyMessage(CODE_UPLOAD)
                    } else {
                        mHandler.sendEmptyMessage(CODE_UPLOAD_FINISH)
                    }
                    val file = File(history.filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                    val file1 = File(history.filePath.substring(0, history.filePath.lastIndexOf(".")))
                    if (file1.exists()) {
                        file1.delete()
                    }
                }

                override fun onFailure(request: PutObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                    // Request exception
                    clientException?.printStackTrace()
                    if (serviceException != null) {
                        // Service exception
                        Log.e("ErrorCode", serviceException.errorCode)
                        Log.e("RequestId", serviceException.requestId)
                        Log.e("HostId", serviceException.hostId)
                        Log.e("RawMessage", serviceException.rawMessage)
                    }
                }

            }, OSSProgressCallback<PutObjectRequest> { request, currentSize, totalSize ->
                Log.d("PutObject", "currentSize: $currentSize totalSize: $totalSize")
                progressDialog?.max = totalSize.toInt()
                progressDialog?.progress = currentSize.toInt()
            })
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }

    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this@TestingReportNewActivity)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog?.setTitle("提示！")
        progressDialog?.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
        progressDialog?.show()
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    /**
     * 上传数据
     */
    fun upload(view: View) {
        fileNum = dataList.size
        if (fileNum > 0) {
            UploadUtils.initOSS()
            initProgressDialog()
            upload()
        } else {
            toast("没有找到数据！")
        }
    }


    override fun finish() {
        FileUtils.resetStats()
        super.finish()
    }

    /**
     * 返回
     */
    fun back(view: View) {
        finish()
    }
}
