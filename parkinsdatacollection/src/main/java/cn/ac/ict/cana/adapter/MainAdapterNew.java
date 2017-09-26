package cn.ac.ict.cana.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.cana.pagers.ExamPageFragment;
import cn.ac.ict.cana.pagers.HistoryPageFragment;
import cn.ac.ict.cana.pagers.SettingPageFragment;
import cn.ac.ict.cana.pagers.UserPageFragmentNew;

/**
 * Created by zhaoliang on 2017/6/6.
 */

public class MainAdapterNew extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainAdapterNew(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new ExamPageFragment());
        fragments.add(new UserPageFragmentNew());
        fragments.add(new HistoryPageFragment());
        fragments.add(new SettingPageFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
