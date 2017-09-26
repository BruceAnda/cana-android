package cn.ac.ict.cana.pagers

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.ac.ict.cana.R
import cn.ac.ict.cana.activities.PatientInfoActivity
import cn.ac.ict.cana.contant.GlobleData
import kotlinx.android.synthetic.main.fragment_exam_page.*

/**
 * 检测页面
 */
class ExamPageFragment : Fragment() {

    companion object {
        val MENT_TYPE = "menu_type"
        val MENU = "menu"
        // 菜单类型
        val MENU_TYPE_ALL = 1
        val MENU_TYPE_SINGLE = 2

        // 详细菜单
        val MENU_ALL = 0
        val MENU_COUNT = 1
        val MENU_TREMOR = 2
        val MENU_SOUND = 3
        val MENU_STAND = 4
        val MENU_STRIDE = 5
        val MENU_TAPPER = 6
        val MENU_FACE = 7
        val MENU_ARM_DROOP = 8
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_exam_page, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 开始测试按钮点击事件
        tv_exam_all_test.setOnClickListener {
            toPatientInfo(MENU_TYPE_ALL, MENU_ALL)
        }
        // 单项测试按钮点击事件
        tv_exam_single_test.setOnClickListener {
            exam_menu.visibility = View.GONE
            exam_menu2.visibility = View.VISIBLE
        }
        // 单选测试返回按钮
        iv_exam_back.setOnClickListener {
            exam_menu.visibility = View.VISIBLE
            exam_menu2.visibility = View.GONE
        }

        tv_exam_single_test_menu1.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_COUNT)
        }
        tv_exam_single_test_menu2.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_TREMOR)
        }
        tv_exam_single_test_menu3.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_SOUND)
        }
        tv_exam_single_test_menu4.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_STAND)
        }
        tv_exam_single_test_menu5.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_STRIDE)
        }
        tv_exam_single_test_menu6.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_TAPPER)
        }
        tv_exam_single_test_menu7.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_FACE)
        }
        tv_exam_single_test_menu8.setOnClickListener {
            toPatientInfo(MENU_TYPE_SINGLE, MENU_ARM_DROOP)
        }
    }

    /**
     * 跳转到病人信息页面
     */
    private fun toPatientInfo(menuType: Int, menu: Int) {
        val intent = Intent(context, PatientInfoActivity::class.java)
        intent.putExtra(MENT_TYPE, menuType)
        intent.putExtra(MENU, menu)
        GlobleData.menu_type = menuType
        GlobleData.menu = menu
        startActivity(intent)
    }


}// Required empty public constructor
