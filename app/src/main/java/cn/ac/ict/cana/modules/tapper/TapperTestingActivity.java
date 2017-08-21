package cn.ac.ict.cana.modules.tapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bcgdv.asia.lib.ticktock.TickTockView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;

public class TapperTestingActivity extends Activity {

    @BindView(R.id.tv_left_count)
    TextView tvLeft;
    @BindView(R.id.tv_right_count)
    TextView tvRight;
    @BindView(R.id.btn_left)
    ImageButton btnLeft;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.ttv_tapper)
    TickTockView ttv;

    private int leftCount, rightCount;
    private ArrayList<String> content;
    private boolean isRight = false;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapper_testing);
        ButterKnife.bind(this);
        content = new ArrayList<>();
        isRight = getIntent().getBooleanExtra("isRight",true);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.countdown);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                initTickTockView();
                setEnable(true);
            }
        });
        mp.start();
        setEnable(false);
    }

    private void initTickTockView()
    {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.SECOND,-1);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.SECOND,10);
        ttv.setOnTickListener(new TickTockView.OnTickListener() {
            @Override
            public String getText(long timeRemainingInMillis) {
                if(timeRemainingInMillis<=0)
                {
//                    mp.release();
//                    mp = null;
                    saveToStorage();
                    startActivity(new Intent(TapperTestingActivity.this, ModuleHelper.getActivityAfterExam()));
                    finish();
                }
                return String.valueOf(timeRemainingInMillis/1000+1);
            }
        });
        ttv.start(start,end);
    }
    @OnClick({R.id.btn_left, R.id.btn_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                tvLeft.setText(String.valueOf(++leftCount));
                content.add("left:" + System.currentTimeMillis());
                break;
            case R.id.btn_right:
                tvRight.setText(String.valueOf(++rightCount));
                content.add("right:" + System.currentTimeMillis());
                break;
        }
    }

    private void setEnable(boolean enabled2) {
        btnLeft.setEnabled(enabled2);
        btnRight.setEnabled(enabled2);
    }


    private void initNum() {
        leftCount = 0;
        rightCount = 0;
        tvLeft.setText(String.valueOf(0));
        tvRight.setText(String.valueOf(0));
    }

    public void saveToStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);

        String filePath = History.getFilePath(this, ModuleHelper.MODULE_TAPPER);
        // Example: How to write data to file.
        File file = new File(filePath);
        try {
            FileWriter fileWrite = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWrite);
            bufferedWriter.write(isRight + "\n");
            for (String line : content) {
                bufferedWriter.write(line + "\n");
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

    @Override
    protected void onPause() {

        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp = null;
        }
        if(ttv!=null)
        {
            ttv.stop();
            ttv = null;
        }
//        MainActivity_.intent(TapperTestingActivity.this).start();
        finish();
        super.onPause();
    }

}
