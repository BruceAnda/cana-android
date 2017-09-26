package cn.ac.ict.cana.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.parkionsdatacollection.base.YouMengBaseActivity
import cn.ac.ict.cana.contant.GlobleData
import cn.ac.ict.cana.mode.AccData
import cn.ac.ict.cana.mode.GyroData
import cn.ac.ict.cana.mode.History
import cn.ac.ict.cana.modules.stand.StandTestActivity
import cn.ac.ict.cana.pagers.ExamPageFragment
import cn.ac.ict.cana.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide4.*
import java.util.*

/**
 * 站立平衡
 */
class ModelGuideActivity4 : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide4)

        if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
            btn_pre.visibility = View.GONE
            btn_skip.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_STAND)
        FileUtils.accLDatalist = ArrayList<AccData>()
        FileUtils.accRDatalist = ArrayList<AccData>()
        FileUtils.gyroLDataList = ArrayList<GyroData>()
        FileUtils.gyroRDataList = ArrayList<GyroData>()
    }

    fun start(view: View) {
        /* val intent = Intent(this@ModelGuideActivity4, StandMainActivity::class.java)
         intent.putExtra("level", 0)
         startActivity(intent)
         finish()*/
        val intent = Intent(this@ModelGuideActivity4, StandTestActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity4, ModelGuideActivity3::class.java))
            finish()
        }.setCancelable(false).show()
    }

    fun next(view: View) {
        startActivity(Intent(this, ModelGuideActivity5::class.java))
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }
}
