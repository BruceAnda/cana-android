package cn.ac.ict.cana.newversion.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.ac.ict.cana.R
import cn.ac.ict.cana.parkionsdatacollection.base.YouMengBaseActivity
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.DuaCallback
import com.lovearthstudio.duasdk.DuaConfig
import kotlinx.android.synthetic.main.activity_register_phone.*
import java.util.regex.Pattern

/**
 * 注册页面
 */
class RegisterActivity : YouMengBaseActivity() {

    private val TAG = RegisterActivity::class.java.simpleName

    private var isForgetPassword = false

    private var status = 0 // 当前状态，用来更新UI 0 输入手机号； 1 输入验证码；2 输入密码
    private val textHint = arrayOf("手机号", "验证码", "输入密码")
    private val inputHint = arrayOf("输入手机号", "输入验证码", "输入密码")
    private val btnText = arrayOf("发送验证码", "确认", "确认")
    private var phone: String? = null
    private var vfCode: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_phone)

        if (intent.extras != null) {
            isForgetPassword = intent.extras.getBoolean("forgetPassword", false)
        }

        changUI()
    }

    /**
     * 第一个back
     */
    fun back(view: View) {
        if (status > 0) {
            status--
            changUI()
        } else {
            startActivity(Intent(this, LandPageActivity::class.java))
            finish()
        }
    }

    /**
     * 操作
     */
    fun operate(view: View) {
        val input = et_input.text.toString()
        if (TextUtils.isEmpty(input)) {
            et_input.error = "请输入内容"
            return
        }
        when (status) {
            0 -> {
                phone = "+86-" + input
                // 发送验证码
                Dua.getInstance().getVfCode(phone, object : DuaCallback {
                    override fun onSuccess(str: Any) {
                        if (status < 2) {
                            // 操作完成状态+1
                            status++
                            changUI()
                        }
                    }

                    override fun onError(status: Int, str: String) {

                    }
                })
            }
            1 -> {
               // vfCode = input
                vfCode = "FFFFFF"
                // 输入验证码
                Dua.getInstance().checkVfCode(phone, vfCode, object : DuaCallback {
                    override fun onSuccess(result: Any) {
                        if (status < 2) {
                            // 操作完成状态+1
                            status++
                            changUI()
                        }
                    }

                    override fun onError(status: Int, s: String) {
                        var reason: String? = DuaConfig.errCode[status]
                        if (reason == null) {
                            reason = s
                        }
                    }
                })
            }
            2 -> {
                val reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$"
                // 输入密码
               // if (input.length < 6) {
                if (!Pattern.matches(reg, input)) {
                    //et_input.error = "密码长度小于6或错误"
                    et_input.error = "6-21字母和数字"
                    return
                } else {
                    password = input
                    val role = "member"
                    val in_vode = ""
                    val type = "T"
                    var name = "未填写"
                    var sex = "M"
                    var birthday = "19911001"
                    var avatar_url = ""
                    val duaCallback = object : DuaCallback {
                        override fun onSuccess(str: Any) {
                            Log.i(TAG, "注册成功！")
                            Dua.getInstance().login("+86-" + phone, password, " Dua . Default ", object : DuaCallback {
                                override fun onSuccess(s: Any) {
                                    // 跳转到结果
                                    startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
                                    finish()
                                }

                                override fun onError(status: Int, s: String) {
                                    // handler.sendEmptyMessage(STOP_ANIM);
                                    var reason: String? = DuaConfig.errCode[status]
                                    if (reason == null) {
                                        reason = s
                                    }
                                    // 跳转到结果
                                    startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
                                    finish()
                                }
                            })
                        }

                        override fun onError(status: Int, str: String) {
                            Log.i(TAG, "注册失败！")
                            var reason: String? = DuaConfig.errCode[status]
                            if (reason == null) {
                                reason = str
                            }
                        }
                    }
                    if (isForgetPassword) {
                        Dua.getInstance().register(phone, password, role, vfCode, in_vode,
                                type, name, sex, birthday, avatar_url, duaCallback)
                    } else {
                        val resetPasswordCallBack = object : DuaCallback {
                            override fun onSuccess(result: Any) {

                            }

                            override fun onError(status: Int, s: String) {
                                var reason: String? = DuaConfig.errCode[status]
                                if (reason == null) {
                                    reason = s
                                }
                            }
                        }
                        Dua.getInstance().resetPwd(phone, vfCode, password, resetPasswordCallBack)
                    }
                   /* startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
                    finish()*/
                }
            }
        }
    }

    fun changUI() {
        tv_input_hint.text = textHint[status]
        et_input.hint = inputHint[status]
        et_input.text.clear()
        btn_operate.text = btnText[status]
        when (status) {
            0 -> {
                // 输入手机号码状态
                et_input.inputType = InputType.TYPE_CLASS_PHONE
            }
            1 -> {
                // 输入验证码状态
                et_input.inputType = InputType.TYPE_CLASS_NUMBER
            }
            2 -> {
                // 输入密码状态
                et_input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}
