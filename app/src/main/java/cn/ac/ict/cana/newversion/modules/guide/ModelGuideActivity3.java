package cn.ac.ict.cana.newversion.modules.guide;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.modules.sound.SoundMainActivity;
import cn.ac.ict.cana.newversion.utils.FileUtils;

/**
 * 语言测试
 */
public class ModelGuideActivity3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_guide3);
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_SOUND);
    }

    public void start(View view) {
        startActivity(new Intent(ModelGuideActivity3.this, SoundMainActivity.class));
        finish();
    }

    public void pre(View view) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ModelGuideActivity3.this, ModelGuideActivity2.class));
                finish();
            }
        }).setCancelable(false).show();
    }

    public void next(View view) {
        startActivity(new Intent(this, ModelGuideActivity4.class));
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
