package cn.ac.ict.cana.newversion.modules.guide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.modules.face.FaceMainActivity;
import cn.ac.ict.cana.newversion.modules.upload.UploadActivity;
import cn.ac.ict.cana.newversion.utils.FileUtils;

public class ModelGuideActivity7 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_guide7);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_FACE);
    }

    public void start(View view) {
        Intent intent = new Intent(ModelGuideActivity7.this, FaceMainActivity.class);
        startActivity(intent);
        finish();
    }

    public void pre(View view) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ModelGuideActivity7.this, ModelGuideActivity6.class));
                finish();
            }
        }).setCancelable(false).show();
    }

    public void next(View view) {
        startActivity(new Intent(this, UploadActivity.class));
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
