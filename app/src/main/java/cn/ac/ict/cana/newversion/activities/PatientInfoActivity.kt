package cn.ac.ict.cana.newversion.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity
import cn.ac.ict.cana.newversion.utils.FileUtils
import com.lovearthstudio.duasdk.util.encryption.MD5
import kotlinx.android.synthetic.main.activity_patient_info.*

/**
 * 病人信息页面
 */
class PatientInfoActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_info)
        FileUtils.PATIENT_NAME = ""
        FileUtils.PATIENT_AGE = ""
        FileUtils.PATIENT_SEX = ""
        FileUtils.PATIENT_MEDICINE = ""
        btn_save.setOnClickListener {
            val patient_name = edittext_patient_name.text?.toString()
            if (TextUtils.isEmpty(patient_name)) {
                edittext_patient_name.setError("请输入病人名称！")
                return@setOnClickListener
            }
            FileUtils.PATIENT_NAME = patient_name

            val patient_sex = edittext_patient_sex.text?.toString()
            if (TextUtils.isEmpty(patient_sex)) {
                edittext_patient_sex.setError("请输入病人性别")
                return@setOnClickListener
            }
            FileUtils.PATIENT_SEX = patient_sex

            val patient_age = edittext_patient_age.text?.toString()
            if (TextUtils.isEmpty(patient_age)) {
                edittext_patient_age.setError("请输入病人年龄")
                return@setOnClickListener
            }
            FileUtils.PATIENT_AGE = patient_age

            val patient_medicine = edittext_patient_medicine.text?.toString()
            if (TextUtils.isEmpty(patient_medicine)) {
                edittext_patient_medicine.setError("请输入病人使用药物")
                return@setOnClickListener
            }
            FileUtils.PATIENT_MEDICINE = patient_medicine

            startActivity(Intent(this@PatientInfoActivity, ModelGuideActivity::class.java))
            FileUtils.batch = MD5.md5(System.currentTimeMillis().toString());
            finish()
        }
    }
}
