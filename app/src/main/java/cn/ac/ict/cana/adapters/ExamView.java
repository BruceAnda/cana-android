package cn.ac.ict.cana.adapters;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.Exam;

/**
 * Author: saukymo
 * Date: 9/13/16
 */


@EViewGroup(R.layout.adapter_exam)
public class ExamView extends LinearLayout {

    @ViewById(R.id.bt_add_history)
    Button btAddHistory;

    public Context mContext;

    public ExamView(Context context) {
        super(context);
        mContext = context;
    }

    public void bind(Exam exam) {
        btAddHistory.setText(ModuleHelper.getName(mContext, exam.name));
    }
}