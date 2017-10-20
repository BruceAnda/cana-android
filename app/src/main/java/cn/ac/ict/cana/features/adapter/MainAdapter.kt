package cn.ac.ict.cana.features.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import java.util.ArrayList

import cn.ac.ict.cana.features.pagers.ExamPageFragment
import cn.ac.ict.cana.features.pagers.HistoryPageFragment
import cn.ac.ict.cana.features.pagers.SettingPageFragment
import cn.ac.ict.cana.features.pagers.UserPageFragmentNew

/**
 * 程序的主界面适配器
 * Created by zhaoliang on 2017/6/6.
 */

class MainAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // 所有的页面
    private val fragments: MutableList<Fragment>

    init {
        // 初始化页面
        fragments = ArrayList()
        fragments.add(ExamPageFragment())
        fragments.add(UserPageFragmentNew())
        fragments.add(HistoryPageFragment())
        fragments.add(SettingPageFragment())
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
