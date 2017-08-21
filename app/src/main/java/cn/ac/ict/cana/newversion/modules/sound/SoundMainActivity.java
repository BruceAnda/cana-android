package cn.ac.ict.cana.newversion.modules.sound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.lovearthstudio.duasdk.Dua;

import java.io.File;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.activities.FeedBackActivity;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity3;
import cn.ac.ict.cana.newversion.provider.HistoryProvider;
import cn.ac.ict.cana.newversion.utils.FileUtils;

public class SoundMainActivity extends FragmentActivity {

    private static final String TAG = SoundMainActivity.class.getSimpleName();
    private MediaRecorder recorder;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new MainFragment()).commit();
        path = FileUtils.filePath;
    }

    public void prepareRecorder() {
        if (recorder == null) {
            try {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setOutputFile(path);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void finishTesting() {
        releaseRecorder();
        // SaveToStorage();
        Intent intent = new Intent(SoundMainActivity.this, FeedBackActivity.class);
        intent.putExtra("modelName", ModuleHelper.MODULE_SOUND);
        startActivity(intent);
        finish();
    }

    public void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        releaseRecorder();
        finish();
        super.onPause();
    }

    public void showDialog(final boolean isFirstPager) {

        // SaveToStorage();
        if (isFirstPager) {
//            Log.d("ddd", "showDialog: ");
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new TestingFragment()).commit();
        } else {
            startActivity(new Intent(SoundMainActivity.this, ModuleHelper.getActivityAfterExam()));
            finish();
        }

    }

    private void SaveToStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);

        String filePath = History.getFilePath(this, ModuleHelper.MODULE_SOUND);

        File file = new File(path);
        Boolean result = file.renameTo(new File(filePath));
        Log.d("SaveToStorage", "save file result: " + result);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HistoryFilePath", filePath);
        editor.apply();
        HistoryProvider historyProvider = new HistoryProvider(DataBaseHelper.getInstance(this));
        History history = new History(Dua.getInstance().getCurrentDuaId(), ModuleHelper.MODULE_SOUND, filePath);
        historyProvider.InsertHistory(history);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ModelGuideActivity3.class));
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        new AlertDialog.Builder(this).setTitle("提示").setMessage("测试失败,重新开始").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(SoundMainActivity.this, SoundMainActivity.class));
                finish();
            }
        }).setCancelable(false).show();
    }
}
