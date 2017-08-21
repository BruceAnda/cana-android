package cn.ac.ict.cana.newversion.pagers;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.lovearthstudio.duasdk.Dua;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.activities.MainActivityNew;
import cn.ac.ict.cana.newversion.adapter.HistoryAdapter;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.newversion.provider.HistoryProvider;
import cn.ac.ict.cana.widget.TreeView;
import zhaoliang.com.uploadfile.UploadUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HIstoryPageFragment extends Fragment {


    private static final String TAG = HIstoryPageFragment.class.getSimpleName();
    private HistoryProvider historyProvider;
    private MainActivityNew activity;

    public HIstoryPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_page, container, false);
        historyProvider = new HistoryProvider(DataBaseHelper.getInstance(getContext()));
        activity = (MainActivityNew) getActivity();

        final TreeView treeView = (TreeView) view.findViewById(R.id.tree_view);
        ArrayList<ArrayList<History>> mChild = new ArrayList<ArrayList<History>>() {
            {
                for (int i = 0; i < ModuleHelper.ModuleList.size(); i++) {
                    add(new ArrayList<History>());
                }
            }
        };
        ArrayList<String> mGroup = ModuleHelper.ModuleList;
        ArrayList<History> historyList = historyProvider.getHistories();

        for (History history : historyList) {
            mChild.get(mGroup.indexOf(history.type)).add(history);
        }
        final HistoryAdapter historyAdapter = new HistoryAdapter(getContext(), treeView, mGroup, mChild);
        treeView.setAdapter(historyAdapter);

        Button uploadButton = (Button) view.findViewById(R.id.bt_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ContentValues> Ids = new ArrayList<>(historyAdapter.getCheckedIds());

                //activity.callArrayList = historyProvider.uploadHistories(Ids);
                uploadHistories(Ids);
            }
        });
        uploadButton.setEnabled(false);

        Button uploadallButton = (Button) view.findViewById(R.id.bt_upload_all);
        uploadallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ContentValues> Ids = new ArrayList<>(historyAdapter.getCheckedIds());

                // activity.callArrayList = historyProvider.uploadHistories(Ids);
                uploadHistories(Ids);
            }
        });
        uploadButton.setEnabled(false);


        Button deleteButton = (Button) view.findViewById(R.id.bt_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ContentValues> Ids = new ArrayList<>(historyAdapter.getUnUploadIds());
                //   historyProvider.deleteHistories(Ids);
                historyAdapter.removeItems(Ids);
            }
        });
        return view;
    }

    public void uploadHistories(ArrayList<ContentValues> items) {
        Toast.makeText(activity, "上传中", Toast.LENGTH_SHORT).show();

        activity.showProgressBar(true, "Start Uploading..");

        UploadUtils.initOSS();
        for (ContentValues item : items) {
            System.out.println(item.get("id"));
            final History history = historyProvider.getHistoryById((Long) item.get("id"));
            try {
                String fileName = history.type + System.currentTimeMillis();

                String url = "http://api.ivita.org/event/" + Dua.getInstance().getCurrentDuaId() + "/" + history.type + "/" + fileName + "/0/0";
                post(url);

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
                        history.isUpload = true;
                        historyProvider.updateHistory(history);
                    }
                }, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activity.showProgressBar(false, "关闭");
    }

    OkHttpClient client = new OkHttpClient();

    public void post(String url) {
        Log.i("httpurl:", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                
            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }
}
