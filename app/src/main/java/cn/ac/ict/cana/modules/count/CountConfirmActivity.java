package cn.ac.ict.cana.modules.count;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;

/**
 * Created by zhongxi on 2016/8/22.
 */
public class CountConfirmActivity extends Activity {

    private String randomStr;
    private EditText nextet;
    private Button nextbtn;
    private Intent intent;
    private int times;
    private boolean isRight;
    private ArrayList<String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_confirm);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(CountConfirmActivity.this,CountMainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){
        intent = this.getIntent();
        randomStr = intent.getStringExtra("data");
        randomStr = randomStr.substring(0,6);
        nextet = (EditText)findViewById(R.id.et_answer);
        nextet.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        nextet.addTextChangedListener(mTextWatcher);
        nextbtn = (Button) findViewById(R.id.btn_confirm);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = nextet.getText().toString().trim();
                result.add(str);
                if(str.equals(randomStr)){
                    isRight = true;
                    saveAndContinue();

                }else{
                    if(!isRight) {
                        times++;
                        if(times >=5){
                            saveAndContinue();
                        }
                    }
                    Toast.makeText(CountConfirmActivity.this, String.format(Locale.CHINA, getString(R.string.count_wrong_answer), times),Toast.LENGTH_SHORT).show();
                }
            }
        });

        result = new ArrayList<>();
    }

    protected void saveAndContinue() {
        String content = randomStr;
        if(isRight){
            content+=";1";
        }else{
            content+=";0";
        }
        for(String x: result){
            content+=";"+x;
        }

        saveToStorage(content);
        startActivity(new Intent(CountConfirmActivity.this, ModuleHelper.getActivityAfterExam()));
        finish();
    }

    public void saveToStorage(String content){
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
        //EventBus.getDefault().post(new NewHistoryEvent());

//        Toast.makeText(getApplicationContext(), CountEvaluation.evaluation(history),Toast.LENGTH_SHORT).show();
    }

    TextWatcher mTextWatcher = new TextWatcher() {

        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(temp.length()>=6){
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(CountConfirmActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    };
}
