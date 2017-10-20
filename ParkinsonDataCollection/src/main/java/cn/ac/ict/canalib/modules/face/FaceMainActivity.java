package cn.ac.ict.canalib.modules.face;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ac.ict.canalib.modules.guide.ModelGuideActivity7;
import cn.ac.ict.canalib.R;
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity7;

public class FaceMainActivity extends Activity {
    private Button bt_begin;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_main);
        initWidget();
    }
    private void initWidget()
    {
      mp = MediaPlayer.create(getApplicationContext(),R.raw.face_guide);
        mp.start();
        bt_begin = (Button)findViewById(R.id.bt_begin);

        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp!=null)
                {
                    mp.stop();
                    mp.release();
                    mp=null;

                }
                Intent intent = new Intent(FaceMainActivity.this,VideoCaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp=null;

        }
        finish();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ModelGuideActivity7.class));
        finish();
    }
}
