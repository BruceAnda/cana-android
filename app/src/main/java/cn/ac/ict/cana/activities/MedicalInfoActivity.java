package cn.ac.ict.cana.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.newversion.base.YouMengBaseActivity;

/**
 * Author: saukymo
 * Date: 9/19/16
 */
public class MedicalInfoActivity extends YouMengBaseActivity {
    Button btContinue;
    ToggleButton tgPDMedicine, tgLeftMedicine, tgCurrentStatus;
    TextView etTimeSinceLast;
    LinearLayout linearLayout;
    int time;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_info);
        init();
    }
    boolean currentStatus, takingPDMed, takingLeftMed;
    protected void init() {
      //  settings = getSharedPreferences("Cana", 0);

        tgCurrentStatus = (ToggleButton) findViewById(R.id.toggle_current_status);
        tgPDMedicine = (ToggleButton) findViewById(R.id.toggle_pd_medicine);
        tgLeftMedicine = (ToggleButton) findViewById(R.id.toggle_left_medicine);
        etTimeSinceLast = (EditText) findViewById(R.id.edittext_time_since_last);
        btContinue = (Button) findViewById(R.id.bt_continue);
        linearLayout = (LinearLayout)findViewById(R.id.ll_time);
        linearLayout.setVisibility(View.GONE);
        tgLeftMedicine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int visible = !b?View.VISIBLE:View.GONE;
                linearLayout.setVisibility(visible);
            }
        });
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add an action
                currentStatus = tgCurrentStatus.isChecked();
                takingLeftMed = tgLeftMedicine.isChecked();
                takingPDMed = tgPDMedicine.isChecked();
                try {
                    time = Integer.parseInt(etTimeSinceLast.getText().toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

    }
}
