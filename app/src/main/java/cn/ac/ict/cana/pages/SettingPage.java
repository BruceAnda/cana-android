package cn.ac.ict.cana.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.ac.ict.cana.R;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class SettingPage {

    static public View InitialSettingPageView(final Context context){
        final Context myContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.pageview_setting, null, false);

        final Button tv_device = (Button) view.findViewById(R.id.btn_setting_device);
        final Button tv_logs = (Button) view.findViewById(R.id.btn_setting_logs);
        final Button tv_support = (Button) view.findViewById(R.id.btn_setting_support);
        final Button tv_about = (Button) view.findViewById(R.id.btn_setting_about);
        final TextView tv_content = (TextView) view.findViewById(R.id.tv_setting);
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_content.setText(myContext.getString(R.string.about));
            }
        });
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_content.setText(myContext.getString(R.string.support));
            }
        });
        tv_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, context.getString(R.string.logs), Toast.LENGTH_SHORT).show();
            }
        });
        tv_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, context.getString(R.string.device), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
