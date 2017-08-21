package cn.ac.ict.cana.newversion.modules.guide;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.modules.count.CountMainActivity;
import cn.ac.ict.cana.newversion.utils.FileUtils;

/**
 * 数字记忆
 */
public class ModelGuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_guide);
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_COUNT);
        FileUtils.countDataList = new ArrayList<>();
    }

    public void start(View view) {
        Intent intent = new Intent(ModelGuideActivity.this, CountMainActivity.class);
        intent.putExtra("grade", 3);
        startActivity(intent);
        finish();
    }

    public void next(View view) {
        startActivity(new Intent(this, ModelGuideActivity2.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否确定退出测试?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }
}
