package cn.ac.ict.cana.newversion.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity
import cn.ac.ict.cana.newversion.modules.guide.*
import cn.ac.ict.cana.newversion.pagers.ExamPageFragment
import cn.ac.ict.cana.newversion.utils.FileUtils
import kotlinx.android.synthetic.main.activity_patient_info.*
import java.util.*

/**
 * 病人信息页面
 */
class PatientInfoActivity : YouMengBaseActivity() {

    private val sex = arrayOf("男", "女")
    private val open = arrayOf("关", "开")

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
                edittext_patient_name.error = "请输入病人名称！"
                return@setOnClickListener
            }
            FileUtils.PATIENT_NAME = patient_name


            val patient_age = edittext_patient_age.text?.toString()
            if (TextUtils.isEmpty(patient_age)) {
                edittext_patient_age.error = "请输入病人年龄"
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
                    }
                }
            }
            target?.putExtra(ExamPageFragment.MENT_TYPE, menu_type)
            target?.putExtra(ExamPageFragment.MENU, menu)
            startActivity(target)
            //FileUtils.batch = MD5.md5(System.currentTimeMillis().toString())
            // 产生一个batch
            FileUtils.batch = UUID.randomUUID().toString()
            finish()
        }
    }
}
