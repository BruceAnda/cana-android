package cn.ac.ict.canalib.modules.guide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.mode.History
import cn.ac.ict.canalib.modules.tapper.TapperTestActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.Tapping
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_model_guide6.*
import kotlin.collections.ArrayList

/**
 * 手指灵敏
 */
class ModelGuideActivity6 : AudioBaseActivity() {

    override fun onPause() {
        super.onPause()

        pasue()
    }

    override fun onStop() {
        super.onStop()

        stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide6)

        init()

        var controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res://" + packageName + "/" + R.drawable.tapping))
                .setAutoPlayAnimations(true)
                .build()
        model_guide6.controller = controller

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
        createMediaPlayer(R.raw.guide6)
    }

    override fun onResume() {
        super.onResume()
        handlerFile()
        play()
    }

    private fun handlerFile() {
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_TAPPER)
        FileUtils.tappingL = Tapping("Tapping_L", ArrayList())
        FileUtils.tappingR = Tapping("Tapping_R", ArrayList())
    }

    fun start(view: View) {
        /*val intent = Intent(this@ModelGuideActivity6, TapperMainActivity::class.java)
        intent.putExtra("level", 0)
        startActivity(intent)
        finish()*/
        FileUtils.hasTestSix = true
        startActivity(Intent(this@ModelGuideActivity6, TapperTestActivity::class.java))
        finish()
    }

    fun pre(view: View) {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which ->
            startActivity(Intent(this@ModelGuideActivity6, ModelGuideActivity5::class.java))
            finish()
        }.setCancelable(false).show()
    }

    fun next(view: View) {
        startActivity(Intent(this, ModelGuideActivity7::class.java))
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }
}
