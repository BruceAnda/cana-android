package cn.ac.ict.cana.newversion.modules.upload

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast

import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams


import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.activities.MainActivityNew
import cn.ac.ict.cana.newversion.constant.Constant
import cn.ac.ict.cana.newversion.db.bean.History
import cn.ac.ict.cana.newversion.db.database
import cn.ac.ict.cana.newversion.db.parser.HistoryParser
import cn.ac.ict.cana.newversion.utils.FileUtils
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
                    startActivity(Intent(this@UploadActivity, MainActivityNew::class.java))
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

    lateinit var list: List<History>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        database.use {
            list = select(History.TABLE_NAME).whereSimple(History.BATCH + " = ?", FileUtils.batch).parseList(HistoryParser<History>())
            fileNum = list.size
            for (history in list) {
                when {
                    history.type == ModuleHelper.MODULE_COUNT -> numCount++
                    history.type == ModuleHelper.MODULE_TREMOR -> numTremor++
                    history.type == ModuleHelper.MODULE_SOUND -> numSound++
                    history.type == ModuleHelper.MODULE_STAND -> numStand++
                    history.type == ModuleHelper.MODULE_STRIDE -> numStride++
                    history.type == ModuleHelper.MODULE_TAPPER -> numTapper++
                    history.type == ModuleHelper.MODULE_FACE -> numFace++
                    history.type == ModuleHelper.MODULE_ARM_DROOP -> numArmDroop++
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
                Toast.makeText(this@UploadActivity, "数据文件丢失", Toast.LENGTH_SHORT).show()
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
                                update(History.TABLE_NAME, History.ISUPLOAD to "1").whereSimple(History.FILEPATH + " = ?", history.filePath).exec()
                            }
                        }

                        override fun onFailure(errorNo: Int, strMsg: String?) {
                            Log.i(TAG, "上传失败" + errorNo.toString() + "------" + strMsg)
                            android.support.v7.app.AlertDialog.Builder(this@UploadActivity).setTitle("提示").setMessage("数据有错误！$errorNo#$strMsg").show()
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

    companion object {

        private val TAG = UploadActivity::class.java.name
        private val CODE_UPLOAD = 0
        private val CODE_UPLOAD_FINISH = 1
        private val CODE_DISMISS_DIAOLG = 2
    }
}
