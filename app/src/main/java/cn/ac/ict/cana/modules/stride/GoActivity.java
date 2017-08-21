package cn.ac.ict.cana.modules.stride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;
import cn.ac.ict.cana.utils.FloatVector;
import jama.Matrix;
import jkalman.JKalman;

public class GoActivity extends Activity {
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
    ArrayList<FloatVector> accFloatVectors;
    ArrayList<FloatVector> gyroFloatVectors;


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
                FloatVector vector = new FloatVector(event.values[0], event.values[1], event.values[2]);
                accFloatVectors.add(vector);
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
                FloatVector gyroFloatVector = new FloatVector(event.values[0], event.values[1], event.values[2]);
                gyroFloatVectors.add(gyroFloatVector);
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
            saveToStorage(accFloatVectors,gyroFloatVectors);
            int test = getStrideNumbers(genKalman(genOneDimension(accFloatVectors)));
            Toast.makeText(GoActivity.this,"哈哈"+test,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GoActivity.this, ModuleHelper.getActivityAfterExam());
            startActivity(intent);
            finish();
        }
    }

    private void prepare() {

        mp.start();
        goContent.setText(getString(R.string.stride_next_turn));

        btnGo.setVisibility(View.GONE);

        accFloatVectors = new ArrayList<>();
        gyroFloatVectors = new ArrayList<>();
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

    public void saveToStorage(ArrayList<FloatVector> accList,ArrayList<FloatVector> gyroList) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);

        String filePath = History.getFilePath(this, ModuleHelper.MODULE_STRIDE);
        // Example: How to write data to file.
        File file = new File(filePath);
        try {
            FileWriter fileWrite = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWrite);
            bufferedWriter.write("Stride Tug \n");
            bufferedWriter.write("ACC \n");
            for (FloatVector acc : accList) {
                bufferedWriter.write(acc.timeStamp + ", " + acc.x + ", " + acc.y + ", " + acc.z + "\n");
                Log.d("GoActivity", String.valueOf(acc.timeStamp));
            }
            bufferedWriter.write("GYRO \n");
            for (FloatVector gyro : gyroList) {
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
    }

    public static ArrayList<Double> genOneDimension(ArrayList<FloatVector> accList){
        ArrayList<Double> result = new ArrayList<>();
        double temp;
        for(int i=0;i<accList.size();i++) {
            temp = Math.sqrt(accList.get(i).x *accList.get(i).x + accList.get(i).y * accList.get(i).y + accList.get(i).z *accList.get(i).z);
            result.add(temp);
        }
        Log.e("dddOneDe",":"+result.size());
        return result;
    }

    public static ArrayList<Double> genKalman(ArrayList<Double> accList){
        ArrayList<Double> result = new ArrayList<>();
        try {
            JKalman kalman = new JKalman(1, 1);
            double[][] A= new double[][]{{1}};
            double[][] H= new double[][]{{1}};
            double[][] Q= new double[][]{{1}};
            double[][] R= new double[][]{{6}};
            kalman.setTransition_matrix(new Matrix(A));
            kalman.setMeasurement_matrix(new Matrix(H));
            kalman.setProcess_noise_cov(new Matrix(Q));
            kalman.setMeasurement_noise_cov(new Matrix(R));
            kalman.setError_cov_post(kalman.getError_cov_post().identity());

            //开始位置
            Matrix statePost = new Matrix(1,1);
            statePost.set(0, 0, accList.get(0));
            kalman.setState_post(statePost);

            Matrix measurementZ = new Matrix(1,1);
            Matrix predictX = null;
            Matrix currectionX = null;
            for(double data : accList){
                measurementZ.set(0, 0,data);
                predictX = kalman.Predict();
                currectionX = kalman.Correct(measurementZ);
                result.add(currectionX.get(0,0));
                Log.e("dddOneDe","currectionX:"+currectionX);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        Log.e("dddOneDe",":"+result.size());
    return result;
    }

    public static int getStrideNumbers(ArrayList<Double> arrayList){
        int result = 0;
        double last = arrayList.get(0);
        int lastIndex = 0;
        boolean isIncreased = true;
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i)>=last){
                last = arrayList.get(i);
                lastIndex = i;
                isIncreased = true;
            }else {
                if((isIncreased)&&(i-lastIndex>=10)){
                    result++;
                }
                isIncreased = false;
            }
        }
        Log.e("dddOneDe",":"+result);
        return result;
    }
}
