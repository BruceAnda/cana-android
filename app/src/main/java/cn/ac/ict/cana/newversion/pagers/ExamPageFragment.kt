package cn.ac.ict.cana.newversion.pagers

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.activities.PatientInfoActivity
import kotlinx.android.synthetic.main.fragment_exam_page.*

/**
 * 检测页面
 */
class ExamPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_exam_page, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 开始测试按钮点击事件
        start_test.setOnClickListener {
            startActivity(Intent(activity, PatientInfoActivity::class.java))
        }
        // 单项测试按钮点击事件
        tvSingleTest.setOnClickListener {

        }
    }


}// Required empty public constructor
