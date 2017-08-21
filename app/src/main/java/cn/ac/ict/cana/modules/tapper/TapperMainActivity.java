package cn.ac.ict.cana.modules.tapper;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ac.ict.cana.R;

public class TapperMainActivity extends Activity {
    private Button bt_begin;
    private ToggleButton toggleHand;
    private boolean isRight = true;
    @BindView(R.id.btn_left)
    ImageButton btn_left;
    @BindView(R.id.btn_right)
    ImageButton btn_right;
    int right = 0;
    int left = 0;

    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapper_main);
        ButterKnife.bind(this);
        initWidget();
    }

    @OnClick({R.id.btn_left, R.id.btn_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                left++;
                break;
            case R.id.btn_right:
                right++;
                break;
        }
        if (left > 3 || right > 3) {
            Toast.makeText(this, getResources().getText(R.string.tapper_main_tip), Toast.LENGTH_SHORT).show();
            left = 0;
            right = 0;
        }

    }

    private void initWidget() {
        toggleHand = (ToggleButton) findViewById(R.id.toggle_hand);
        bt_begin = (Button) findViewById(R.id.bt_begin);
        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRight = toggleHand.isChecked();

                if(mp!=null)
                {
                    mp.stop();
                    mp.release();
                    mp=null;

                }

                Intent intent = new Intent(TapperMainActivity.this, TapperTestingActivity.class);
                intent.putExtra("isRight", isRight);
                startActivity(intent);
            }
        });

        mp = MediaPlayer.create(getApplicationContext(), R.raw.tapper_guide);
        mp.start();

    }

    @Override
    protected void onPause() {
        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp = null;
        }
        finish();
        super.onPause();
    }
}
