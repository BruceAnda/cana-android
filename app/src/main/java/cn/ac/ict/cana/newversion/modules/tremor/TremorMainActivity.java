package cn.ac.ict.cana.newversion.modules.tremor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.activities.MainActivity_;
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity2;
import cn.ac.ict.cana.newversion.utils.FileUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class TremorMainActivity extends Activity {

    private Button bt_begin;
    private boolean isRight = true;
    private boolean isStatic = true;
    private TextView tvTitle;
    private TextView modelMainPage;
    private String[] titiles;
    private String[] tips;

    MediaPlayer mp;
    private int grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tremor_main);
        titiles = getResources().getStringArray(R.array.tremor_title);
        tips = getResources().getStringArray(R.array.tremor_tips);
        initWidget();
    }

    private void initWidget() {
        tvTitle = (TextView) findViewById(R.id.tv_termor_title);
        modelMainPage = (TextView) findViewById(R.id.tv_tremor_tips);
        grade = getIntent().getIntExtra("grade", 0);
        modelMainPage.setText(tips[grade]);
        tvTitle.setText(titiles[grade]);

        bt_begin = (Button) findViewById(R.id.bt_begin);
        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;

                }
                FileUtils.accDatalist = new ArrayList<>();
                FileUtils.gyroDataList = new ArrayList<>();
                Intent intent = new Intent(TremorMainActivity.this, TremorTestingActivity.class);
                intent.putExtra("isRight", isRight);
                intent.putExtra("isStatic", isStatic);
                intent.putExtra("grade", grade);
                startActivity(intent);
                finish();
            }
        });
        SensorManager sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (acc == null || gyro == null) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(TremorMainActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.attention))
                    .setContentText(getString(R.string.not_support))
                    .setConfirmText(getString(R.string.btn_confirm))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            startActivity(new Intent(TremorMainActivity.this, MainActivity_.class));
                        }
                    });
            sweetAlertDialog.show();
        } else {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.stand_guide);
           // mp.start();
        }
    }

    @Override
    protected void onPause() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;

        }
        finish();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ModelGuideActivity2.class));
        finish();
    }
}
