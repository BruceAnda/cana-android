package cn.ac.ict.cana.adapters;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import cn.ac.ict.cana.duaui.DuaActivityProfile;
import cn.ac.ict.cana.pages.ExamPage;
import cn.ac.ict.cana.pages.HistoryPage;
import cn.ac.ict.cana.pages.SettingPage;

/**
 * Created by zhaoliang on 2017/4/12.
 */

public class MainAdapterNew extends PagerAdapter {

    Context mContext;
    public View view;
    HashMap<Integer, View> ViewMap = new HashMap<>();

    private LocalActivityManager mactivityManager = null;

    public MainAdapterNew(Context mContext, LocalActivityManager mactivityManager) {
        this.mContext = mContext;
        this.mactivityManager = mactivityManager;
    }

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
                    //view = getView("one", new Intent(mContext, ExamPageActivity.class));
                    Log.d("MainAdapter exam", String.valueOf(mContext));
                    break;
                case 1:
                    //view = UserPage.InitialUserPageView(mContext);
                    view = getView("one", new Intent(mContext, DuaActivityProfile.class));
                    Log.d("MainAdapter user", String.valueOf(mContext));
                    break;
                case 2:
                    view = HistoryPage.InitialHistoryPageView(mContext);
                    //view = getView("one", new Intent(mContext, HistoryActivity.class));
                    Log.d("MainAdapter history", String.valueOf(mContext));
                    break;
                case 3:
                    view = SettingPage.InitialSettingPageView(mContext);
                    //view = getView("one", new Intent(mContext, SettingActivity.class));
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

    /**
     * 通过activity获取视图
     *
     * @param id
     * @param intent
     * @return
     */
    private View getView(String id, Intent intent) {
        return mactivityManager.startActivity(id, intent).getDecorView();
    }
}
