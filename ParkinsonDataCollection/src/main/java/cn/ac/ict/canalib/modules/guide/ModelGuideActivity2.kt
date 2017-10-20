package cn.ac.ict.canalib.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.mode.History
import cn.ac.ict.canalib.modules.tremor.TremorMainActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.common.Tremor
import cn.ac.ict.canalib.common.TremorData
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide2.*
import kotlin.collections.ArrayList

/**
 * 震颤测试
 */
class ModelGuideActivity2 : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide2)

        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            btn_pre.visibility = View.GONE
            btn_skip.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_TREMOR)
        FileUtils.tremorRR = Tremor(ModuleHelper.MODULE_DATATYPE_TREMOR_RR, TremorData(ArrayList(), ArrayList()))
        FileUtils.tremorRP = Tremor(ModuleHelper.MODULE_DATATYPE_TREMOR_RP, TremorData(ArrayList(), ArrayList()))
        FileUtils.tremorLR = Tremor(ModuleHelper.MODULE_DATATYPE_TREMOR_LR, TremorData(ArrayList(), ArrayList()))
        FileUtils.tremorLP = Tremor(ModuleHelper.MODULE_DATATYPE_TREMOR_LP, TremorData(ArrayList(), ArrayList()))
        FileUtils.tremorData = TremorData(ArrayList(), ArrayList())

    }

    fun start(view: View) {
        val intent = Intent(this@ModelGuideActivity2, TremorMainActivity::class.java)
        intent.putExtra("grade", 0)
        startActivity(intent)
        finish()
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity2, ModelGuideActivity::class.java))
            finish()
        }.setCancelable(false).show()
    }

    fun next(view: View) {
        startActivity(Intent(this, ModelGuideActivity3::class.java))
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }
}