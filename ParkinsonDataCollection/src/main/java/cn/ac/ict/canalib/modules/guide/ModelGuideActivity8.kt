package cn.ac.ict.canalib.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.mode.History
import cn.ac.ict.canalib.modules.armdroop.ArmDroopTestActivity
import cn.ac.ict.canalib.modules.upload.UploadActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.ModelGuideBaseActivity
import cn.ac.ict.canalib.common.ArmDroop
import cn.ac.ict.canalib.common.ArmDroopData
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide4.*
import java.util.ArrayList

/**
 * 手臂下垂
 */
class ModelGuideActivity8 : ModelGuideBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide8)


        init()
    }

    private fun init() {
        handlerMenu()
        handlerSound()
    }

    private fun handlerMenu() {
        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            btn_pre.visibility = View.GONE
            btn_skip.visibility = View.GONE
        }
    }

    private fun handlerSound() {
        createMediaPlayer(R.raw.guide8)
    }

    override fun onResume() {
        super.onResume()
        handlerFile()
    }

    private fun handlerFile() {
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_STAND)
        FileUtils.armDroopL = ArmDroop("ArmDroop_L", ArmDroopData(ArrayList(), ArrayList()))
        FileUtils.armDroopR = ArmDroop("ArmDroop_R", ArmDroopData(ArrayList(), ArrayList()))
        FileUtils.armDroopData = ArmDroopData(ArrayList(), ArrayList())
    }

    fun start(view: View) {
        val intent = Intent(this@ModelGuideActivity8, ArmDroopTestActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity8, ModelGuideActivity7::class.java))
            finish()
        }.setCancelable(false).show()
    }

    fun next(view: View) {
        startActivity(Intent(this, UploadActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }
}
