package cn.ac.ict.cana.newversion.pagers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.ac.ict.cana.R
import kotlinx.android.synthetic.main.fragment_setting_page.*

/**
 * 设置界面
 */
class SettingPageFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_setting_page, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_setting_support.setOnClickListener {
            Toast.makeText(context, getString(R.string.about), Toast.LENGTH_SHORT).show()
        }
        btn_setting_about.setOnClickListener {
            Toast.makeText(context, getString(R.string.support), Toast.LENGTH_SHORT).show()
        }
    }
}// Required empty public constructor
