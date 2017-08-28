package cn.ac.ict.cana.newversion.modules.guide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.contant.GlobleData
import cn.ac.ict.cana.newversion.mode.History
import cn.ac.ict.cana.newversion.modules.tapper_v2.TapperTestActivity
import cn.ac.ict.cana.newversion.pagers.ExamPageFragment
import cn.ac.ict.cana.newversion.utils.FileUtils
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_model_guide6.*
import java.util.*

/**
 * 手指灵敏
 */
class ModelGuideActivity6 : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide6)

        if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
            btn_pre.visibility = View.GONE
            btn_skip.visibility = View.GONE
        }

        var controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res://" + packageName + "/" + R.drawable.tapping))
                .setAutoPlayAnimations(true)
                .build()
        model_guide6.controller = controller

    }

    override fun onResume() {
        super.onResume()
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_TAPPER)

        FileUtils.tapperLDatas = ArrayList()
        FileUtils.tapperRDatas = ArrayList()
    }

    fun start(view: View) {
        /*val intent = Intent(this@ModelGuideActivity6, TapperMainActivity::class.java)
        intent.putExtra("level", 0)
        startActivity(intent)
        finish()*/
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
