package cn.ac.ict.cana.pagers


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import cn.ac.ict.cana.parkionsdatacollection.R

import cn.ac.ict.cana.widget.InputDialog
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.DuaCallback
import kotlinx.android.synthetic.main.fragment_user_page_fragment_new.*
import org.json.JSONException
import org.json.JSONObject

/**
 * 用户信息界面
 */
class UserPageFragmentNew : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_user_page_fragment_new, container, false)
    }

    private val sex = arrayOf("男", "女")
    private val mSexItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            tv_user_sex.text = sex[p2]
        }

    }

    var isEditState = false
    lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = context.getSharedPreferences("profile", Context.MODE_APPEND)
        val userName = sharedPreferences.getString("name", "未填写")
        val sex = sharedPreferences.getString("sex", "未填写")
        var userSex = ""
        if ("M".equals(sex)) {
            userSex = "男"
        } else {
            userSex = "女"
        }
        tv_user_name.text = userName
        tv_user_sex.text = userSex
        val saying = sharedPreferences.getString("saying", "")
        val list = saying.split(",*#,")
        try {
            val userAge = list[0]
            tv_user_age.text = userAge
            val userTitle = list[1]
            tv_user_title.text = userTitle
            val userWorkUnit = list[2]
            tv_user_work_unit.text = userWorkUnit
        } catch (e: Exception) {

        }

        et_edit_user_info.setOnClickListener {
            changeState()
        }

        tv_user_name.setOnClickListener {
            var conotentHint = tv_user_name.text.toString()
            if (conotentHint.isEmpty()) {
                conotentHint = "请输入您的姓名"
            }
            val inputDialog = InputDialog(context, "姓名", InputType.TYPE_CLASS_TEXT, conotentHint)
            inputDialog.onInputContentChangeListener = onUserNameChangeListener
            inputDialog.show()
        }
        spinner_user_sex.onItemSelectedListener = mSexItemSelectListener
        tv_user_age.setOnClickListener {
            var conotentHint = tv_user_age.text.toString()
            if (conotentHint.isEmpty()) {
                conotentHint = "输入您的年龄"
            }
            val inputDialog = InputDialog(context, "年龄", InputType.TYPE_CLASS_NUMBER, conotentHint)
            inputDialog.onInputContentChangeListener = onUserAgeChangeListener
            inputDialog.show()
        }
        tv_user_title.setOnClickListener {
            var conotentHint = tv_user_title.text.toString()
            if (conotentHint.isEmpty()) {
                conotentHint = "输入您的职称"
            }
            val inputDialog = InputDialog(context, "职称", InputType.TYPE_CLASS_TEXT, conotentHint)
            inputDialog.onInputContentChangeListener = onUserTitleChangeListener
            inputDialog.show()
        }
        tv_user_work_unit.setOnClickListener {
            var conotentHint = tv_user_work_unit.text.toString()
            if (conotentHint.isEmpty()) {
                conotentHint = "输入您的工作单位"
            }
            val inputDialog = InputDialog(context, "工作单位", InputType.TYPE_CLASS_TEXT, conotentHint)
            inputDialog.onInputContentChangeListener = onUserWorkUnitChangeListener
            inputDialog.show()
        }
    }

    /**
     * 改变编辑状态
     */
    private fun changeState() {
        if (isEditState) { // 编辑状态

            et_edit_user_info.text = "编辑"
            tv_user_name.setTextColor(resources.getColor(R.color.mainColor))
            tv_user_sex.visibility = View.VISIBLE
            spinner_user_sex.visibility = View.GONE
            tv_user_age.setTextColor(resources.getColor(R.color.mainColor))
            tv_user_title.setTextColor(resources.getColor(R.color.mainColor))
            tv_user_work_unit.setTextColor(resources.getColor(R.color.mainColor))

            tv_user_name.isClickable = false
            tv_user_age.isClickable = false
            tv_user_title.isClickable = false
            tv_user_work_unit.isClickable = false
            saveUserInfo()
        } else {

            et_edit_user_info.text = "保存"
            tv_user_name.setTextColor(Color.GRAY)
            tv_user_sex.visibility = View.GONE
            spinner_user_sex.visibility = View.VISIBLE
            tv_user_age.setTextColor(Color.GRAY)
            tv_user_title.setTextColor(Color.GRAY)
            tv_user_work_unit.setTextColor(Color.GRAY)

            tv_user_name.isClickable = true
            tv_user_age.isClickable = true
            tv_user_title.isClickable = true
            tv_user_work_unit.isClickable = true

        }
        isEditState = !isEditState
    }

    /**
     * 保存用户信息
     */
    private fun saveUserInfo() {
        updateProfile("name", tv_user_name.text.toString())
        val sex = tv_user_sex.text.toString()
        if ("男".equals(sex)) {
            updateProfile("sex", "M")
        } else {
            updateProfile("sex", "F")
        }
        updateProfile("saying", tv_user_age.text.toString() + ",*#," + tv_user_title.text.toString() + ",*#," + tv_user_work_unit.text.toString())
    }

    private val onUserNameChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            if (!TextUtils.isEmpty(text)) {
                tv_user_name.text = text
            }
        }
    }

    private val onUserAgeChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            if (!TextUtils.isEmpty(text)) {
                tv_user_age.text = text
            }
        }
    }
    private val onUserTitleChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            if (!TextUtils.isEmpty(text)) {
                tv_user_title.text = text
            }
        }
    }
    private val onUserWorkUnitChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            if (!TextUtils.isEmpty(text)) {
                tv_user_work_unit.text = text
            }
        }
    }

    /**
     * 更新用户数据
     */
    fun updateProfile(key: String, value: Any) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(key, value)
            Dua.getInstance().setUserProfile(jsonObject, object : DuaCallback {
                override fun onSuccess(result: Any) {
                    //SharedPreferenceUtil.prefSetKey(activity, PREF_PROFILE, key, value.toString())
                    sharedPreferences.edit().putString(key, value.toString()).commit()
                }

                override fun onError(status: Int, reason: String) {
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

}// Required empty public constructor

