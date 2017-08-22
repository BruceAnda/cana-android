package cn.ac.ict.cana.newversion.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import cn.ac.ict.cana.R
import cn.ac.ict.cana.base.YouMengBaseActivity
import cn.ac.ict.cana.helpers.DataBaseHelper
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.guide.*
import cn.ac.ict.cana.newversion.modules.tremor.TremorMainActivity
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity
import cn.ac.ict.cana.newversion.provider.HistoryProvider
import cn.ac.ict.cana.newversion.utils.FileUtils
import cn.ac.ict.cana.newversion.utils.GzipUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 返回的Activity
 */

class FeedBackActivity : YouMengBaseActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private val mTremors = arrayOf("Tremor_LR", "Tremor_LP", "Tremor_RR", "Tremor_RP")
    private val mStands = arrayOf("Stand_L", "Stand_R")
    private val mTappers = arrayOf("Tapping_L", "Tapping_R")

    private val mTermorsTitle = arrayOf("即将进入左手运动性震颤检测", "即将进入右手静止性震颤检测", "即将进入右手运动性震颤检测", "即将进入声音检测")
    private val mStandsTitle = arrayOf("即将进入右脚站立测试", "即将进入行走平衡测试")
    private val mTappersTitle = arrayOf("即将进入右手手指灵敏测试", "恭喜你完成了测试，请上传数据我们会对您的康复情况进行分析。")

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
                    val grade = intent.getIntExtra("grade", 0)
                    when (grade) {
                        1 -> {
                            FileUtils.score_lefthand_still = score
                        }
                        2 -> {
                            FileUtils.score_lefthand_motion = score
                        }
                        3 -> {
                            FileUtils.score_righthand_still = score
                        }
                        4 -> {
                            FileUtils.score_righthand_motion = score
                            Log.i(TAG, "插入${modelName}数据")
                            val data = "{\"score\":\"${FileUtils.score_lefthand_still},${FileUtils.score_lefthand_motion},${FileUtils.score_righthand_still},${FileUtils.score_righthand_motion}\"," +
                                    "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                                    "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                                    "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                                    "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                                    "\"patient_medicine\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                                    "\"switching_period\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                                    "\"file\":\"${fileName}\"}"
                            insertDB(data)
                        }
                    }
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
                        // 进入下一项测试，保存数据
                        startActivity(Intent(this, ModelGuideActivity2::class.java))
                        doAsync {
                            var jo = JSONObject()
                            jo.put("data", JSON.toJSONString(FileUtils.countDataList))
                            val toString = jo.toString()
                            println("countData:" + toString)
                            FileUtils.writeToFile(toString)

                            GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")

                            finish()
                        }
                    }).setCancelable(false).show()
                    // 震颤
                } else if (ModuleHelper.MODULE_TREMOR.equals(modelName)) {
                    val grade = intent.getIntExtra("grade", 0)
                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage(mTermorsTitle[grade - 1]).setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        if (grade < 4) {
                            val intent = Intent(this, TremorMainActivity::class.java)
                            intent.putExtra("grade", grade)
                            startActivity(intent)
                        } else {
                            // 进入下一项测试，保存数据
                            startActivity(Intent(this, ModelGuideActivity3::class.java))
                            doAsync {
                                var jo = JSONObject()

                                var tremorLrData = JSONObject()
                                tremorLrData.put("type", mTremors[0])
                                tremorLrData.put("acc", JSON.toJSONString(FileUtils.tremor_lr_accdatalist))
                                tremorLrData.put("gyro", JSON.toJSONString(FileUtils.tremor_lr_gyrodatalist))

                                var tremorLpData = JSONObject()
                                tremorLpData.put("type", mTremors[1])
                                tremorLpData.put("acc", JSON.toJSONString(FileUtils.tremor_lp_accdatalist))
                                tremorLpData.put("gyro", JSON.toJSONString(FileUtils.tremor_lp_gyrodatalist))

                                var tremorRrData = JSONObject()
                                tremorRrData.put("type", mTremors[2])
                                tremorRrData.put("acc", JSON.toJSONString(FileUtils.tremor_rr_accdatalist))
                                tremorRrData.put("gyro", JSON.toJSONString(FileUtils.tremor_rr_gyrodatalist))

                                var tremorRpData = JSONObject()
                                tremorRpData.put("type", mTremors[3])
                                tremorRpData.put("acc", JSON.toJSONString(FileUtils.tremor_rp_accdatalist))
                                tremorRpData.put("gyro", JSON.toJSONString(FileUtils.tremor_rp_gyrodatalist))

                                var data = JSONObject()
                                data.put(mTremors[0], tremorLrData.toString())
                                data.put(mTremors[1], tremorLpData.toString())
                                data.put(mTremors[2], tremorRrData.toString())
                                data.put(mTremors[3], tremorRpData.toString())

                                jo.put("data", data.toString())

                                FileUtils.writeToFile(jo.toString())

                                GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")
                            }
                        }
                        finish()
                    }).setCancelable(false).show()
                    // 语言能力
                } else if (ModuleHelper.MODULE_SOUND.equals(modelName)) {

                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入站立平衡检测").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(this, ModelGuideActivity4::class.java))
                        doAsync {
                            GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")
                            finish()
                        }
                    }).setCancelable(false).show()
                    // 单腿站立
                } else if (ModuleHelper.MODULE_STAND.equals(modelName)) {


                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入行走平衡测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(this, ModelGuideActivity5::class.java))
                        doAsync {
                            var jo = JSONObject()
                            var standL = JSONObject()
                            standL.put("type", mStands[0])
                            standL.put("acc", JSON.toJSONString(FileUtils.accLDatalist))
                            standL.put("gyro", JSON.toJSONString(FileUtils.gyroLDataList))

                            var standR = JSONObject()
                            standR.put("type", mStands[1])
                            standR.put("acc", JSON.toJSONString(FileUtils.accRDatalist))
                            standR.put("gyro", JSON.toJSONString(FileUtils.gyroRDataList))

                            var data = JSONObject()
                            data.put("Stand_L", standL.toString())
                            data.put("Stand_R", standR.toString())

                            jo.put("data", data.toString())
                            FileUtils.writeToFile(jo.toString())

                            GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")
                            finish()
                        }
                    }).setCancelable(false).show()
                    // 行走平衡
                } else if (ModuleHelper.MODULE_STRIDE.equals(modelName)) {
                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入手指灵敏测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(this, ModelGuideActivity6::class.java))
                        doAsync {
                            var jo = JSONObject()
                            jo.put("type", "Stride")
                            var data = JSONObject()
                            data.put("acc", JSON.toJSONString(FileUtils.accSDatalist))
                            data.put("gyro", JSON.toJSONString(FileUtils.gyroSDataList))
                            jo.put("data", data.toString())
                            FileUtils.writeToFile(jo.toString())

                            GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")

                            finish()
                        }
                    }).setCancelable(false).show()
                    // 手指灵敏
                } else if (ModuleHelper.MODULE_TAPPER.equals(modelName)) {
                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("即将进入面部表情测试。").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(this, ModelGuideActivity7::class.java))
                        doAsync {
                            var jo = JSONObject()
                            var tapperL = JSONObject()
                            tapperL.put("type", mTappers[0])
                            tapperL.put("data", JSON.toJSONString(FileUtils.tapperLDatas))

                            var tapperR = JSONObject()
                            tapperR.put("type", mTappers[1])
                            tapperR.put("data", JSON.toJSONString(FileUtils.tapperRDatas))

                            var data = JSONObject()
                            data.put(mTappers[0], tapperL)
                            data.put(mTappers[1], tapperR)
                            jo.put("data", data)

                            FileUtils.writeToFile(jo.toString())

                            GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")
                            finish()
                        }
                    }).setCancelable(false).show()
                    // 面部表情
                } else if (ModuleHelper.MODULE_FACE.equals(modelName)) {

                    AlertDialog.Builder(this@FeedBackActivity).setTitle("提示").setMessage("恭喜你完成了测试，请上传数据我们会对您的康复情况进行分析。").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(this, UploadActivity::class.java))
                        doAsync {
                            GzipUtil.compressForZip(FileUtils.filePath, FileUtils.filePath + ".gz")
                            finish()
                        }
                    }).setCancelable(false).show()
                }
            }
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        score = position
    }

    var modelName: String? = null
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back_new)

        modelName = intent.getStringExtra("modelName")
        tv_module_name.text = ModuleHelper.getName(this, modelName)
        tv_fb_name.text = Dua.getInstance().duaUser.name.replace("dua:", "")
        tv_fb_time.text = simpleDateFormat.format(Date())
        spinner_pd_level.onItemSelectedListener = this

        evaluation_guide_name.text = ModuleHelper.getEvaluationGuide(this, modelName)

        btn_save.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    companion object {
        private val TAG = FeedBackActivity::class.java.simpleName
    }
}
