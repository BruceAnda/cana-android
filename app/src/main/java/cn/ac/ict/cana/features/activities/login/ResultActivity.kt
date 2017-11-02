package cn.ac.ict.cana.features.activities.login

import android.os.Bundle
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.common.ParkinsDataCollection

/**
 * 显示结果的页面
 */
class ResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置内容布局
        setContentView(R.layout.activity_result)
    }

    /**
     * 测试按钮点击
     */
    fun toTest(view: View) {
        ParkinsDataCollection.uiIntent.toTest(this@ResultActivity)
        finish()
    }

    /**
     * 自定义用户信息按钮点击
     */
    fun toCustomInfo(view: View) {
        ParkinsDataCollection.uiIntent.toCustomProfileInfo(this@ResultActivity)
        finish()
    }
}
