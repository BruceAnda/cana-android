package cn.ac.ict.cana.modules.sound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;

public class SoundMainActivity extends FragmentActivity {

    private MediaRecorder recorder;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new MainFragment()).commit();
        path = getFilesDir().getAbsolutePath()+"/"+"temp.3gp";
    }

    public void prepareRecorder() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            recorder.setOutputFile(path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void finishTesting()
    {
        releaseRecorder();
        SaveToStorage();
        startActivity(new Intent(SoundMainActivity.this, ModuleHelper.getActivityAfterExam()));
        finish();
    }

    public void releaseRecorder() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    protected void onPause() {
        releaseRecorder();
        finish();
        super.onPause();
    }

    public void showDialog(final boolean isFirstPager) {

        SaveToStorage();
        if (isFirstPager) {
//            Log.d("ddd", "showDialog: ");
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new TestingFragment()).commit();
        } else {
            startActivity(new Intent(SoundMainActivity.this, ModuleHelper.getActivityAfterExam()));
            finish();
        }

    }

    private void SaveToStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);

        String filePath = History.getFilePath(this, ModuleHelper.MODULE_SOUND);

        File file = new File(path);
        Boolean result = file.renameTo(new File(filePath));
        Log.d("SaveToStorage", "save file result: " + result);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HistoryFilePath", filePath);
        editor.apply();
    }
}
