package cn.ac.ict.cana.modules.guide

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.helpers.ModuleHelper
import cn.ac.ict.cana.parkionsdatacollection.base.YouMengBaseActivity
import cn.ac.ict.cana.contant.GlobleData
import cn.ac.ict.cana.mode.CountData
import cn.ac.ict.cana.mode.History
import cn.ac.ict.cana.modules.count.CountGameActivity
import cn.ac.ict.cana.pagers.ExamPageFragment
import cn.ac.ict.cana.utils.FileUtils
import kotlinx.android.synthetic.main.activity_model_guide.*
import java.util.*

/**
 * 数字记忆
 */
class ModelGuideActivity : YouMengBaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_guide)

        if (GlobleData.menu_type == ExamPageFragment.MENU_TYPE_SINGLE) {
            btn_skip.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_COUNT)
        FileUtils.countDataList = ArrayList<CountData>()

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
