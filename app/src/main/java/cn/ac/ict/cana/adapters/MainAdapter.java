package cn.ac.ict.cana.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.HashMap;

import cn.ac.ict.cana.pages.ExamPage;
import cn.ac.ict.cana.pages.HistoryPage;
import cn.ac.ict.cana.pages.SettingPage;
import cn.ac.ict.cana.pages.UserPage;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
@EBean
public class MainAdapter extends PagerAdapter {

    @RootContext Context mContext;
    public View view;
    HashMap<Integer, View> ViewMap = new HashMap<>();

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        //TODO: I'm sure that there must be a better solution.
        if (!ViewMap.containsKey(position)) {
            switch (position) {
                case 0:
                    view = ExamPage.InitialExamPageView(mContext);
                    Log.d("MainAdapter exam", String.valueOf(mContext));
                    break;
                case 1:
                    view = UserPage.InitialUserPageView(mContext);
                    Log.d("MainAdapter user", String.valueOf(mContext));
                    break;
                case 2:
                    view = HistoryPage.InitialHistoryPageView(mContext);
                    Log.d("MainAdapter history", String.valueOf(mContext));
                    break;
                case 3:
                    view = SettingPage.InitialSettingPageView(mContext);
                    Log.d("MainAdapter setting", String.valueOf(mContext));
                    break;
            }

            container.addView(view);
            ViewMap.put(position, view);
        } else {
            view = ViewMap.get(position);
        }
        return view;
    }
}
