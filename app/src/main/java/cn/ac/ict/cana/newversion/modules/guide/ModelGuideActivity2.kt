package cn.ac.ict.cana.newversion.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.mode.AccData
import cn.ac.ict.cana.newversion.mode.GyroData
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.tremor.TremorMainActivity
import cn.ac.ict.cana.newversion.utils.FileUtils
import java.util.*

/**
 * 震颤测试
 */
class ModelGuideActivity2 : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide2)
    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_TREMOR)
        FileUtils.tremor_lr_accdatalist = ArrayList<AccData>()
        FileUtils.tremor_lr_gyrodatalist = ArrayList<GyroData>()

        FileUtils.tremor_lp_accdatalist = ArrayList<AccData>()
        FileUtils.tremor_lp_gyrodatalist = ArrayList<GyroData>()

        FileUtils.tremor_rr_accdatalist = ArrayList<AccData>()
        FileUtils.tremor_rr_gyrodatalist = ArrayList<GyroData>()

        FileUtils.tremor_rp_accdatalist = ArrayList<AccData>()
        FileUtils.tremor_rp_gyrodatalist = ArrayList<GyroData>()
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
