package cn.ac.ict.cana.newversion.pagers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ac.ict.cana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageChartFragment extends Fragment {


    public PageChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_chart, container, false);
    }

}
