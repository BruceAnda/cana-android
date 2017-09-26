package cn.ac.ict.cana.newversion.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.contant.GlobleData
import cn.ac.ict.cana.newversion.mode.AccData
import cn.ac.ict.cana.newversion.mode.GyroData
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.armdroop.ArmDroopTestActivity
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity
import cn.ac.ict.cana.newversion.pagers.ExamPageFragment
import cn.ac.ict.cana.newversion.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide4.*
import java.util.ArrayList

/**
 * 手臂下垂
 */
class ModelGuideActivity8 : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide8)

        if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
            btn_pre.visibility = View.GONE
            btn_skip.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_STAND)
        FileUtils.accArmDroopLDatalist = ArrayList<AccData>()
        FileUtils.accArmDroopRDatalist = ArrayList<AccData>()
        FileUtils.gyroArmDroopLDataList = ArrayList<GyroData>()
        FileUtils.gyroArmDroopRDataList = ArrayList<GyroData>()
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
