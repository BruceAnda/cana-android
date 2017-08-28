package cn.ac.ict.cana.newversion.modules.stand;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.bcgdv.asia.lib.ticktock.TickTockView;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.lovearthstudio.duasdk.Dua;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.activities.FeedBackActivity;
import cn.ac.ict.cana.newversion.mode.AccData;
import cn.ac.ict.cana.newversion.mode.GyroData;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.provider.HistoryProvider;
import cn.ac.ict.cana.newversion.utils.FileUtils;
import cn.ac.ict.cana.utils.FloatVector;


public class StandTestingActivity extends Activity {
    private static final String TAG = StandTestingActivity.class.getSimpleName();
    private NumberProgressBar pbx;
    private NumberProgressBar pby;
    private NumberProgressBar pbz;
    private TickTockView ttv;
    Vibrator vibrator;
    MediaPlayer mp;
    long[] pattern = {100, 400};
    SensorManager sm;
    AccEventListener accEventListener;
    GyroEventListener gyroEventListener;
    ArrayList<FloatVector> accVectors;
    ArrayList<FloatVector> gyroVectors;
    boolean start = true;
    boolean isRight = true;
    private int level;
    private int maxLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stand_testing);
        isRight = getIntent().getBooleanExtra("isRight", true);
        level = getIntent().getIntExtra("level", 0);
        init();
    }

    private void init() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        accVectors = new ArrayList<>();
        gyroVectors = new ArrayList<>();
        mp = MediaPlayer.create(getApplicationContext(), R.raw.countdown);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                vibrator.vibrate(pattern, -1);
                initSensors();
                initProgressBars();
                initTickTockView();
                start = true;
            }
        });
        mp.start();

    }

    private void initTickTockView() {
        ttv = (TickTockView) findViewById(R.id.ttv);
        Calendar start = Calendar.getInstance();
        start.add(Calendar.SECOND, -1);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.SECOND, 10);
        ttv.setOnTickListener(new TickTockView.OnTickListener() {

            @Override
            public String getText(long timeRemainingInMillis) {
                if (timeRemainingInMillis <= 0) {
                    vibrator.vibrate(pattern, -1);
                    stopSensors();
                    executeFinish();
                }
                return String.valueOf(timeRemainingInMillis / 1000 + 1);
            }
        });
        ttv.start(start, end);
    }

    private void initSensors() {
        accEventListener = new AccEventListener();
        gyroEventListener = new GyroEventListener();
        sm = (SensorManager) StandTestingActivity.this.getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(accEventListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(gyroEventListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);

    }

    private void initProgressBars() {
        pbx = (NumberProgressBar) findViewById(R.id.pb_x);
        pby = (NumberProgressBar) findViewById(R.id.pb_y);
        pbz = (NumberProgressBar) findViewById(R.id.pb_z);
        pbx.setMax(100);
        pby.setMax(100);
        pbz.setMax(100);

    }

    private void stopSensors() {
        if (sm != null) {
            sm.unregisterListener(accEventListener);
            sm.unregisterListener(gyroEventListener);
        }
        if (ttv != null) {
            ttv.stop();
        }
    }

    private void executeFinish() {
        next();
        //new SaveTask().execute();
    }

    private void next() {
        if (level < maxLevel) {
            level++;
            new AlertDialog.Builder(this).setTitle("提示").setMessage("即将进入右脚站立平衡测试").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(StandTestingActivity.this, StandMainActivity.class);
                    intent.putExtra("level", level);
                    startActivity(intent);
                    finish();
                }
            }).setCancelable(false).show();
        } else {
            Intent intent = new Intent(StandTestingActivity.this, FeedBackActivity.class);
            intent.putExtra("modelName", ModuleHelper.MODULE_STAND);
            intent.putExtra("level", level);
            startActivity(intent);
            finish();
        }
       /* level++;
        Intent intent = new Intent(StandTestingActivity.this, FeedBackActivity_v2.class);
        intent.putExtra("modelName", ModuleHelper.MODULE_STAND);
        intent.putExtra("level", level);
        startActivity(intent);
        finish();*/
    }

    class SaveTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(StandTestingActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("正在保存数据，请稍后。。。");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FileUtils.open();
            FileUtils.write(String.valueOf(level + 1) + "\n");
            FileUtils.write("ACC \n");
            for (FloatVector acc : accVectors) {
                FileUtils.write(acc.timeStamp + ", " + acc.x + ", " + acc.y + ", " + acc.z + "\n");
                Log.d("GoActivity", String.valueOf(acc.timeStamp));
            }
            FileUtils.write("GYRO \n");
            for (FloatVector gyro : gyroVectors) {
                FileUtils.write(gyro.timeStamp + ", " + gyro.x + ", " + gyro.y + ", " + gyro.z + "\n");
            }
            FileUtils.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            next();
        }
    }

    class AccEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (start) {
                if (level == 0) {
                    FileUtils.accLDatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else {
                    FileUtils.accRDatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                }
                /*FloatVector vector = new FloatVector(event.values[0], event.values[1], event.values[2]);
                accVectors.add(vector);*/
                pbx.setProgress((int) (event.values[0] * 10));
                pby.setProgress((int) (event.values[1] * 10));
                pbz.setProgress((int) (event.values[2] * 10));
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
                if (level == 0) {
                    FileUtils.gyroLDataList.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else {
                    FileUtils.gyroRDataList.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));

                }
               /* FloatVector gyroVector = new FloatVector(event.values[0], event.values[1], event.values[2]);
                gyroVectors.add(gyroVector);*/
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    protected void onPause() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        stopSensors();
        finish();

        super.onPause();
    }

    public void save() {
        FileUtils.open();
        FileUtils.write(String.valueOf(level + 1) + "\n");
        FileUtils.write("ACC \n");
        for (FloatVector acc : accVectors) {
            FileUtils.write(acc.timeStamp + ", " + acc.x + ", " + acc.y + ", " + acc.z + "\n");
            Log.d("GoActivity", String.valueOf(acc.timeStamp));
        }
        FileUtils.write("GYRO \n");
        for (FloatVector gyro : gyroVectors) {
            FileUtils.write(gyro.timeStamp + ", " + gyro.x + ", " + gyro.y + ", " + gyro.z + "\n");
        }
        FileUtils.close();
    }


    public void saveToStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);

        String filePath = History.getFilePath(this, ModuleHelper.MODULE_STAND);
        // Example: How to write data to file.
        File file = new File(filePath);
        try {
            FileWriter fileWrite = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWrite);
            bufferedWriter.write(isRight + "\n");
            bufferedWriter.write("ACC \n");
            for (FloatVector acc : accVectors) {
                bufferedWriter.write(acc.timeStamp + ", " + acc.x + ", " + acc.y + ", " + acc.z + "\n");
                Log.d("GoActivity", String.valueOf(acc.timeStamp));
            }
            bufferedWriter.write("GYRO \n");
            for (FloatVector gyro : gyroVectors) {
                bufferedWriter.write(gyro.timeStamp + ", " + gyro.x + ", " + gyro.y + ", " + gyro.z + "\n");
            }


            //Important! Have a new line in the end of txt file.
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWrite.close();
        } catch (IOException e) {
            Log.e("ExamAdapter", e.toString());
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HistoryFilePath", filePath);
        editor.apply();
        HistoryProvider historyProvider = new HistoryProvider(DataBaseHelper.getInstance(this));
        History history = new History(Dua.getInstance().getCurrentDuaId(), ModuleHelper.MODULE_STAND, filePath, String.valueOf(level));
        historyProvider.InsertHistory(history);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StandMainActivity.class));
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        new AlertDialog.Builder(this).setTitle("提示").setMessage("测试失败,重新开始").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(StandTestingActivity.this, StandMainActivity.class));
                finish();
            }
        }).setCancelable(false).show();
    }
}
