package cn.ac.ict.cana.newversion.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import cn.ac.ict.cana.R
import cn.ac.ict.cana.parkionsdatacollection.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.db.bean.Batch
import cn.ac.ict.cana.newversion.db.database
import cn.ac.ict.cana.newversion.modules.guide.*
import cn.ac.ict.cana.newversion.pagers.ExamPageFragment
import cn.ac.ict.cana.newversion.utils.FileUtils
import cn.ac.ict.cana.newversion.widget.InputDialog
import kotlinx.android.synthetic.main.activity_patient_info.*
import java.util.*
import java.util.regex.Pattern

/**
 * 病人信息页面
 * 思路：
 *      1. 一开始先创建一个UUID
 */
class PatientInfoActivity : YouMengBaseActivity() {

    private val sex = arrayOf("M", "F")
    private val open = arrayOf("0", "1")

    private val mOnSexItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            FileUtils.PATIENT_SEX = sex[p2]
        }

    }

    private val mOnOpenSexItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            FileUtils.SWITCHING_PERIOD = open[p2]
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_info)

        // 数据批次的UUID
        val randomUUID = UUID.randomUUID().toString()


        FileUtils.PATIENT_NAME = ""
        FileUtils.PATIENT_AGE = ""
        FileUtils.PATIENT_SEX = ""
        FileUtils.PATIENT_MEDICINE = ""

        // 性别默认为男
        FileUtils.PATIENT_SEX = sex[0]
        // 开关期
        FileUtils.SWITCHING_PERIOD = open[0]

        spinner_patient_sex.onItemSelectedListener = mOnSexItemSelectListener
        spinner_patient_open.onItemSelectedListener = mOnOpenSexItemSelectListener

        btn_save.setOnClickListener {
            val patient_name = edittext_patient_name.text?.toString()
            if (TextUtils.isEmpty(patient_name)) {
                //edittext_patient_name.error = "请输入病人名称！"
                tv_tip.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    tv_tip.visibility = View.GONE
                }, 4000)
                return@setOnClickListener
            }
            FileUtils.PATIENT_NAME = patient_name


            val patient_age = edittext_patient_age.text?.toString()
            if (TextUtils.isEmpty(patient_age)) {
                //edittext_patient_age.error = "请输入病人年龄"
                tv_tip.visibility = View.VISIBLE
                Handler().postDelayed(Runnable {
                    tv_tip.visibility = View.GONE
                }, 4000)
                return@setOnClickListener
            }
            FileUtils.PATIENT_AGE = patient_age

            val patient_medicine = edittext_patient_medicine.text?.toString()
            if (TextUtils.isEmpty(patient_medicine)) {
                FileUtils.PATIENT_MEDICINE = ""
            } else {
                FileUtils.PATIENT_MEDICINE = patient_medicine
            }

            val menu_type = intent.extras.getInt(ExamPageFragment.MENT_TYPE)
            val menu = intent.extras.getInt(ExamPageFragment.MENU)
            var target: Intent? = null
            when (menu_type) {
                ExamPageFragment.MENU_TYPE_ALL -> {
                    target = Intent(this@PatientInfoActivity, ModelGuideActivity::class.java)
                }
                ExamPageFragment.MENU_TYPE_SINGLE -> {
                    when (menu) {
                        ExamPageFragment.MENU_COUNT -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity::class.java)
                        }
                        ExamPageFragment.MENU_TREMOR -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity2::class.java)
                        }
                        ExamPageFragment.MENU_SOUND -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity3::class.java)
                        }
                        ExamPageFragment.MENU_STAND -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity4::class.java)
                        }
                        ExamPageFragment.MENU_STRIDE -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity5::class.java)
                        }
                        ExamPageFragment.MENU_TAPPER -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity6::class.java)
                        }
                        ExamPageFragment.MENU_FACE -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity7::class.java)
                        }
                        ExamPageFragment.MENU_ARM_DROOP -> {
                            target = Intent(this@PatientInfoActivity, ModelGuideActivity8::class.java)
                        }
                    }
                }
            }
            target?.putExtra(ExamPageFragment.MENT_TYPE, menu_type)
            target?.putExtra(ExamPageFragment.MENU, menu)
            startActivity(target)

            // 界面跳转的时候，保存数据
            database.use {
                // UUID
                FileUtils.batch = randomUUID
                // 保存Batch信息
                val values = ContentValues()
                values.put(Batch.TIME, System.currentTimeMillis())
                values.put(Batch.BATCH, randomUUID)
                // 病人信息
                values.put(Batch.PATIENT_NAME, FileUtils.PATIENT_NAME)
                values.put(Batch.PATIENT_AGE, FileUtils.PATIENT_AGE)
                values.put(Batch.PATIENT_SEX, FileUtils.PATIENT_SEX)
                values.put(Batch.PATIENT_MEDICINE, FileUtils.PATIENT_MEDICINE)
                values.put(Batch.PATIENT_OPEN, FileUtils.SWITCHING_PERIOD)
                // 插入数据库
                insert(Batch.TABLE_NAME, null, values)
            }
            finish()
        }
    }

    private val onPationNameChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            var reg = "^(([\u4e00-\u9fa5]{2,8})|([a-zA-Z]{2,16}))$"
            if (!TextUtils.isEmpty(text) && Pattern.matches(reg, text)) {
                edittext_patient_name.text = text
            } else {
                Toast.makeText(this@PatientInfoActivity, "请输入合法的姓名", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isAge(age: String): Boolean {
        var isTrue = false
        if (age != null && age != "") {
            val pattern = Pattern.compile("[0-9]*")
            if (pattern.matcher(age).matches()) {
                val ageInt = age.toInt()
                if (ageInt in 1..119) {
                    isTrue = true
                }
            }
        }
        return isTrue
    }

    private val onPationtAgeChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            if (!TextUtils.isEmpty(text) && isAge(text)) {
                edittext_patient_age.text = text
            } else {
                Toast.makeText(this@PatientInfoActivity, "请输入合法的年龄", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val onPationMedicineChangeListener = object : InputDialog.OnInputContentChangeListener {
        override fun onContentChange(text: String) {
            if (!TextUtils.isEmpty(text) && (!text.contains(" ") && (!text.contains(";")))) {
                edittext_patient_medicine.text = text
            }
        }
    }

    fun showPatientNameInput(view: View) {
        var conotentHint = edittext_patient_name.text.toString()
        if (conotentHint.isEmpty()) {
            conotentHint = "输入病人姓名"
        }
        val inputDialog = InputDialog(this@PatientInfoActivity, "病人姓名", InputType.TYPE_CLASS_TEXT, conotentHint)
        inputDialog.onInputContentChangeListener = onPationNameChangeListener
        inputDialog.show()
    }

    fun showPatientAgeInput(view: View) {
        var conotentHint = edittext_patient_age.text.toString()
        if (conotentHint.isEmpty()) {
            conotentHint = "输入病人年龄"
        }
        val inputDialog = InputDialog(this@PatientInfoActivity, "病人年龄", InputType.TYPE_CLASS_NUMBER, conotentHint)
        inputDialog.onInputContentChangeListener = onPationtAgeChangeListener
        inputDialog.show()
    }

    fun showPatientMedicineInput(view: View) {
        var conotentHint = edittext_patient_medicine.text.toString()
        if (conotentHint.isEmpty()) {
            conotentHint = "输入病人使用的药物，多个药物用逗号分隔"
        }
        val inputDialog = InputDialog(this@PatientInfoActivity, "使用药物", InputType.TYPE_CLASS_TEXT, conotentHint)
        inputDialog.onInputContentChangeListener = onPationMedicineChangeListener
        inputDialog.show()
    }
}
