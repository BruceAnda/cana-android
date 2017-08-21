package cn.ac.ict.cana.modules.count;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.ac.ict.cana.R;

/**
 * Created by zhongxi on 2016/10/19.
 */
public class CountSoundActivity extends Activity {

    private final static int DATALNEGTH = 6;
    private SoundPool pool;
    private Map<String, Integer> poolMap;
    private Handler myHandler;
    private Runnable runnable;
    private int delayMillis;
    private static int count = -1;
    private boolean isLoad;
    private String strRandom;
    private MediaPlayer mp;

    private TextView tv;

    private int[] source = new int[]{
            R.raw.counts0,
            R.raw.counts1,
            R.raw.counts2,
            R.raw.counts3,
            R.raw.counts4,
            R.raw.counts5,
            R.raw.counts6,
            R.raw.counts7,
            R.raw.counts8,
            R.raw.counts9,
            R.raw.counts_dang

    };
    private int[] index = new int[DATALNEGTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_sound);
        init();
    }

    public void init() {

        tv = (TextView) findViewById(R.id.count_sound_hint);

        isLoad = false;
        strRandom = "";

        index = genRandomNumber();
        pool = new SoundPool(DATALNEGTH + 1, AudioManager.STREAM_MUSIC, 0);
        poolMap = new HashMap<>();
        delayMillis = 500;
        myHandler = new Handler();


        for (int i = 0; i < DATALNEGTH; i++) {
            poolMap.put("index" + i, pool.load(this, source[index[i]], 1));
        }
        poolMap.put("index-1", pool.load(this, source[10], 1));
        pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                if (sampleId == poolMap.size()) {
                    isLoad = true;
                }
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {

                pool.play(poolMap.get("index" + count), 1.0f, 1.0f, 0, 1, 1.0f);
                myHandler.postDelayed(this, 1500);
                count++;
                if (count > 5) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count = -1;
                    myHandler.removeCallbacks(runnable);
                    Intent intent = new Intent();
                    intent.setClass(CountSoundActivity.this, CountSimKeyboardActivity.class);
                    intent.putExtra("data", strRandom);
                    intent.putExtra("version", "sound");
                    startActivity(intent);
                    finish();
                }

            }
        };
        mp = MediaPlayer.create(getApplicationContext(), R.raw.count_sound_guide);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp = null;
                tv.setText(getString(R.string.count_listen_carefully_finish));
                runSound();
            }
        });

        mp.start();
    }

    public void runSound() {
        if (isLoad) {
            myHandler.postDelayed(runnable, 1500);
        }
    }

    public int[] genRandomNumber() {
        Random rand = new Random();
        int[] result = new int[DATALNEGTH];
        for (int i = 0; i < DATALNEGTH; i++) {
            result[i] = rand.nextInt(10);
            strRandom += result[i];
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        if (pool != null) {
            pool.release();
            pool = null;
        }

        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;

        }
        count = -1;
        super.onDestroy();
    }
}
