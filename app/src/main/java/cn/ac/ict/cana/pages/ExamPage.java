package cn.ac.ict.cana.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.adapters.ExamAdapter;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.Exam;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class ExamPage {

    static public View InitialExamPageView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.pageview_exam, null, false);

        final ListView lvExam = (ListView) view.findViewById(R.id.lv_exam);
        ArrayList<Exam> examList = new ArrayList<>();
        final ExamAdapter examAdapter = new ExamAdapter(context);


        for (String examName: ModuleHelper.ModuleList) {
            examList.add(new Exam.Builder().setName(examName).build());
        }

        examAdapter.setList(examList);
        lvExam.setAdapter(examAdapter);

        return view;
    }
}
