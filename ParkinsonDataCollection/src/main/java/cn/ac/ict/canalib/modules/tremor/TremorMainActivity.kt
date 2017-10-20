package cn.ac.ict.canalib.modules.tremor

import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity2
import cn.ac.ict.canalib.R
import kotlinx.android.synthetic.main.activity_tremor_main2.*

/**
 * 震颤主页面
 */
class TremorMainActivity : BaseActivity() {

    private var isAction = false
    private val tipsTitle = arrayOf("静止性震颤", "运动性震颤")
    private val tipsContent = arrayOf("静坐在椅子上，手持手机自然下垂放在腿上，保持完全放松状态。如图所示。", "静坐在椅子上，手持手机自然下垂放在腿上，保持完全放松状态。如图所示。")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tremor_main2)
        isAction = intent.extras.getBoolean("isAction", false)
        if (isAction) {
            tv_termor_title.text = tipsTitle[1]
            tv_tremor_tips.text = tipsContent[1]
        } else {
            tv_termor_title.text = tipsTitle[0]
            tv_tremor_tips.text = tipsContent[0]
        }
    }

    /**
     * 开始测试
     */
    fun startTest(view: View) {
        val target = Intent(this@TremorMainActivity, TremorTestActivity::class.java)
        target.putExtra("isAction", isAction)
        startActivity(target)
        finish()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity2::class.java))
        finish()
    }
}
