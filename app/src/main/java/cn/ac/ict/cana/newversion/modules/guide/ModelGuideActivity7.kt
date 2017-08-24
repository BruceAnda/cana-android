package cn.ac.ict.cana.newversion.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.face.FaceMainActivity
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity
import cn.ac.ict.cana.newversion.utils.FileUtils

/**
 * 面部表情
 */
class ModelGuideActivity7 : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide7)
    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_FACE)
    }

    fun start(view: View) {
        val intent = Intent(this@ModelGuideActivity7, FaceMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity7, ModelGuideActivity6::class.java))
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
