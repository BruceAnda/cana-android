package cn.ac.ict.canalib.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.ParkinsDataCollection
import cn.ac.ict.canalib.common.XYZ
import cn.ac.ict.canalib.constant.Constant
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.db.parser.HistoryParser
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.EntryXComparator
import com.github.mikephil.charting.components.Description
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams
import com.lovearthstudio.duasdk.upload.UploadUtils
import kotlinx.android.synthetic.main.activity_testing_report.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 测试报告页面
 */
class TestingReportActivity : AudioBaseActivity() {

    private val TAG = TestingReportActivity::class.java.name
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
                    ParkinsDataCollection.uiIntent.uploadFinish(this@TestingReportActivity)
                    sendEmptyMessage(CODE_FINNISH_UI)
                    // 检测以前有没有未上传的数据
                    /*database.use {

                        // 查询本次测试
                        list = select(HistoryData.TABLE_NAME).whereSimple("${HistoryData.ISUPLOAD} = ?", "0").parseList(HistoryParser<HistoryData>())
                        if (list.isNotEmpty()) {
                            currentFile = 1
                            fileNum = list.size
                            AlertDialog.Builder(this@TestingReportActivity)
                                    .setTitle("提示")
                                    .setMessage("发现以前有${list.size}条数据没有上传，是否现在上传？")
                                    .setNegativeButton("取消", { dialog, which -> sendEmptyMessage(CODE_FINNISH_UI) })
                                    .setPositiveButton("上传") { dialog, which ->
                                        initProgressDialog()
                                        upload()
                                    }
                                    .show()
                        } else {
                            sendEmptyMessage(CODE_FINNISH_UI)
                        }
                    }*/
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_report)
        init()
    }

    /**
     * 初始化
     */
    private fun init() {
        initPatientInfo()
        if (FileUtils.isTestingEnter) {
            findHistory()
        } else {
            btn_upload.visibility = View.GONE
        }
        if (FileUtils.hasTestOne) {
            initMemory()
        }
        if (FileUtils.hasTestTwo) {
            initTremor()
        }
        if (FileUtils.hasTestThree) {
            initSound()
        }
        if (FileUtils.hasTestFour) {
            initStand()
        }
        if (FileUtils.hasTestFive) {
            initStride()
        }
        if (FileUtils.hasTestSix) {
            initTapping()
        }
        if (FileUtils.hasTestSeven) {
            initVideo()
        }
        if (FileUtils.hasTestEight) {
            initArmDroop()
        }
    }

    /**
     * 初始化病人信息
     */
    private fun initPatientInfo() {
        val patienT_NAME = FileUtils.PATIENT_NAME
        val patienT_SEX = if (FileUtils.PATIENT_SEX.equals("M")) {
            "男"
        } else {
            "女"
        }
        val patienT_AGE = FileUtils.PATIENT_AGE
        val patienT_MEDICINE = FileUtils.PATIENT_MEDICINE
        val switchinG_PERIOD = if (FileUtils.SWITCHING_PERIOD.equals(0)) {
            "关"
        } else {
            "开"
        }
        val text = "姓名:$patienT_NAME\n性别:$patienT_SEX\n年龄:$patienT_AGE\n使用药物:$patienT_MEDICINE\n用药开关期:$switchinG_PERIOD"
        Log.i(TAG, "$text")
        tv_patient_data.text = text
    }

    /**
     * 初始化手臂下垂数据
     */
    private fun initArmDroop() {
        ll_arm_droop.visibility = View.VISIBLE
        // 显示特征
        arm_droop_feature.text = "右手下摆次数：${FileUtils.rArmDroopCount}\n左手下摆次数：${FileUtils.lArmDroopCount}"

        // 右手手臂下垂
        initLineChar(arm_droop_r_acc, "右手手臂下垂加速度")
        addEntry(FileUtils.armDroopR.data.acc, arm_droop_r_acc)
        initLineChar(arm_droop_r_gyro, "右手手臂下垂陀螺仪")
        addEntry(FileUtils.armDroopR.data.gyro, arm_droop_r_gyro)

        // 左手手臂下垂
        initLineChar(arm_droop_l_acc, "左手手臂下垂加速度")
        addEntry(FileUtils.armDroopL.data.acc, arm_droop_l_acc)
        initLineChar(arm_droop_l_gyro, "左手手臂下垂陀螺仪")
        addEntry(FileUtils.armDroopL.data.gyro, arm_droop_l_gyro)
    }

    /**
     * 初始化Video数据
     */
    private fun initVideo() {
        ll_face.visibility = View.VISIBLE
        // 显示特征
        face_feature.text = "瞬目次数：${FileUtils.blinkTimes}\n嘴角微笑角度：${FileUtils.smileAngle}"
    }

    /**
     * 初始化Tapping数据
     */
    private fun initTapping() {
        ll_tapper.visibility = View.VISIBLE
        tapping_feature.text = "右手(交替比率：${FileUtils.rAlternatingRatio}，平均速度：${FileUtils.rAvgspeed})\n左手(交替比率：${FileUtils.lAlternatingRatio}，平均速度：${FileUtils.lAvgspeed})"
    }

    /**
     * 初始化行走平衡数据
     */
    private fun initStride() {
        ll_stride.visibility = View.VISIBLE
        // 显示特征
        stride_feature.text = "步数：${FileUtils.step}"
        // 行走平衡
        initLineChar(stride_chart_acc, "行走平衡加速度")
        addEntry(FileUtils.strideData.acc, stride_chart_acc)
        initLineChar(stride_chart_gyro, "行走平衡陀螺仪")
        addEntry(FileUtils.strideData.gyro, stride_chart_gyro)
    }

    /**
     * 初始化Stand数据
     */
    private fun initStand() {
        ll_stand.visibility = View.VISIBLE
        // 显示特征
        stand_feature.text = "右腿站立(方差：${FileUtils.rVariance}，时间：${FileUtils.rRime})\n左腿站立(方差：${FileUtils.lVariance}，时间：${FileUtils.lTime})"
        // 右腿站立
        initLineChar(stand_chart_r_acc, "右腿站立加速度")
        addEntry(FileUtils.standR.data.acc, stand_chart_r_acc)
        initLineChar(stand_chart_r_gyro, "右腿站立陀螺仪")
        addEntry(FileUtils.standR.data.gyro, stand_chart_r_gyro)
        // 左腿站立
        initLineChar(stand_chart_l_acc, "左腿站立加速度")
        addEntry(FileUtils.standL.data.acc, stand_chart_l_acc)
        initLineChar(stand_chart_l_gyro, "左腿站立陀螺仪")
        addEntry(FileUtils.standL.data.gyro, stand_chart_l_gyro)
    }

    /**
     * 初始化Sound数据
     */
    private fun initSound() {
        ll_sound.visibility = View.VISIBLE
        // 显示特征
        sound_feature.text = "音调：${FileUtils.tone}\n音量：${FileUtils.volume}"
        // 声音图表数据
        initLineChar(sound_chart, "语言能力")
        addEntry(ArrayList(), sound_chart)
    }

    /**
     * 初始化震颤模块数据
     */
    private fun initTremor() {
        ll_tremor.visibility = View.VISIBLE
        // 显示特征
        tremor_feature.text = "右手静止性震颤(频率：${FileUtils.rrFrequency}，幅度：${FileUtils.rrAmplitude})\n左手静止性震颤(频率：${FileUtils.lrFrequency}，幅度：${FileUtils.lrAmplitude})\n右手动作性震颤(频率：${FileUtils.rpFrequency}，幅度：${FileUtils.rpAmplitude})\n左手动作性震颤(频率：${FileUtils.lpFrequency}，幅度：${FileUtils.lpAmplitude})\n"
        // 右手静止性震颤
        initLineChar(tremor_chart_rr_acc, "右手静止性震颤加速度")
        addEntry(FileUtils.tremorRR.data.acc, tremor_chart_rr_acc)
        initLineChar(tremor_chart_rr_gyro, "右手静止性震颤陀螺仪")
        addEntry(FileUtils.tremorRR.data.gyro, tremor_chart_rr_gyro)
        // 左手静止性震颤
        initLineChar(tremor_chart_lr_acc, "左手静止性震颤加速度")
        addEntry(FileUtils.tremorLR.data.acc, tremor_chart_lr_acc)
        initLineChar(tremor_chart_lr_gyro, "左手静止性震颤陀螺仪")
        addEntry(FileUtils.tremorLR.data.gyro, tremor_chart_lr_gyro)
        // 右手动作性震颤
        initLineChar(tremor_chart_rp_acc, "右手动作性震颤加速度")
        addEntry(FileUtils.tremorRP.data.acc, tremor_chart_rp_acc)
        initLineChar(tremor_chart_rp_gyro, "右手动作性震颤陀螺仪")
        addEntry(FileUtils.tremorRP.data.gyro, tremor_chart_rp_gyro)
        // 左手动作性震颤
        initLineChar(tremor_chart_lp_acc, "左手动作性震颤加速度")
        addEntry(FileUtils.tremorLP.data.acc, tremor_chart_lp_acc)
        initLineChar(tremor_chart_lp_gyro, "左手动作性震颤陀螺仪")
        addEntry(FileUtils.tremorLP.data.gyro, tremor_chart_lp_gyro)
    }

    /**
     * 初始化记忆模块数据
     */
    private fun initMemory() {
        ll_memory.visibility = View.VISIBLE
        // 显示数字记忆的答案
        tv_memory_data.text = "数字记忆:${FileUtils.memory.data}"
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

    private fun upload() {
        try {
            val history = list!![currentFile - 1]

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
                            android.support.v7.app.AlertDialog.Builder(this@TestingReportActivity).setTitle("提示").setMessage("数据有错误！$errorNo#$strMsg").show()
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
        progressDialog = ProgressDialog(this@TestingReportActivity)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog?.setTitle("提示！")
        progressDialog?.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
        progressDialog?.show()
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun initLineChar(lineChart: LineChart, desc: String) {
        // no description text
        val description = Description()
        description.text = desc
        description.textColor = Color.WHITE
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
        lineChart.setBackgroundColor(Color.LTGRAY)

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
        leftAxis.setAxisMaxValue(10f)
        leftAxis.setAxisMinValue(-10f)
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

    /**
     * 上传数据
     */
    fun upload(view: View) {
        if (fileNum > 0) {
            UploadUtils.initOSS()
            initProgressDialog()
            upload()
        } else {
            toast("没有找到数据！")
        }
    }

    /**
     * 取消上传
     */
    fun cancel(view: View) {
        finish()
    }

    override fun finish() {
        FileUtils.resetStats()
        super.finish()
    }
}
