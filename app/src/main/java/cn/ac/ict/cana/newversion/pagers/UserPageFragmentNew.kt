package cn.ac.ict.cana.newversion.pagers


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

import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.widget.InputDialog
import kotlinx.android.synthetic.main.fragment_user_page_fragment_new.*

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
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_APPEND)
        val userName = sharedPreferences.getString("user_name", "安惠美")
        val userSex = sharedPreferences.getString("user_sex", "女")
        val userAge = sharedPreferences.getString("user_age", "30")
        val userTitle = sharedPreferences.getString("user_title", "技师")
        val userWorkUnit = sharedPreferences.getString("user_work_unit", "首都医科大学宣武医院")

        tv_user_name.text = userName
        tv_user_sex.text = userSex
        tv_user_age.text = userAge
        tv_user_title.text = userTitle
        tv_user_work_unit.text = userWorkUnit

        et_edit_user_info.setOnClickListener {
            changeState()
        }

        tv_user_name.setOnClickListener {
            val inputDialog = InputDialog(context, "姓名", InputType.TYPE_CLASS_TEXT)
            inputDialog.onInputContentChangeListener = onUserNameChangeListener
            inputDialog.show()
        }
        spinner_user_sex.onItemSelectedListener = mSexItemSelectListener
        tv_user_age.setOnClickListener {
            val inputDialog = InputDialog(context, "年龄", InputType.TYPE_CLASS_TEXT)
            inputDialog.onInputContentChangeListener = onUserAgeChangeListener
            inputDialog.show()
        }
        tv_user_title.setOnClickListener {
            val inputDialog = InputDialog(context, "职称", InputType.TYPE_CLASS_TEXT)
            inputDialog.onInputContentChangeListener = onUserTitleChangeListener
            inputDialog.show()
        }
        tv_user_work_unit.setOnClickListener {
            val inputDialog = InputDialog(context, "工作单位", InputType.TYPE_CLASS_TEXT)
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
        sharedPreferences.edit()
                .putString("user_name", tv_user_name.text.toString())
                .putString("user_sex", tv_user_sex.text.toString())
                .putString("user_age", tv_user_age.text.toString())
                .putString("user_title", tv_user_title.text.toString())
                .putString("user_work_unit", tv_user_work_unit.text.toString())
                .commit()
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

}// Required empty public constructor

