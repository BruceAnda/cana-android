package cn.ac.ict.canalib.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.modules.guide.*
import cn.ac.ict.canalib.modules.upload.UploadActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_feed_back_v2.*
import java.util.*

/**
 * 打分的Activity
 */

class ScoreActivity : BaseActivity(), View.OnClickListener {

    private val TAG = ScoreActivity::class.java.simpleName

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_cancel -> {
                SweetAlertDialog(this@ScoreActivity, SweetAlertDialog.WARNING_TYPE)
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
            }
            R.id.btn_save -> {
                var fileName: String
                if (ModuleHelper.MODULE_TREMOR.equals(modelName)) {
                    Log.i(TAG, "插入${modelName}数据")
                    val datalp = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_TREMOR_LP, datalp)

                    val datalr = "{\"score\":\"${score2}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_TREMOR_LR, datalr)

                    val datarp = "{\"score\":\"${score3}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_TREMOR_RP, datarp)

                    val datarr = "{\"score\":\"${score4}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_TREMOR_RR, datarr)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("即将进入声音检测").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            // 进入下一项测试，保存数据
                            startActivity(Intent(this, ModelGuideActivity3::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 语言能力
                } else if (ModuleHelper.MODULE_SOUND.equals(modelName)) {
                    Log.i(TAG, "插入${modelName}数据")
                    var data = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.wav\"}"

                    updataHistory(ModuleHelper.MODULE_DATATYPE_SOUND, data)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("即将进入站立平衡检测").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity4::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 单腿站立
                } else if (ModuleHelper.MODULE_STAND.equals(modelName)) {

                    var datal = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_STAND_L, datal)

                    var datar = "{\"score\":\"${score2}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_STAND_R, datar)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {

                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("即将进入行走平衡测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity5::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 行走平衡
                } else if (ModuleHelper.MODULE_STRIDE.equals(modelName)) {
                    Log.i(TAG, "插入${modelName}数据")

                    var data = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"

                    updataHistory(ModuleHelper.MODULE_DATATYPE_STRIDE, data)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("即将进入手指灵敏测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity6::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 手指灵敏
                } else if (ModuleHelper.MODULE_TAPPER.equals(modelName)) {

                    var datal = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_TAPPING_L, datal)

                    var datar = "{\"score\":\"${score2}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_TAPPING_R, datar)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("即将进入面部表情测试。").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity7::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                    // 面部表情
                } else if (ModuleHelper.MODULE_FACE.equals(modelName)) {

                    var data = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.mp4\"}"

                    updataHistory(ModuleHelper.MODULE_DATATYPE_FACE, data)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("即将进入手臂下垂测试").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, ModelGuideActivity8::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                } else if (ModuleHelper.MODULE_ARM_DROOP.equals(modelName)) {

                    var datal = "{\"score\":\"${score}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_ARMDROOP_L, datal)

                    var datar = "{\"score\":\"${score2}\"," +
                            "\"doctor\":\"${Dua.getInstance().duaUser.name}\"," +
                            "\"patient\":\"${FileUtils.PATIENT_NAME}\"," +
                            "\"patient_age\":\"${FileUtils.PATIENT_AGE}\"," +
                            "\"patient_sex\":\"${FileUtils.PATIENT_SEX}\"," +
                            "\"patient_med\":\"${FileUtils.PATIENT_MEDICINE}\"," +
                            "\"onoff\":\"${FileUtils.SWITCHING_PERIOD}\"," +
                            "\"filever\":\"${1}\"," +
                            "\"file\":\"Parkins/${MD5.md5("${Dua.getInstance().currentDuaUid}${System.currentTimeMillis()}${UUID.randomUUID()}")}.txt\"}"
                    updataHistory(ModuleHelper.MODULE_DATATYPE_ARMDROOP_R, datar)
                    if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
                        startActivity(Intent(this@ScoreActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage("恭喜你完成了测试，请上传数据我们会对您的康复情况进行分析。").setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(this, UploadActivity::class.java))
                            finish()
                        }).setCancelable(false).show()
                    }
                }
            }
        }
    }

    /**
     * 把数据文件路径插入到数据库
     */
    fun updataHistory(type: String, mark: String) {
        Log.i(TAG, "updataHistory$mark")
        database.use {
            // 历史数据
            val values = ContentValues()
            values.put(HistoryData.MARK, mark)
            update(HistoryData.TABLE_NAME, values, "${HistoryData.BATCH} = ? and ${HistoryData.TYPE} = ?", arrayOf(FileUtils.batch, type))
        }
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back_v2)

        modelName = intent.getStringExtra("modelName")

        if (modelName.equals(ModuleHelper.MODULE_TREMOR)) {
            ll_score2.visibility = View.VISIBLE
            ll_score3.visibility = View.VISIBLE
            ll_score4.visibility = View.VISIBLE
            tv_score_tip.text = "右手静止性震颤"
            tv_score_tip2.text = "左手静止性震颤"
            tv_score_tip3.text = "右手运动性震颤"
            tv_score_tip4.text = "左手运动性震颤"
        } else if (modelName.equals(ModuleHelper.MODULE_STAND)) {
            ll_score2.visibility = View.VISIBLE
            tv_score_tip.text = "右腿站立平衡"
            tv_score_tip2.text = "左腿站立平衡"
        } else if (modelName.equals(ModuleHelper.MODULE_TAPPER)) {
            ll_score2.visibility = View.VISIBLE
            tv_score_tip.text = "右手灵敏"
            tv_score_tip2.text = "左手灵敏"
        } else if (modelName.equals(ModuleHelper.MODULE_ARM_DROOP)) {
            ll_score2.visibility = View.VISIBLE
            tv_score_tip.text = "右手手臂下垂"
            tv_score_tip2.text = "左手手臂下垂"
        }

        tv_module_name.text = ModuleHelper.getName(this, modelName)

        spinner_pd_level.onItemSelectedListener = mOnItemSelecter
        spinner_pd_level2.onItemSelectedListener = mOnItemSelecter2
        spinner_pd_level3.onItemSelectedListener = mOnItemSelecter3
        spinner_pd_level4.onItemSelectedListener = mOnItemSelecter4

        evaluation_guide_name.text = ModuleHelper.getEvaluationGuide(this, modelName)

        btn_save.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

}