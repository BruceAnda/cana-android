package cn.ac.ict.cana.newversion.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View

import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.adapter.HistoryDetailAdapter
import cn.ac.ict.cana.newversion.db.bean.Batch
import cn.ac.ict.cana.newversion.db.bean.History
import cn.ac.ict.cana.newversion.db.database
import cn.ac.ict.cana.newversion.db.parser.HistoryParser
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity
import cn.ac.ict.cana.newversion.utils.FileUtils
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams
import kotlinx.android.synthetic.main.activity_history_detail.*
import org.jetbrains.anko.UI
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.json.JSONObject
import zhaoliang.com.uploadfile.UploadUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * 历史详情页面
 */
class HistoryDetailActivity : AppCompatActivity() {

    private val TAG = HistoryDetailActivity::class.java.simpleName
    private val titles = arrayOf("数字记忆", "震颤情况", "语言能力", "站立平衡", "行走平衡", "手指灵敏", "面部表情")
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

        Log.i(TAG, "onCreate:batch:" + batch.batch + ":" + batch.time + ":" + batch.patientName)

        patient_name.text = "患者：" + batch.patientName
        collection_time.text = "采集时间：" + SimpleDateFormat("yyyy-MM-dd, HH:mm").format(Date(batch.time))


        database.use {
            val historyList = select(History.TABLE_NAME).whereSimple(History.BATCH + " = ?", batch.batch).parseList(HistoryParser<History>())
            rv_history_detail_list.layoutManager = LinearLayoutManager(this@HistoryDetailActivity, LinearLayoutManager.VERTICAL, false)
            rv_history_detail_list.adapter = HistoryDetailAdapter(this@HistoryDetailActivity, historyList, titlesMap)

            unUploadhistoryList = select(History.TABLE_NAME).whereSimple(History.BATCH + " = ?", batch.batch).parseList(HistoryParser<History>())
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
        UploadUtils.initOSS()
        initProgressDialog()
        upload()
    }

    private fun upload() {
        try {
            val history = unUploadhistoryList!![currentFile - 1]
            var suffix = history.filePath.substring(history.filePath.lastIndexOf("."), history.filePath.length)
            var newfileName = history.type + "_" + System.currentTimeMillis() + suffix

            // http://api.ivita.org/event/2/parkins.walk/walkfile/-3/2
            // String url = "http://api.ivita.org/event/" + Dua.getInstance().getCurrentDuaId() + "/" + history.type + "/" + fileName + "/0/0";
            val dbMark = history.mark
            val jsonObject = JSONObject(dbMark)
            val fileName = jsonObject.optString("file")
            Log.i(TAG, fileName)

            val url = "http://api.ivita.org/event"
            //String mark = "{\"uid\":" + Dua.getInstance().getCurrentDuaUid() + ",\"data\":" + history.mark + ",\"type\":" + history.type
            //+"}";
            // post(url, mark);
            val params = HttpParams()
            params.put("uid", history.userID)
            params.put("data", history.mark)
            params.put("type", history.type)
            params.put("tag", "Parkinson")
            params.put("batch", FileUtils.batch)
            post(url, params)

            UploadUtils.asyncPutFile(newfileName, history.filePath, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                override fun onSuccess(putObjectRequest: PutObjectRequest, putObjectResult: PutObjectResult) {
                    var result = false
                    Log.i(TAG, "上传成功！")
                    try {
                        // TODO: Change to Gson
                        // updateHistoryUploadedById(history.id);
                        result = true
                    } catch (e: Exception) {
                        Log.e("toJson", e.toString())
                    }

                }

                override fun onFailure(putObjectRequest: PutObjectRequest, e: ClientException, e1: ServiceException) {
                    Log.i(TAG, "上传失败！")
                    currentFile++
                    if (currentFile <= fileNum) {
                        upload()
                        UI {
                            progressDialog!!.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
                        }
                        // mHandler.sendEmptyMessage(CODE_UPLOAD)
                    } else {
                        // mHandler.sendEmptyMessage(CODE_UPLOAD_FINISH)
                        UI {
                            progressDialog!!.setMessage("上传完成！")
                            Thread.sleep(1000)
                            if (progressDialog != null && progressDialog!!.isShowing)
                                progressDialog!!.dismiss()
                            startActivity(Intent(this@HistoryDetailActivity, MainActivityNew::class.java))
                            finish()
                        }
                    }
                    val file = File(history.filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                    val file1 = File(history.filePath.substring(0, history.filePath.lastIndexOf(".")))
                    if (file1.exists()) {
                        file1.delete()
                    }
                    database.use {
                        update(History.TABLE_NAME, History.ISUPLOAD to true).whereSimple(History.FILEPATH, history.filePath).exec()
                    }
                }
            }, null)
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }

    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this@HistoryDetailActivity)
        progressDialog!!.setTitle("提示！")
        progressDialog!!.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个")
        progressDialog!!.show()
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = 100
        progressDialog!!.progress = 20
        progressDialog!!.show()
    }

    fun post(url: String, params: HttpParams) {
        Log.i(TAG, "url:" + url + "-------params:" + params.jsonParams)

        RxVolley.post(url, params, object : HttpCallback() {
            override fun onSuccess(t: String?) {
                Log.i(TAG, t)
            }

            override fun onFailure(errorNo: Int, strMsg: String?) {
                Log.i(TAG, errorNo.toString() + "------" + strMsg)
            }
        })
    }
}
