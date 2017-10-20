package cn.ac.ict.canalib.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.constant.GlobleData
import cn.ac.ict.canalib.mode.History
import cn.ac.ict.canalib.modules.count.CountGameActivity
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.common.Memory
import cn.ac.ict.canalib.helpers.MenuHelper
import cn.ac.ict.canalib.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide.*
import kotlin.collections.ArrayList

/**
 * 数字记忆
 */
class ModelGuideActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide)

        if (GlobleData.menu_type == MenuHelper.MENU_TYPE_SINGLE) {
            btn_skip.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_COUNT)
        FileUtils.memory = Memory("Memory", ArrayList())
    }

    /**
     * 开始测试
     */
    fun start(view: View) {
        val intent = Intent(this@ModelGuideActivity, CountGameActivity::class.java)
        intent.putExtra("grade", 3)
        startActivity(intent)
        finish()
    }

    /**
     * 下一项测试
     */
    fun next(view: View) {
        startActivity(Intent(this, ModelGuideActivity2::class.java))
        finish()
    }

    /**
     * 返回键按下
     */
    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定") { dialog, which -> finish() }.setCancelable(false).show()
    }
}