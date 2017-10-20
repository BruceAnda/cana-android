package cn.ac.ict.cana.features.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast

import cn.ac.ict.cana.features.adapter.HistoryDetailAdapter
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.constant.Constant
import cn.ac.ict.canalib.db.bean.Batch
import cn.ac.ict.canalib.db.parser.HistoryParser
import cn.ac.ict.canalib.R
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams
import com.lovearthstudio.duasdk.upload.UploadUtils
import kotlinx.android.synthetic.main.activity_history_detail.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * 历史详情页面
 */
class HistoryDetailActivity : AppCompatActivity() {

    private val CODE_UPLOAD = 0
    private val CODE_UPLOAD_FINISH = 1
    private val CODE_DISMISS_DIAOLG = 2
    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CODE_UPLOAD -> progressDialog!!.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
                CODE_UPLOAD_FINISH -> {
                    progressDialog!!.setMessage("上传完成！")
                    sendEmptyMessageDelayed(CODE_DISMISS_DIAOLG, 1000)
                }
                CODE_DISMISS_DIAOLG -> {
                    if (progressDialog != null && progressDialog!!.isShowing)
                        progressDialog!!.dismiss()
                    /* startActivity(Intent(this@HistoryDetailActivity, MainActivityNew_::class.java))
                     finish()*/
                    database.use {
                        val historyList = select(HistoryData.TABLE_NAME).whereSimple(HistoryData.BATCH + " = ?", batch.batch).parseList(HistoryParser<HistoryData>())
                        rv_history_detail_list.layoutManager = LinearLayoutManager(this@HistoryDetailActivity, LinearLayoutManager.VERTICAL, false)
                        rv_history_detail_list.adapter = HistoryDetailAdapter(this@HistoryDetailActivity, historyList, titlesMap)

                        unUploadhistoryList = select(HistoryData.TABLE_NAME).whereArgs(HistoryData.BATCH + " = \"${batch.batch}\" and " + HistoryData.ISUPLOAD + " = \"0\"").parseList(HistoryParser<HistoryData>())
                        fileNum = unUploadhistoryList.size
                    }
                }
            }
        }
    }

    private val TAG = HistoryDetailActivity::class.java.simpleName
    private val titles = arrayOf(
            "数字记忆",
            "右手静止性震颤", "右手动作性震颤", "左手静止性震颤", "左手运动性震颤",
            "语言能力",
            "右腿站立平衡", "左腿站立平衡",
            "行走平衡",
            "右手手指灵敏", "左手手指灵敏",
            "面部表情",
            "右手手臂下垂", "左手手臂下垂")
    private val titlesMap = HashMap<String, String>()
    lateinit var unUploadhistoryList: List<HistoryData>

    private lateinit var batch: Batch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        batch = intent.extras.getParcelable<Batch>("batch")

        titlesMap.put(ModuleHelper.MODULE_COUNT, titles[0])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_TREMOR_RR, titles[1])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_TREMOR_RP, titles[2])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_TREMOR_LR, titles[3])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_TREMOR_LP, titles[4])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_SOUND, titles[5])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_STAND_R, titles[6])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_STAND_L, titles[7])
        titlesMap.put(ModuleHelper.MODULE_STRIDE, titles[8])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_TAPPING_R, titles[9])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_TAPPING_L, titles[10])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_FACE, titles[11])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_ARMDROOP_R, titles[12])
        titlesMap.put(ModuleHelper.MODULE_DATATYPE_ARMDROOP_L, titles[13])



        Log.i(TAG, "onCreate:batch:" + batch.batch + ":" + batch.time + ":" + batch.patientName)

        patient_name.text = "患者：" + batch.patientName
        collection_time.text = "采集时间：" + SimpleDateFormat("yyyy-MM-dd, HH:mm").format(Date(batch.time))


        database.use {
            val historyList = select(HistoryData.TABLE_NAME).whereSimple(HistoryData.BATCH + " = ?", batch.batch).parseList(HistoryParser<HistoryData>())
            rv_history_detail_list.layoutManager = LinearLayoutManager(this@HistoryDetailActivity, LinearLayoutManager.VERTICAL, false)
            rv_history_detail_list.adapter = HistoryDetailAdapter(this@HistoryDetailActivity, historyList, titlesMap)

            unUploadhistoryList = select(HistoryData.TABLE_NAME).whereArgs(HistoryData.BATCH + " = \"${batch.batch}\" and " + HistoryData.ISUPLOAD + " = \"0\"").parseList(HistoryParser<HistoryData>())
            fileNum = unUploadhistoryList.size
        }
    }

    /**
     * 删除数据
     */
    fun delete(view: View) {
        AlertDialog.Builder(this@HistoryDetailActivity).setTitle("提示！")
                .setCancelable(false)
                .setMessage("确定要删除吗？注意此操作不可逆奥！")
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    database.use {
                        // 删除文件
                        val historyList = select(HistoryData.TABLE_NAME).whereSimple(HistoryData.BATCH + " = ?", batch.batch).parseList(HistoryParser<HistoryData>())
                        for (history in historyList) {
                            val file = File(history.filePath)
                            if (file.exists()) {
                                file.delete()
                            }
                        }

                        // 删除历史表
                        delete(HistoryData.TABLE_NAME, HistoryData.BATCH + " = ?", arrayOf(batch.batch))

                        // 删除Batch表
                        delete(Batch.TABLE_NAME, Batch.BATCH + " = ?", arrayOf(batch.batch))
                        finish()
                    }
                })
                .setNegativeButton("取消", null).show()
    }

    /**
     * 返回按钮点击
     */
    fun back(view: View) {
        finish()
    }

    private var progressDialog: ProgressDialog? = null
    private var fileNum: Int = 0
    private var currentFile = 1

    fun upload(view: View) {
        if (fileNum > 0) {
            UploadUtils.initOSS()
            initProgressDialog()
            upload()
        } else {
            Toast.makeText(this@HistoryDetailActivity, "所有测试数据都已经上传！", Toast.LENGTH_SHORT).show()
        }
    }

    fun upload() {
        try {
            val history = unUploadhistoryList!![currentFile - 1]

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
            // post(url, params)

            val localFile = File(history.filePath)
            if (!localFile.exists()) {
                Toast.makeText(this@HistoryDetailActivity, "数据文件丢失", Toast.LENGTH_SHORT).show()
                progressDialog?.dismiss()
                return
            }

            UploadUtils.asyncPutFile(Constant.DATA_FILE_BUCKET, fileName, history.filePath, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                    Log.d("PutObject", "UploadSuccess")
                    RxVolley.post(url, params, object : HttpCallback() {
                        override fun onSuccess(t: String?) {
                            Log.i(TAG, "上传成功！" + t)
                            var result = JSONObject(t)
                            database.use {
                                update(HistoryData.TABLE_NAME, HistoryData.ISUPLOAD to "1").whereSimple(HistoryData.FILEPATH + " = ?", history.filePath).exec()
                            }
                        }

                        override fun onFailure(errorNo: Int, strMsg: String?) {
                            Log.i(TAG, "上传失败" + errorNo.toString() + "------" + strMsg)
                            android.support.v7.app.AlertDialog.Builder(this@HistoryDetailActivity).setTitle("提示").setMessage("数据有错误！$errorNo#$strMsg").show()
                        }
                    })
                    currentFile++
                    if (currentFile <= fileNum) {
                        upload()
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
                        Log.e("ErrorCode", serviceException.getErrorCode());
                        Log.e("RequestId", serviceException.getRequestId());
                        Log.e("HostId", serviceException.getHostId());
                        Log.e("RawMessage", serviceException.getRawMessage());
                    }
                }

            }, OSSProgressCallback<PutObjectRequest> { request, currentSize, totalSize ->
                progressDialog?.max = totalSize.toInt()
                progressDialog?.progress = currentSize.toInt()
                Log.d("PutObject", "currentSize: $currentSize totalSize: $totalSize")
            })

        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }

    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this@HistoryDetailActivity)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog?.setTitle("提示！")
        progressDialog?.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
        progressDialog?.show()
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }
}
