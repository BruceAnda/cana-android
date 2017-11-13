package cn.ac.ict.canalib.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.mode.History
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.Stand
import cn.ac.ict.canalib.common.StandData
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.modules.modulesnew.stand.StandTestActivity
import cn.ac.ict.canalib.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide4.*
import kotlin.collections.ArrayList

/**
 * 站立平衡
 */
class ModelGuideActivity4 : AudioBaseActivity() {

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
        setContentView(R.layout.activity_model_guide4)

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
        createMediaPlayer(R.raw.guide4)
    }

    override fun onResume() {
        super.onResume()
        handlerFile()
        play()
    }

    private fun handlerFile() {
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_STAND)
        FileUtils.standL = Stand("Stand_L", StandData(ArrayList(), ArrayList()))
        FileUtils.standR = Stand("Stand_R", StandData(ArrayList(), ArrayList()))
        FileUtils.standData = StandData(ArrayList(), ArrayList())
    }

    fun start(view: View) {
        /* val intent = Intent(this@ModelGuideActivity4, StandMainActivity::class.java)
         intent.putExtra("level", 0)
         startActivity(intent)
         finish()*/
        FileUtils.hasTestFour = true
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
