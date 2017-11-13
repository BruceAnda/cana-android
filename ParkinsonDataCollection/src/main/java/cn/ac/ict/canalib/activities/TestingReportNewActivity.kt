package cn.ac.ict.canalib.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import cn.ac.ict.canalib.R
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
import com.lovearthstudio.duasdk.upload.UploadUtils
import kotlinx.android.synthetic.main.activity_testing_report_new.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File
import java.util.*

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

    private val mChartData = ArrayList<XYZ>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_report_new)

        et_testing_report_remark.isCursorVisible = false
        et_testing_report_remark.setOnClickListener {
            et_testing_report_remark.isCursorVisible = true
        }
        cv_testing_patient_info.visibility = View.VISIBLE
        cv_testing_report_memory.visibility = View.VISIBLE

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

        init()

    }

    fun init() {
        initMemory()
        initTremor()
        initSound()
        initStand()
        initStride()
        initTapping()
        initFace()
        initArmDroop()
    }

    private fun initArmDroop() {

    }

    private fun initFace() {

    }

    private fun initTapping() {

    }

    private fun initStride() {

    }

    private fun initStand() {

    }

    private fun initSound() {

    }

    private fun initTremor() {
        // 右手静止性震颤
        initLineChar(tremor_chart_rr_acc, "加速度")
        addEntry(mChartData, tremor_chart_rr_acc)
        initLineChar(tremor_chart_rr_gyro, "陀螺仪")
        addEntry(mChartData, tremor_chart_rr_gyro)
        // 左手静止性震颤
        initLineChar(tremor_chart_lr_acc, "加速度")
        addEntry(mChartData, tremor_chart_lr_acc)
        initLineChar(tremor_chart_lr_gyro, "陀螺仪")
        addEntry(mChartData, tremor_chart_lr_gyro)
        // 右手动作性震颤
        initLineChar(tremor_chart_rp_acc, "加速度")
        addEntry(mChartData, tremor_chart_rp_acc)
        initLineChar(tremor_chart_rp_gyro, "陀螺仪")
        addEntry(mChartData, tremor_chart_rp_gyro)
        // 左手动作性震颤
        initLineChar(tremor_chart_lp_acc, "加速度")
        addEntry(mChartData, tremor_chart_lp_acc)
        initLineChar(tremor_chart_lp_gyro, "陀螺仪")
        addEntry(mChartData, tremor_chart_lp_gyro)
    }

    private fun initMemory() {

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
