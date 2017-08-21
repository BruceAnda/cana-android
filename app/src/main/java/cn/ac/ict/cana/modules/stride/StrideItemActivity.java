package cn.ac.ict.cana.modules.stride;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ac.ict.cana.R;

public class StrideItemActivity extends Activity {
    Button tug_btn = null;
    Button walking_btn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_item);
        init_widget();
    }

    private void init_widget() {
        tug_btn = (Button) findViewById(R.id.stride_tug_btn);
        walking_btn = (Button) findViewById(R.id.stride_walking_btn);
        tug_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StrideItemActivity.this, StrideMainActivity.class));
            }
        });
        walking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StrideItemActivity.this, StrideWalkingActivity.class));
            }
        });
    }
}
