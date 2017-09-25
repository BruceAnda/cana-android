package cn.ac.ict.cana.modules.count;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;
import cn.ac.ict.cana.newversion.modules.guide.ModelGuideActivity;

/**
 * Created by zhongxi on 2016/10/19.
 */
public class CountSimKeyboardActivity extends Activity {

    private String randomStr;
    private String version;
    private TextView nextet;
    private Button count_simkeyboard_confirmBtn;
    private Intent intent;
    private int times;
    private boolean isRight;
    private ArrayList<String> result;

    private GridLayout gridLayout;
    private TextView tv;
//    private Button confirmBtn;

    private String[] chars;

    private int[] source;
    private SoundPool pool;
    private Map<String, Integer> poolMap;
    private boolean isLoad;
    public boolean isMusic;
    private Button musicBtn;
    private boolean isNotFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_simkeyboard);

        init();
    }

    public void init() {

        isMusic = true;
        isNotFull = true;
        pool = new SoundPool(11, AudioManager.STREAM_MUSIC, 0);
        poolMap = new HashMap<>();
        source = new int[]{
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
                R.raw.counts_del
        };

        for (int i = 0; i < 11; i++) {
            poolMap.put("index" + i, pool.load(this, source[i], 1));
        }
        pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                if (sampleId == poolMap.size()) {
                    isLoad = true;
                }
            }
        });

        musicBtn = (Button) findViewById(R.id.count_voice);
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMusic) {
                    musicBtn.setBackgroundResource(R.drawable.count_keymusic_close);
                    isMusic = false;
                } else {
                    musicBtn.setBackgroundResource(R.drawable.count_keymusic_open);
                    isMusic = true;
                }
            }
        });


        chars = new String[]{
                "7", "8", "9",
                "6", "5", "4",
                "3", "2", "1",
                "0", getApplicationContext().getString(R.string.count_sim_clear), getApplication().getString(R.string.count_sim_delete)
        };

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int oneQuarterWidth = (int) (screenWidth * 0.30);

        intent = this.getIntent();
        randomStr = intent.getStringExtra("data");
        version = intent.getStringExtra("version");
        randomStr = randomStr.substring(0, 6);
        nextet = (TextView) findViewById(R.id.count_simkeyboard_tv);
        nextet.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        count_simkeyboard_confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = nextet.getText().toString().trim();
                result.add(str);
                if (str.equals(randomStr)) {
                    isRight = true;
                    saveAndContinue();

                } else {
                    if (!isRight) {
                        times++;
                        if (times >= 5) {
                            saveAndContinue();
                        }
                    }
//                    Toast.makeText(CountSimKeyboardActivity.this, String.format(Locale.CHINA, getString(R.string.count_wrong_answer), times),Toast.LENGTH_SHORT).show();

                    if (times < 5) {
                        Toast.makeText(CountSimKeyboardActivity.this, String.format(Locale.CHINA, getString(R.string.count_wrong_answer), times), Toast.LENGTH_SHORT).show();

//                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(CountSimKeyboardActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText(getString(R.string.input_hint))
//                                .setContentText(String.format(Locale.CHINA, getString(R.string.count_wrong_answer),times))
//                                .setConfirmText(getString(R.string.btn_confirm))
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        sDialog.dismissWithAnimation();
////                                    sDialog.cancel();
//                                    }
//                                });
//                        sweetAlertDialog.show();
                    }

                }
            }
        });

        result = new ArrayList<>();

