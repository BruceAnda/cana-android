package cn.ac.ict.cana.newversion.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.DataBaseHelper
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.bean.AccAndGyroData
import cn.ac.ict.cana.newversion.contant.GlobleData
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.guide.*
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity
import cn.ac.ict.cana.newversion.pagers.ExamPageFragment
import cn.ac.ict.cana.newversion.provider.HistoryProvider
import cn.ac.ict.cana.newversion.utils.FileUtils
import cn.ac.ict.cana.newversion.utils.GzipUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_feed_back_v2.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 返回的Activity
 */

class FeedBackActivity : YouMengBaseActivity(), View.OnClickListener {

    private val TAG = FeedBackActivity::class.java.simpleName

    private val mTremors = arrayOf("Tremor_LR", "Tremor_LP", "Tremor_RR", "Tremor_RP")
    private val mStands = arrayOf("Stand_L", "Stand_R")
    private val mTappers = arrayOf("Tapping_L", "Tapping_R")

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_cancel ->
                SweetAlertDialog(this@FeedBackActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.are_you_sure))
                        .setContentText(getString(R.string.cannot_recover))
                        .setConfirmText(getString(R.string.btn_cancel))
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                        }
                        .setCancelText(resources.getText(R.string.btn_discard).toString())
                        .showCancelButton(true)
                        .setCancelClickListener { sDialog ->
                            sDialog.cancel()
                            finish()
                        }.show()
            R.id.btn_save -> {
                val suffix = FileUtils.filePath.substring(FileUtils.filePath.lastIndexOf("."), FileUtils.filePath.length)
                val fileName = "Parkins/" + MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}") + suffix

                if (ModuleHelper.MODULE_TREMOR.equals(modelName)) {
                    Log.i(TAG, "插入${modelName}数据")
                    val data = "{\"score\":\"${score},${score2},${score3},${score4}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_medicine\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"switching_period\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"file\":\"${fileName}\"}"
                    insertDB(data)
                } else {
                    Log.i(TAG, "插入${modelName}数据")
                    val data = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_medicine\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"switching_period\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"file\":\"${fileName}\"}"
                    insertDB(data)
                }

                // 数字记忆
                if (ModuleHelper.MODULE_COUNT.equals(modelName)) {
                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入震颤测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        var filePath = FileUtils.filePath
                        // 进入下一项测试，保存数据
                        startActivity(Intent(this, ModelGuideActivity2::class.java))
                        doAsync {
                            var jo = JSONObject()
                            jo.put("data", JSON.toJSONString(FileUtils.countDataList))
                            val toString = jo.toString()
                            println("countData:" + toString)
                            FileUtils.writeToFile(toString, filePath)

                            GzipUtil.compressForZip(filePath, filePath + ".gz")

                            finish()
                        }
                    }).setCancelable(false).show()
                    // 震颤
                } else if (ModuleHelper.MODULE_TREMOR.equals(modelName)) {
                    var filePath = FileUtils.filePath
                    if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@FeedBackActivity, UploadActivity::class.java))
                        writeTremorData(filePath)
                        finish()
                    } else {
                        AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入声音检测").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            // 进入下一项测试，保存数据
                            startActivity(Intent(this, ModelGuideActivity3::class.java))
                            writeTremorData(filePath)
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 语言能力
                } else if (ModuleHelper.MODULE_SOUND.equals(modelName)) {
                    var filePath = FileUtils.filePath
                    if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@FeedBackActivity, UploadActivity::class.java))
                        writeSoundData(filePath)
                        finish()
                    } else {
                        AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入站立平衡检测").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity4::class.java))
                            writeSoundData(filePath)
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 单腿站立
                } else if (ModuleHelper.MODULE_STAND.equals(modelName)) {
                    var filePath = FileUtils.filePath
                    if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@FeedBackActivity, UploadActivity::class.java))
                        writeStandData(filePath)
                        finish()
                    } else {

                        AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入行走平衡测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity5::class.java))
                            writeStandData(filePath)
                        }).setCancelable(false).show()
                    }
                    // 行走平衡
                } else if (ModuleHelper.MODULE_STRIDE.equals(modelName)) {
                    var filePath = FileUtils.filePath
                    if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@FeedBackActivity, UploadActivity::class.java))
                        writeStrideData(filePath)
                        finish()
                    } else {
                        AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入手指灵敏测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity6::class.java))
                            writeStrideData(filePath)
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 手指灵敏
                } else if (ModuleHelper.MODULE_TAPPER.equals(modelName)) {
                    var filePath = FileUtils.filePath
                    if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@FeedBackActivity, UploadActivity::class.java))
                        writeTapperData(filePath)
                        finish()
                    } else {
                        AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入面部表情测试。").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity7::class.java))
                            writeTapperData(filePath)
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 面部表情
                } else if (ModuleHelper.MODULE_FACE.equals(modelName)) {
                    var filePath = FileUtils.filePath
                    if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@FeedBackActivity, UploadActivity::class.java))
                        writeFaceData(filePath)
                        finish()
                    } else {
                        AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("恭喜你完成了测试，请上传数据我们会对您的康复情况进行分析。").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, UploadActivity::class.java))
                            writeFaceData(filePath)
                        }).setCancelable(false).show()
                    }
                }
            }
        }
    }

    private fun writeFaceData(filePath: String) {
        doAsync {
            GzipUtil.compressForZip(filePath, filePath + ".gz")
            finish()
        }
    }

    private fun writeTapperData(filePath: String) {
        doAsync {
            var jo = JSONObject()

            jo.put("type", "tapper")
            jo.put(mTappers[0], JSON.toJSONString(FileUtils.tapperLDatas))
            jo.put(mTappers[1], JSON.toJSONString(FileUtils.tapperRDatas))

            FileUtils.writeToFile(jo.toString(), filePath)

            GzipUtil.compressForZip(filePath, filePath + ".gz")
        }
    }

    private fun writeStrideData(filePath: String) {
        doAsync {
            var jo = JSONObject()
            jo.put("type", "stride")
            jo.put("data", JSON.toJSONString(AccAndGyroData(FileUtils.accSDatalist, FileUtils.gyroSDataList)))

            FileUtils.writeToFile(jo.toString(), filePath)

            GzipUtil.compressForZip(filePath, filePath + ".gz")
        }
    }

    private fun writeStandData(filePath: String) {
        doAsync {
            var jo = JSONObject()

            jo.put("type", "stand")
            jo.put("Stand_L", JSON.toJSONString(AccAndGyroData(FileUtils.accLDatalist, FileUtils.gyroLDataList)))
            jo.put("Stand_R", JSON.toJSONString(AccAndGyroData(FileUtils.accRDatalist, FileUtils.gyroRDataList)))

            FileUtils.writeToFile(jo.toString(), filePath)

            GzipUtil.compressForZip(filePath, filePath + ".gz")
            finish()
        }
    }

    private fun writeSoundData(filePath: String) {
        doAsync {
            GzipUtil.compressForZip(filePath, filePath + ".gz")
        }
    }

    private fun writeTremorData(filePath: String) {
        doAsync {
            var jo = JSONObject()

            jo.put("type", "tremor")
            jo.put(mTremors[0], JSON.toJSONString(AccAndGyroData(FileUtils.tremor_lr_accdatalist, FileUtils.tremor_lr_gyrodatalist)))
            jo.put(mTremors[1], JSON.toJSONString(AccAndGyroData(FileUtils.tremor_lp_accdatalist, FileUtils.tremor_lp_gyrodatalist)))
            jo.put(mTremors[2], JSON.toJSONString(AccAndGyroData(FileUtils.tremor_rr_accdatalist, FileUtils.tremor_rr_gyrodatalist)))
            jo.put(mTremors[3], JSON.toJSONString(AccAndGyroData(FileUtils.tremor_rp_accdatalist, FileUtils.tremor_rp_gyrodatalist)))

            FileUtils.writeToFile(jo.toString(), filePath)

            GzipUtil.compressForZip(filePath, filePath + ".gz")
        }
    }

    /**
     * 把数据文件路径插入到数据库
     */
    fun insertDB(mark: String) {
        Log.i(TAG, "insertDB${mark}")
        val historyProvider = HistoryProvider(DataBaseHelper.getInstance(this))
        val history = History(Dua.getInstance().currentDuaId, modelName, FileUtils.filePath + ".gz", mark)
        historyProvider.InsertHistory(history)
    }

    var score = 0
    var score2 = 0
    var score3 = 0
    var score4 = 0

    var mOnItemSelecter = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            score = p2
        }
    }
    var mOnItemSelecter2 = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            score2 = p2
        }
    }

    var mOnItemSelecter3 = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            score3 = p2
        }
    }
    var mOnItemSelecter4 = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            score4 = p2
        }
    }

    var modelName: String? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back_v2)

        modelName = intent.getStringExtra("modelName")

        if (modelName.equals(ModuleHelper.MODULE_TREMOR)) {
            ll_score2.visibility = View.VISIBLE
            ll_score3.visibility = View.VISIBLE
            ll_score4.visibility = View.VISIBLE
        }

        tv_module_name.text = ModuleHelper.getName(this, modelName)
        tv_fb_name.text = Dua.getInstance().duaUser.name.replace("dua:", "")
        tv_fb_time.text = simpleDateFormat.format(Date())
        spinner_pd_level.onItemSelectedListener = mOnItemSelecter
        spinner_pd_level2.onItemSelectedListener = mOnItemSelecter2
        spinner_pd_level3.onItemSelectedListener = mOnItemSelecter3
        spinner_pd_level4.onItemSelectedListener = mOnItemSelecter4

        evaluation_guide_name.text = ModuleHelper.getEvaluationGuide(this, modelName)

        btn_save.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

}