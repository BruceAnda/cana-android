package cn.ac.ict.cana.newversion.modules.tremor;

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

public class TremorTestingActivity extends Activity {

    private static final String TAG = TremorTestingActivity.class.getSimpleName();
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
    private int grade;
    private int maxGrade = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tremor_testing);
        Intent intent = getIntent();
        grade = intent.getIntExtra("grade", 0);
        isRight = intent.getBooleanExtra("isRight", true);
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
        end.add(Calendar.SECOND, 20);
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
        sm = (SensorManager) TremorTestingActivity.this.getSystemService(Context.SENSOR_SERVICE);
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
        //saveToStorage();
        // save();
       /* if (grade < maxGrade) {
            Intent intent = new Intent(TremorTestingActivity.this, TremorMainActivity.class);
            intent.putExtra("grade", grade);
            startActivity(intent);
        } else {
            Intent intent = new Intent(TremorTestingActivity.this, FeedBackActivity_v2.class);
            intent.putExtra("modelName", ModuleHelper.MODULE_TREMOR);
            startActivity(intent);
        }*/


    }

    class SaveTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TremorTestingActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("正在保存数据，请稍后。。。");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FileUtils.open();
            FileUtils.write(String.valueOf(grade + 1) + "\n");
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

    private void next() {
        grade++;
        Intent intent = new Intent(TremorTestingActivity.this, FeedBackActivity.class);
        intent.putExtra("modelName", ModuleHelper.MODULE_TREMOR);
        intent.putExtra("grade", grade);
        startActivity(intent);
        finish();
    }

    class AccEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (start) {
                if (grade == 0) {
                    FileUtils.tremor_lr_accdatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else if (grade == 1) {
                    FileUtils.tremor_lp_accdatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else if (grade == 2) {
                    FileUtils.tremor_rr_accdatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else if (grade == 3) {
                    FileUtils.tremor_rp_accdatalist.add(new AccData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                }
               /* FloatVector vector = new FloatVector(event.values[0], event.values[1], event.values[2]);
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
                if (grade == 0) {
                    FileUtils.tremor_lr_gyrodatalist.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else if (grade == 1) {
                    FileUtils.tremor_lp_gyrodatalist.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else if (grade == 2) {
                    FileUtils.tremor_rr_gyrodatalist.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                } else if (grade == 3) {
                    FileUtils.tremor_rp_gyrodatalist.add(new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]));
                }
                /*FloatVector gyroVector = new FloatVector(event.values[0], event.values[1], event.values[2]);
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
        FileUtils.write(String.valueOf(grade + 1) + "\n");
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

        /*FileUtils.writeData(String.valueOf(grade) + "\n");
        FileUtils.writeData("ACC \n");
        for (FloatVector acc : accVectors) {
            FileUtils.writeData(acc.timeStamp + ", " + acc.x + ", " + acc.y + ", " + acc.z + "\n");
            Log.d("GoActivity", String.valueOf(acc.timeStamp));
        }
        FileUtils.writeData("GYRO \n");
        for (FloatVector gyro : gyroVectors) {
            FileUtils.writeData(gyro.timeStamp + ", " + gyro.x + ", " + gyro.y + ", " + gyro.z + "\n");
        }*/
    }

    public void saveToStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);

        String filePath = History.getFilePath(this, ModuleHelper.MODULE_TREMOR);
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
        History history = new History(Dua.getInstance().getCurrentDuaId(), ModuleHelper.MODULE_TREMOR, filePath, String.valueOf(grade));
        historyProvider.InsertHistory(history);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TremorMainActivity.class));
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        new AlertDialog.Builder(this).setTitle("提示").setMessage("测试失败,重新开始").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(TremorTestingActivity.this, TremorMainActivity.class));
                finish();
            }
        }).setCancelable(false).show();
    }
}
