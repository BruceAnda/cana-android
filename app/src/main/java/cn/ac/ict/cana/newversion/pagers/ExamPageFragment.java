package cn.ac.ict.cana.newversion.pagers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.newversion.activities.PatientInfoActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamPageFragment extends Fragment implements View.OnClickListener {

    private Button btn_start_test;

    public ExamPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exam_page, container, false);
        btn_start_test = (Button) view.findViewById(R.id.btn_start_test);
        btn_start_test.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_test:
                startActivity(new Intent(getActivity(), PatientInfoActivity.class));
                break;
        }
    }
}
