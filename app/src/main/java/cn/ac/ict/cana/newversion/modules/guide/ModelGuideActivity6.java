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
import cn.ac.ict.cana.newversion.modules.tapper.TapperMainActivity;
import cn.ac.ict.cana.newversion.utils.FileUtils;

/**
 * 手指灵敏
 */
public class ModelGuideActivity6 extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_guide6);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FileUtils.filePath = History.getFilePath(this, ModuleHelper.MODULE_TAPPER);

        FileUtils.tapperLDatas = new ArrayList();
        FileUtils.tapperRDatas = new ArrayList();
    }

    public void start(View view) {
        Intent intent = new Intent(ModelGuideActivity6.this, TapperMainActivity.class);
        intent.putExtra("level", 0);
        startActivity(intent);
        finish();
    }

    public void pre(View view) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回上一测试项?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ModelGuideActivity6.this, ModelGuideActivity5.class));
                finish();
            }
        }).setCancelable(false).show();
    }

    public void next(View view) {
        startActivity(new Intent(this, ModelGuideActivity7.class));
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
