package cn.ac.ict.cana.newversion.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.activities.MainActivityNew
import cn.ac.ict.cana.parkionsdatacollection.base.YouMengBaseActivity

/**
 * 显示结果的页面
 */
class ResultActivity : YouMengBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
    }

    /**
     * 跳转到测试页面
     */
    fun toTest(view: View) {
        startActivity(Intent(this@ResultActivity, MainActivityNew::class.java))
        finish()
    }

    /**
     * 跳转到自定义信息页面
     */
    fun toCustomInfo(view: View) {
        val intent = Intent(this@ResultActivity, MainActivityNew::class.java)
        intent.putExtra("page", 1)
        startActivity(intent)
        finish()
    }
}
