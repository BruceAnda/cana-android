package cn.ac.ict.cana.newversion.modules.tapper;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ac.ict.cana.R;
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity6;

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

    private int level;
    private String[] levels;
    private TextView tvTitle;

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
        level = getIntent().getIntExtra("level", 0);
        levels = getResources().getStringArray(R.array.hand);
        tvTitle = (TextView) findViewById(R.id.tv_tapper_title);
        tvTitle.setText(levels[level]);

        toggleHand = (ToggleButton) findViewById(R.id.toggle_hand);
        bt_begin = (Button) findViewById(R.id.bt_begin);
        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRight = toggleHand.isChecked();

                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;

                }


                Intent intent = new Intent(TapperMainActivity.this, TapperTestingActivity.class);
                intent.putExtra("isRight", isRight);
                intent.putExtra("level", level);
                startActivity(intent);
            }
        });

        mp = MediaPlayer.create(getApplicationContext(), R.raw.tapper_guide);
        mp.start();

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
        startActivity(new Intent(this, ModelGuideActivity6.class));
        finish();
    }
}
