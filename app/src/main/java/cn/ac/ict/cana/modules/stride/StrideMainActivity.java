package cn.ac.ict.cana.modules.stride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.activities.MainActivity_;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class StrideMainActivity extends Activity {
    Button bt_begin;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_main);
        initWidget();

    }

    private void initWidget() {
        SensorManager sm = (SensorManager) StrideMainActivity.this.getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (acc == null || gyro == null) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(StrideMainActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.attention))
                    .setContentText(getString(R.string.not_support))
                    .setConfirmText(getString(R.string.btn_confirm))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            startActivity(new Intent(StrideMainActivity.this, MainActivity_.class));
                        }
                    });
            sweetAlertDialog.show();
        } else {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.stride_guide);
            mp.start();
        }
        bt_begin = (Button) findViewById(R.id.bt_begin);
        bt_begin.setOnClickListener(new onBeginListener());

    }

    class onBeginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }

            Intent intent = new Intent(getApplicationContext(), GoActivity.class);
            startActivity(intent);
            finish();

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

}
