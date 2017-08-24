package cn.ac.ict.cana.newversion.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.sound.SoundMainActivity
import cn.ac.ict.cana.newversion.utils.FileUtils

/**
 * 语言测试
 */
class ModelGuideActivity3 : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide3)
    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_SOUND)
    }

    fun start(view: View) {
        startActivity(Intent(this@ModelGuideActivity3, SoundMainActivity::class.java))
        finish()
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity3, ModelGuideActivity2::class.java))
            finish()
        }.setCancelable(false).show()
    }

    fun next(view: View) {
        startActivity(Intent(this, ModelGuideActivity4::class.java))
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }
}
