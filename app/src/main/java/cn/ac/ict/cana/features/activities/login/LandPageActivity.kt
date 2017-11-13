package cn.ac.ict.cana.features.activities.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import cn.ac.ict.canalib.common.ParkinsDataCollection
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.DuaCallback
import com.lovearthstudio.duasdk.DuaConfig
import com.lovearthstudio.duasdk.util.ComUtil
import kotlinx.android.synthetic.main.activity_land_page.*
import org.json.JSONObject

/**
 * 登录的开始页面
 */
class LandPageActivity : AudioBaseActivity() {

    private val TAG = LandPageActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_land_page)
    }

    /**
     * 跳转到登录
     */
    fun toLogin(view: View) {
        rl_login.visibility = View.VISIBLE
        rl_land.visibility = View.GONE
    }

    /**
     * 跳转到注册
     */
    fun toRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("forgetPassword", true)
        startActivity(intent)
        finish()
    }

    /**
     * 忘记密码
     */
    fun forgotPassword(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }

    /**
     * 登录
     */
    fun login(view: View) {
        val phone = et_phone_num.text.toString()
        val password = et_password.text.toString()
        if (TextUtils.isEmpty(phone)) {
            tv_forgot_password.text = "用户信息不正确，忘记密码"
            tv_forgot_password.setTextColor(Color.YELLOW)
            return
        }
        if (TextUtils.isEmpty(password)) {
            tv_forgot_password.text = "用户信息不正确，忘记密码"
            tv_forgot_password.setTextColor(Color.YELLOW)
            return
        }

        Dua.getInstance().login("+86-" + phone, password, "Dua.Default", object : DuaCallback {
            override fun onSuccess(s: Any) {
                // handler.sendEmptyMessage(STOP_ANIM);
                // activity.onLoginOk()
                Log.i(TAG, s.toString())
                updateLocalProfile()
            }

            override fun onError(status: Int, s: String) {
                // handler.sendEmptyMessage(STOP_ANIM);
                var reason: String? = DuaConfig.errCode[status]
                if (reason == null) {
                    reason = s
                }

                tv_forgot_password.text = "用户信息不正确，忘记密码"
                tv_forgot_password.setTextColor(Color.YELLOW)
            }
        })
    }

    /**
     * 更新本地SharedPerferences用户信息
     */
    private fun updateLocalProfile() {
        val array = ComUtil.listToJSONArray("bday", "sex", "name", "avatar", "height", "weight", "saying")
        Dua.getInstance().getUserProfile(array, object : DuaCallback {
            override fun onSuccess(result: Any) {
                updateProfileSuccess(result)
            }

            override fun onError(status: Int, reason: String) {
                updateProfileFailer()
            }
        })
    }

    /**
     * 更新Profile成功
     */
    private fun updateProfileSuccess(result: Any) {
        try {
            val bday = (result as JSONObject).optString("bday")
            val sex = result.optString("sex")
            val name = result.optString("name")
            val avatar = result.optString("avatar")
            val height = result.optString("height")
            val weight = result.optString("weight")
            val saying = result.optString("saying")

            getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("bday", bday)
                    .putString("sex", sex)
                    .putString("name", name.replace("dua:", ""))
                    .putString("avatar", avatar)
                    .putString("height", height)
                    .putString("weight", weight)
                    .putString("saying", saying).commit()

            ParkinsDataCollection.uiIntent.loginUpdateProfileSuccess(this@LandPageActivity)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 更新Profile失败
     */
    private fun updateProfileFailer() {
        ParkinsDataCollection.uiIntent.loginUpdateProfileFailer(this@LandPageActivity)
        finish()
    }

    /**
     * 返回
     */
    fun back(view: View) {
        rl_login.visibility = View.GONE
        rl_land.visibility = View.VISIBLE
    }
}
