package cn.ac.ict.cana.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.models.Exam;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
@EBean
public class ExamAdapter extends BaseAdapter {

    private Context mContext;
    public ExamAdapter(Context context){
        mContext = context;
    }
    private List<Exam> exams;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ExamView examView;
        if (view == null) {

            examView = ExamView_.build(mContext);
        } else {
            examView = (ExamView) view;
        }

        final Exam exam = getItem(position);
        examView.bind(exam);

        Button btAddHistory = (Button) examView.findViewById(R.id.bt_add_history);
        btAddHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    exam.go(mContext);
            }
        });
        return examView;
    }

    public void setList(ArrayList<Exam> examList) {
        exams = examList;
    }

    @Override
    public int getCount() {
        return exams.size();
    }

    @Override
    public Exam getItem(int position) {
        return exams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Exam exam){
        exams.add(exam);
    }

}


