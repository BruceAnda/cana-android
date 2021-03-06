package cn.ac.ict.canalib.modules.upload

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast

import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams


import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.constant.Constant
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.db.parser.HistoryParser
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.common.ParkinsDataCollection
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.lovearthstudio.duasdk.upload.UploadUtils
import kotlinx.android.synthetic.main.activity_upload.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.json.JSONObject
import java.io.File

/**
 * 上传数据界面
 */
class UploadActivity : Activity() {

    private val TAG = UploadActivity::class.java.name
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
                    ParkinsDataCollection.uiIntent.uploadFinish(this@UploadActivity)

                    // 检测以前有没有未上传的数据
                    database.use {

                        // 查询本次测试
                        list = select(HistoryData.TABLE_NAME).whereSimple("${HistoryData.ISUPLOAD} = ?", "0").parseList(HistoryParser<HistoryData>())
                        if (list.isNotEmpty()) {
                            currentFile = 1
                            fileNum = list.size
                            AlertDialog.Builder(this@UploadActivity)
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
                    }
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
        setContentView(R.layout.activity_upload)

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
        tv_total.text = "总共(${fileNum})个文件"
        tv_count!!.text = "数字记忆($numCount)"
        tv_tremor!!.text = "震颤情况($numTremor)"
        tv_sound!!.text = "语言能力($numSound)"
        tv_stand!!.text = "站立平衡($numStand)"
        tv_stride!!.text = "行走平衡($numStride)"
        tv_tapper!!.text = "手指灵敏($numTapper)"
        tv_face!!.text = "面部表情($numFace)"
        tv_arm_droop.text = "手臂下垂($numArmDroop)"

    }

    fun upload(view: View) {
        UploadUtils.initOSS()
        initProgressDialog()
        upload()
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
                            android.support.v7.app.AlertDialog.Builder(this@UploadActivity).setTitle("提示").setMessage("数据有错误！$errorNo#$strMsg").show()
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
        progressDialog = ProgressDialog(this@UploadActivity)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog?.setTitle("提示！")
        progressDialog?.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
        progressDialog?.show()
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }
}
