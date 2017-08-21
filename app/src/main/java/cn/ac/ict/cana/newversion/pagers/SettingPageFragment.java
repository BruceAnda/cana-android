package cn.ac.ict.cana.newversion.pagers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.ac.ict.cana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingPageFragment extends Fragment {


    public SettingPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_page, container, false);
        final Button tv_device = (Button) view.findViewById(R.id.btn_setting_device);
        final Button tv_logs = (Button) view.findViewById(R.id.btn_setting_logs);
        final Button tv_support = (Button) view.findViewById(R.id.btn_setting_support);
        final Button tv_about = (Button) view.findViewById(R.id.btn_setting_about);
        final TextView tv_content = (TextView) view.findViewById(R.id.tv_setting);
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getString(R.string.about), Toast.LENGTH_SHORT).show();
                //tv_content.setText(getString(R.string.about));
            }
        });
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getString(R.string.support), Toast.LENGTH_SHORT).show();
                // tv_content.setText(getString(R.string.support));
            }
        });
        tv_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getString(R.string.logs), Toast.LENGTH_SHORT).show();
            }
        });
        tv_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getString(R.string.device), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}