//        confirmBtn = (Button)findViewById(R.id.count_simkeyboard_confirmBtn);
        tv = (TextView) findViewById(R.id.count_simkeyboard_tv);
        gridLayout = (GridLayout) findViewById(R.id.coutn_gridlayout_root);
        for (int i = 0; i < chars.length; i++) {
            Button btn = new Button(this);
//            btn.setTextColor(getResources().getColor(R.color.freebie_9));
            btn.setBackgroundResource(R.drawable.count_key_button_style);

//            btn.setBackgroundResource(R.drawable.count_compute_bg);
//            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT,GridLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(10, 10, 10, 10);
//            btn.setLayoutParams(lp);

//            btn.setBackground();
//            btn.setBackgroundColor(getResources().getColor(R.color.freebie_2));

            btn.setText(chars[i]);
            if (i > 9) {
                btn.setTextSize(30);
            } else {
                btn.setTextSize(40);
            }
//            btn.setPadding(5,35,5,35);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Button btn = (Button) arg0;

                    String btnText = btn.getText().toString().trim();

                    if (!isNotFull && btnText.length() < 2) {

                        Toast.makeText(CountSimKeyboardActivity.this, getString(R.string.input_full_hint), Toast.LENGTH_SHORT).show();
//                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(CountSimKeyboardActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText(getString(R.string.input_hint))
//                                .setContentText(getString(R.string.input_full_hint))
//                                .setConfirmText(getString(R.string.btn_confirm))
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        sDialog.dismissWithAnimation();
////                                    sDialog.cancel();
//                                    }
//                                });
//                        sweetAlertDialog.show();
                    }

                    if (isMusic && isNotFull) {
                        if (btnText.length() < 2) {
                            pool.play(poolMap.get("index" + String.valueOf(btnText)), 1.0f, 1.0f, 0, 1, 1.0f);
                        } else {
                            pool.play(poolMap.get("index" + 10), 1.0f, 1.0f, 0, 1, 1.0f);
                        }
                    }
                    if (isMusic && !isNotFull) {
                        pool.play(poolMap.get("index" + 10), 1.0f, 1.0f, 0, 1, 1.0f);
                    }

                    String str = tv.getText().toString();
                    if (btnText.equals("删除") || btnText.equals("delete")) {
                        deleteText(arg0);
                        isNotFull = true;
                    } else if (btnText.equals("清空") || btnText.equals("clear")) {
                        clearText(arg0);
                        isNotFull = true;
                    } else {

                        btnText = str.concat(btnText);
                        tv.setText(btnText);
                        if (btnText.length() > 5) {
                            isNotFull = false;
                        } else {
                            isNotFull = true;
                        }
                    }
                }

            });
            GridLayout.Spec rowSpec = GridLayout.spec(i / 3);
            GridLayout.Spec colSpec = GridLayout.spec(i % 3);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.setGravity(Gravity.FILL);
            gridLayout.addView(btn, params);
            params.width = oneQuarterWidth;
        }

    }

    protected void saveAndContinue() {
        String content = randomStr;
        if (isRight) {
            content += ";1";
        } else {
            content += ";0";
        }
        if (version.equals("sound")) {
            content += ";1";
        } else {
            content += ";0";
        }
        for (String x : result) {
            content += ";" + x;
        }

        saveToStorage(content);
        startActivity(new Intent(CountSimKeyboardActivity.this, ModuleHelper.getActivityAfterExam()));
        finish();
    }

    public void saveToStorage(String content) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);
//        String uuid = sharedPreferences.getString("selectedUser", "None");
//        HistoryProvider historyProvider = new HistoryProvider(DataBaseHelper.getInstance(this));
//        History history = new History(this, uuid, ModuleHelper.MODULE_COUNT);

        // Example: How to write data to file.
        String filePath = History.getFilePath(this, ModuleHelper.MODULE_COUNT);
        File file = new File(filePath);
        try {
            FileWriter fileWrite = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWrite);

            bufferedWriter.write(content);

            //Important! Have a new line in the end of txt file.
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWrite.close();
        } catch (IOException e) {
            Log.e("ExamAdapter", e.toString());
        }

//        history.id = historyProvider.InsertHistory(history);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HistoryFilePath", filePath);
        editor.apply();
//        Log.d("CountSaveToStorage", String.valueOf(history.id));
       // EventBus.getDefault().post(new NewHistoryEvent());

//        Toast.makeText(getApplicationContext(), CountEvaluation.evaluation(history),Toast.LENGTH_SHORT).show();
    }

    public void clearText(View v) {
        tv.setText("");
    }

    public void deleteText(View v) {
        String str = tv.getText().toString().trim();
        if (str.equals("")) {
            return;
        }
        str = str.substring(0, str.length() - 1);
        tv.setText(str);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(CountSimKeyboardActivity.this, ModelGuideActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (pool != null) {
            pool.release();
            pool = null;
        }

        super.onDestroy();
    }
}
