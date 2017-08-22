package cn.ac.ict.cana.newversion.modules.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.lovearthstudio.duasdk.Dua;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.activities.MainActivityNew_;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.provider.HistoryProvider;
import cn.ac.ict.cana.newversion.utils.FileUtils;
import zhaoliang.com.uploadfile.UploadUtils;

/**
 * 上传数据界面
 */
public class UploadActivity extends Activity {

    private static final String TAG = UploadActivity.class.getName();
    private static final int CODE_UPLOAD = 0;
    private static final int CODE_UPLOAD_FINISH = 1;
    private static final int CODE_DISMISS_DIAOLG = 2;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPLOAD:
                    progressDialog.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个");
                    break;
                case CODE_UPLOAD_FINISH:
                    progressDialog.setMessage("上传完成！");
                    sendEmptyMessageDelayed(CODE_DISMISS_DIAOLG, 1000);
                    break;
                case CODE_DISMISS_DIAOLG:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    startActivity(new Intent(UploadActivity.this, MainActivityNew_.class));
                    finish();
                    break;
            }
        }
    };
    private ProgressDialog progressDialog;
    private int fileNum;
    private int currentFile = 1;
    private ArrayList<History> histories;
    private HistoryProvider historyProvider;

    private TextView tv_total;
    private TextView tv_count;
    private TextView tv_tremor;
    private TextView tv_sound;
    private TextView tv_stand;
    private TextView tv_stride;
    private TextView tv_tapper;
    private TextView tv_face;

    private int numCount, numTremor, numSound, numStand, numStride, numTapper, numFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_tremor = (TextView) findViewById(R.id.tv_tremor);
        tv_sound = (TextView) findViewById(R.id.tv_sound);
        tv_stand = (TextView) findViewById(R.id.tv_stand);
        tv_stride = (TextView) findViewById(R.id.tv_stride);
        tv_tapper = (TextView) findViewById(R.id.tv_tapper);
        tv_face = (TextView) findViewById(R.id.tv_face);

        historyProvider = new HistoryProvider(DataBaseHelper.getInstance(this));
        histories = historyProvider.getHistoriesNoUpLoad();
        fileNum = histories.size();
        tv_total.setText("总共(" + fileNum + ")个文件");
        for (History history : histories) {
            if (history.type.equals(ModuleHelper.MODULE_COUNT)) {
                numCount++;
            } else if (history.type.equals(ModuleHelper.MODULE_TREMOR)) {
                numTremor++;
            } else if (history.type.equals(ModuleHelper.MODULE_SOUND)) {
                numSound++;
            } else if (history.type.equals(ModuleHelper.MODULE_STAND)) {
                numStand++;
            } else if (history.type.equals(ModuleHelper.MODULE_STRIDE)) {
                numStride++;
            } else if (history.type.equals(ModuleHelper.MODULE_TAPPER)) {
                numTapper++;
            } else if (history.type.endsWith(ModuleHelper.MODULE_FACE)) {
                numFace++;
            }
        }
        tv_count.setText("数字记忆(" + numCount + ")");
        tv_tremor.setText("震颤情况(" + numTremor + ")");
        tv_sound.setText("语言能力(" + numSound + ")");
        tv_stand.setText("站立平衡(" + numStand + ")");
        tv_stride.setText("行走平衡(" + numStride + ")");
        tv_tapper.setText("手指灵敏(" + numTapper + ")");
        tv_face.setText("面部表情(" + numFace + ")");
    }

    public void upload(View view) {
        UploadUtils.initOSS();
        initProgressDialog();
        upload();
    }

    private void upload() {
        try {
            final History history = histories.get(currentFile - 1);
            /*String suffix = history.filePath.substring(history.filePath.lastIndexOf("."), history.filePath.length());
            String fileName = history.type + "_" + System.currentTimeMillis() + suffix;*/

            // http://api.ivita.org/event/2/parkins.walk/walkfile/-3/2
            // String url = "http://api.ivita.org/event/" + Dua.getInstance().getCurrentDuaId() + "/" + history.type + "/" + fileName + "/0/0";
            String dbMark = history.mark;
            JSONObject jsonObject = new JSONObject(dbMark);
            String fileName = jsonObject.optString("file");
            Log.i(TAG, fileName);

            String url = "http://api.ivita.org/event";
            //String mark = "{\"uid\":" + Dua.getInstance().getCurrentDuaUid() + ",\"data\":" + history.mark + ",\"type\":" + history.type
            //+"}";
            // post(url, mark);
            HttpParams params = new HttpParams();
            params.put("uid", String.valueOf(Dua.getInstance().getCurrentDuaId()));
            params.put("data", history.mark);
            params.put("type", history.type);
            params.put("tag", "Parkinson");
            params.put("batch", FileUtils.batch);
            post(url, params);

            UploadUtils.asyncPutFile(fileName, history.filePath, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    boolean result = false;
                    Log.i(TAG, "上传成功！");
                    try {
                        // TODO: Change to Gson
                        // updateHistoryUploadedById(history.id);
                        result = true;
                    } catch (Exception e) {
                        Log.e("toJson", e.toString());
                    }
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    Log.i(TAG, "上传失败！");
                    currentFile++;
                    if (currentFile <= fileNum) {
                        upload();
                        mHandler.sendEmptyMessage(CODE_UPLOAD);
                    } else {
                        mHandler.sendEmptyMessage(CODE_UPLOAD_FINISH);
                    }
                    File file = new File(history.filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    File file1 = new File(history.filePath.substring(0, history.filePath.lastIndexOf(".")));
                    if (file1.exists()) {
                        file1.delete();
                    }
                    history.isUpload = true;
                    historyProvider.updateHistory(history);
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示！");
        progressDialog.setMessage("共" + fileNum + "个文件当前上传第" + currentFile + "个");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(20);
        progressDialog.show();
    }

    public void post(String url, HttpParams params) {
        Log.i(TAG, "url:" + url + "-------params:" + params.getJsonParams());

        RxVolley.post(url, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                Log.i(TAG, t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Log.i(TAG, errorNo + "------" + strMsg);
            }
        });
    }
}
