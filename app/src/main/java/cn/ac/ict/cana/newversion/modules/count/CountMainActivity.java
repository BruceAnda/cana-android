package cn.ac.ict.cana.newversion.modules.count;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity;

/**
 * Created by zhongxi on 2016/8/30.
 */
public class CountMainActivity extends Activity {

    //    Button bt_begin;
//    Vibrator vibrator;
    private MediaPlayer mp;
    private long[] pattern = {100, 400};
    //    RadioGroup vchoice_RG;
    private Intent intent;
    private Button picBtn;
    private Button souBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_main);

        init();
    }

    public void startTest(View view) {
        intent = new Intent(getApplicationContext(), CountGameActivity.class);
        startActivity(intent);
        finish();
    }

    public void init() {

        picBtn = (Button) findViewById(R.id.count_picture_btn);
        souBtn = (Button) findViewById(R.id.count_sound_btn);

//        vchoice_RG = (RadioGroup)findViewById(R.id.count_version_choice);
//        vchoice_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                int radioButtonId = vchoice_RG.getCheckedRadioButtonId();
//                if(radioButtonId == R.id.count_picture){
//                    intent = new Intent(getApplicationContext(), CountGameActivity.class);
//                }else{
//                    intent = new Intent(getApplicationContext(), CountSoundActivity.class);;
//                }
//            }
//        });
        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), CountGameActivity.class);
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;

                }

                startActivity(intent);
                finish();

            }
        });

        souBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), CountSoundActivity.class);
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;

                }

                startActivity(intent);
                finish();

            }
        });

//        bt_begin = (Button)findViewById(R.id.count_begin);
//        bt_begin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mp!=null)
//                {
//                    mp.stop();
//                    mp.release();
//                    mp=null;
//
//                }
//
//                startActivity(intent);
//                finish();
//            }
//        });

//        intent = new Intent(getApplicationContext(), CountGameActivity.class);
//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.count_guide);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                vibrator.vibrate(pattern, -1);
                mp = null;
            }
        });

        // mp.start();
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
        startActivity(new Intent(this, ModelGuideActivity.class));
        finish();
    }
}

