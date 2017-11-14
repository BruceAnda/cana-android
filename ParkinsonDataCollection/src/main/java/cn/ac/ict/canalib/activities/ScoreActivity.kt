package cn.ac.ict.canalib.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.modules.guide.*
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.extensions.toReport
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_feed_back_v2.*
import java.util.*

/**
 * 打分的Activity
 */

class ScoreActivity : AudioBaseActivity(), View.OnClickListener {

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
                if (ModuleHelper.MODULE_TREMOR.equals(modelName)) {
                    FileUtils.tremorRRScore = "${score}"
                    FileUtils.tremorLRScore = "${score2}"
                    FileUtils.tremorRPScore = "${score3}"
                    FileUtils.tremorLPScore = "${score4}"

                    toNext("即将进入声音检测")
                    // 语言能力
                } else if (ModuleHelper.MODULE_SOUND.equals(modelName)) {
                    FileUtils.soundScore = "$score"
                    toNext("即将进入站立平衡检测")

                    // 单腿站立
                } else if (ModuleHelper.MODULE_STAND.equals(modelName)) {
                    FileUtils.standScore = "$score"
                    toNext("即将进入行走平衡测试")

                    // 行走平衡
                } else if (ModuleHelper.MODULE_STRIDE.equals(modelName)) {
                    FileUtils.strideScore = "$score"
                    toNext("即将进入手指灵敏测试")

                    // 手指灵敏
                } else if (ModuleHelper.MODULE_TAPPER.equals(modelName)) {
                    FileUtils.tappingRScore = "$score"
                    FileUtils.tappingLScore = "$score2"
                    toNext("即将进入面部表情测试")

                    // 面部表情
                } else if (ModuleHelper.MODULE_FACE.equals(modelName)) {
                    FileUtils.faceScore = "$score"
                    toNext("即将进入手臂下垂测试")

                    // 手臂下垂
                } else if (ModuleHelper.MODULE_ARM_DROOP.equals(modelName)) {
                    FileUtils.armDroopRScore = "$score"
                    FileUtils.armDroopLScore = "$score2"
                    toNext("恭喜你完成了测试，请上传数据我们会对您的康复情况进行分析。")
                }
            }
        }
    }

    /**
     * 确定按钮操作，进入下一步测试或跳转到报告页面
     */
    private fun toNext(msg: String) {
        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            toReport()
            finish()
        } else {
            AlertDialog.Builder(this@ScoreActivity).setTitle("提示").setMessage(msg).setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                // 进入下一项测试，保存数据
                when (modelName) {
                    ModuleHelper.MODULE_COUNT -> {
                        startActivity(Intent(this, ModelGuideActivity2::class.java))
                    }
                    ModuleHelper.MODULE_TREMOR -> {
                        startActivity(Intent(this, ModelGuideActivity3::class.java))
                    }
                    ModuleHelper.MODULE_SOUND -> {
                        startActivity(Intent(this, ModelGuideActivity4::class.java))
                    }
                    ModuleHelper.MODULE_STAND -> {
                        startActivity(Intent(this, ModelGuideActivity5::class.java))
                    }
                    ModuleHelper.MODULE_STRIDE -> {
                        startActivity(Intent(this, ModelGuideActivity6::class.java))
                    }
                    ModuleHelper.MODULE_TAPPER -> {
                        startActivity(Intent(this, ModelGuideActivity7::class.java))
                    }
                    ModuleHelper.MODULE_FACE -> {
                        startActivity(Intent(this, ModelGuideActivity8::class.java))
                    }
                    ModuleHelper.MODULE_ARM_DROOP -> {
                       toReport()
                    }
                }

                finish()
            }).setCancelable(false).show()
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
            tv_score_tip.text = "站立平衡"
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