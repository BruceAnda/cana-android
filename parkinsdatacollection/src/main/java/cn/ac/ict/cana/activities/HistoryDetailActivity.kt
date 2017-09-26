package cn.ac.ict.cana.activities

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

import cn.ac.ict.cana.adapter.HistoryDetailAdapter
import cn.ac.ict.cana.db.bean.History
import cn.ac.ict.cana.db.database
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.constant.Constant
import cn.ac.ict.cana.db.bean.Batch
import cn.ac.ict.cana.db.parser.HistoryParser
import cn.ac.ict.cana.parkionsdatacollection.R
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
                        val historyList = select(History.TABLE_NAME).whereSimple(History.BATCH + " = ?", batch.batch).parseList(HistoryParser<History>())
                        rv_history_detail_list.layoutManager = LinearLayoutManager(this@HistoryDetailActivity, LinearLayoutManager.VERTICAL, false)
                        rv_history_detail_list.adapter = HistoryDetailAdapter(this@HistoryDetailActivity, historyList, titlesMap)

                        unUploadhistoryList = select(History.TABLE_NAME).whereArgs(History.BATCH + " = \"${batch.batch}\" and " + History.ISUPLOAD + " = \"0\"").parseList(HistoryParser<History>())
                        fileNum = unUploadhistoryList.size
                    }
                }
            }
        }
    }

    private val TAG = HistoryDetailActivity::class.java.simpleName
    private val titles = arrayOf("数字记忆", "震颤情况", "语言能力", "站立平衡", "行走平衡", "手指灵敏", "面部表情", "手臂下垂")
    private val titlesMap = HashMap<String, String>()
    lateinit var unUploadhistoryList: List<History>

    private lateinit var batch: Batch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        batch = intent.extras.getParcelable<Batch>("batch")

        titlesMap.put(ModuleHelper.MODULE_COUNT, titles[0])
        titlesMap.put(ModuleHelper.MODULE_TREMOR, titles[1])
        titlesMap.put(ModuleHelper.MODULE_SOUND, titles[2])
        titlesMap.put(ModuleHelper.MODULE_STAND, titles[3])
        titlesMap.put(ModuleHelper.MODULE_STRIDE, titles[4])
        titlesMap.put(ModuleHelper.MODULE_TAPPER, titles[5])
        titlesMap.put(ModuleHelper.MODULE_FACE, titles[6])
        titlesMap.put(ModuleHelper.MODULE_ARM_DROOP, titles[7])

        Log.i(TAG, "onCreate:batch:" + batch.batch + ":" + batch.time + ":" + batch.patientName)

        patient_name.text = "患者：" + batch.patientName
        collection_time.text = "采集时间：" + SimpleDateFormat("yyyy-MM-dd, HH:mm").format(Date(batch.time))


        database.use {
            val historyList = select(History.TABLE_NAME).whereSimple(History.BATCH + " = ?", batch.batch).parseList(HistoryParser<History>())
            rv_history_detail_list.layoutManager = LinearLayoutManager(this@HistoryDetailActivity, LinearLayoutManager.VERTICAL, false)
            rv_history_detail_list.adapter = HistoryDetailAdapter(this@HistoryDetailActivity, historyList, titlesMap)

            unUploadhistoryList = select(History.TABLE_NAME).whereArgs(History.BATCH + " = \"${batch.batch}\" and " + History.ISUPLOAD + " = \"0\"").parseList(HistoryParser<History>())
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
                        val historyList = select(History.TABLE_NAME).whereSimple(History.BATCH + " = ?", batch.batch).parseList(HistoryParser<History>())
                        for (history in historyList) {
                            val file = File(history.filePath)
                            if (file.exists()) {
                                file.delete()
                            }
                        }

                        // 删除历史表
                        delete(History.TABLE_NAME, History.BATCH + " = ?", arrayOf(batch.batch))

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
                                update(History.TABLE_NAME, History.ISUPLOAD to "1").whereSimple(History.FILEPATH + " = ?", history.filePath).exec()
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
