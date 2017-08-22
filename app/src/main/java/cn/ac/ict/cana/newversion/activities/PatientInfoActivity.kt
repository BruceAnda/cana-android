package cn.ac.ict.cana.newversion.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView

import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity
import cn.ac.ict.cana.newversion.utils.FileUtils
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_patient_info.*

/**
 * 病人信息页面
 */
class PatientInfoActivity : Activity() {

    private val sex = arrayOf("男", "女")

    private val mONItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            FileUtils.PATIENT_SEX = sex[p2]
            println(sex[p2])
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

        spinner_patient_sex.onItemSelectedListener = mONItemSelectListener

        btn_save.setOnClickListener {
            val patient_name = edittext_patient_name.text?.toString()
            if (TextUtils.isEmpty(patient_name)) {
                edittext_patient_name.setError("请输入病人名称！")
                return@setOnClickListener
            }
            FileUtils.PATIENT_NAME = patient_name


            val patient_age = edittext_patient_age.text?.toString()
            if (TextUtils.isEmpty(patient_age)) {
                edittext_patient_age.setError("请输入病人年龄")
                return@setOnClickListener
            }
            FileUtils.PATIENT_AGE = patient_age

            val patient_medicine = edittext_patient_medicine.text?.toString()
            if (TextUtils.isEmpty(patient_medicine)) {
                FileUtils.PATIENT_MEDICINE = ""
            } else {
                FileUtils.PATIENT_MEDICINE = patient_medicine
            }

            startActivity(Intent(this@PatientInfoActivity, ModelGuideActivity::class.java))
            FileUtils.batch = MD5.md5(System.currentTimeMillis().toString());
            finish()
        }
    }
}
