package cn.ac.ict.cana.newversion.modules.upload

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View

import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams


import cn.ac.ict.cana.R
import cn.ac.ict.cana.activities.MainActivity
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.activities.MainActivityNew
import cn.ac.ict.cana.newversion.db.bean.History
import cn.ac.ict.cana.newversion.db.database
import cn.ac.ict.cana.newversion.db.parser.HistoryParser
import cn.ac.ict.cana.newversion.utils.FileUtils
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_upload.*
import org.jetbrains.anko.UI
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.json.JSONObject
import zhaoliang.com.uploadfile.UploadUtils
import java.io.File

/**
 * 上传数据界面
 */
class UploadActivity : Activity() {

    /*internal var mHandler: Handler = object : Handler() {
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
                    startActivity(Intent(this@UploadActivity, MainActivityNew_::class.java))
                    finish()
                }
            }
        }
    }*/
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

    }

    fun upload(view: View) {
        UploadUtils.initOSS()
        initProgressDialog()
        upload()
    }

    private fun upload() {
        try {
            val history = list!![currentFile - 1]
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
                            startActivity(Intent(this@UploadActivity, MainActivityNew::class.java))
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
        progressDialog = ProgressDialog(this)
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

    companion object {

        private val TAG = UploadActivity::class.java.name
        private val CODE_UPLOAD = 0
        private val CODE_UPLOAD_FINISH = 1
        private val CODE_DISMISS_DIAOLG = 2
    }
}
