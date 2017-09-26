package cn.ac.ict.cana.modules.stride;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.activities.FeedBackActivity;
import cn.ac.ict.cana.mode.AccData;
import cn.ac.ict.cana.mode.GyroData;
import cn.ac.ict.cana.utils.FileUtils;

public class GoActivity extends Activity {
    private static final String TAG = GoActivity.class.getSimpleName();
    TextView goContent;
    Button btnGo;
    Vibrator vibrator;
    long[] pattern = {100, 400};
    boolean flag = false;
    boolean start = false;
    MediaPlayer mp;
    SensorManager sm;
    AccEventListener accEventListener;
    GyroEventListener gyroEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_go);
        goContent = (TextView) findViewById(R.id.tv_go);
        btnGo = (Button) findViewById(R.id.btn_go);
        btnGo.setOnClickListener(new onBtnClickListener());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.countdown);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                flag = true;
                btnGo.setText(getString(R.string.stride_btn_text));
                btnGo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.freebie_2));
                btnGo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.freebie_1));
                btnGo.setVisibility(View.VISIBLE);
                vibrator.vibrate(pattern, -1);
                goContent.setText(getString(R.string.stride_testing));
                start = true;

            }
        });
        sm = (SensorManager) GoActivity.this.getSystemService(Context.SENSOR_SERVICE);

        accEventListener = new AccEventListener();
        gyroEventListener = new GyroEventListener();
        flag = false;
        prepare();
    }

    private void register() {
        sm.registerListener(accEventListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(gyroEventListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void stop() {
        start = false;
        sm.unregisterListener(accEventListener);
        sm.unregisterListener(gyroEventListener);
    }

    class AccEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (start) {
               /* FloatVector vector = new FloatVector(event.values[0], event.values[1], event.values[2]);
                accFloatVectors.add(vector);*/
                FileUtils.accSDatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    class GyroEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (start) {
                /*FloatVector gyroFloatVector = new FloatVector(event.values[0], event.values[1], event.values[2]);
                gyroFloatVectors.add(gyroFloatVector);*/
                FileUtils.gyroSDataList.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    class onBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            stop();
            // new SaveTask().execute();
            next();
        }
    }

    private void prepare() {

        mp.start();
        goContent.setText(getString(R.string.stride_next_turn));

        btnGo.setVisibility(View.GONE);


        register();
    }

    @Override
    protected void onPause() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;

        }
        stop();
        finish();
        super.onPause();
    }



    private void next() {
        // int test = getStrideNumbers(genKalman(genOneDimension(accFloatVectors)));
        Intent intent = new Intent(GoActivity.this, FeedBackActivity.class);
        intent.putExtra("modelName", ModuleHelper.MODULE_STRIDE);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StrideMainActivity.class));
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        new AlertDialog.Builder(this).setTitle("提示").setMessage("测试失败,重新开始").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(GoActivity.this, StrideMainActivity.class));
                finish();
            }
        }).setCancelable(false).show();
    }
}
