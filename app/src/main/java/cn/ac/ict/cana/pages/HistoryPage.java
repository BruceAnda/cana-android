package cn.ac.ict.cana.pages;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.activities.MainActivity;
import cn.ac.ict.cana.adapters.HistoryAdapter;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;
import cn.ac.ict.cana.providers.HistoryProvider;
import cn.ac.ict.cana.widget.TreeView;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class HistoryPage {

    static public View InitialHistoryPageView(final Context context) {
        View view = View.inflate(context, R.layout.pageview_history, null);
        final HistoryProvider historyProvider = new HistoryProvider(DataBaseHelper.getInstance(context));

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
        final HistoryAdapter historyAdapter = new HistoryAdapter(context, treeView, mGroup, mChild);
        treeView.setAdapter(historyAdapter);

        Button uploadButton = (Button) view.findViewById(R.id.bt_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) context;
                ArrayList<ContentValues> Ids = new ArrayList<>(historyAdapter.getCheckedIds());

                //activity.callArrayList = historyProvider.uploadHistories(Ids);
                historyProvider.uploadHistories(Ids);
                activity.showProgressBar(true, "Start Uploading..");
            }
        });
        uploadButton.setEnabled(false);

        Button uploadallButton = (Button) view.findViewById(R.id.bt_upload_all);
        uploadallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) context;

                ArrayList<ContentValues> Ids = new ArrayList<>(historyAdapter.getCheckedIds());

                //activity.callArrayList = historyProvider.uploadHistories(Ids);
                historyProvider.uploadHistories(Ids);
                activity.showProgressBar(true, "Start Uploading..");
            }
        });
        uploadButton.setEnabled(false);


        Button deleteButton = (Button) view.findViewById(R.id.bt_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ContentValues> Ids = new ArrayList<>(historyAdapter.getUnUploadIds());
                historyProvider.deleteHistories(Ids);
                historyAdapter.removeItems(Ids);
            }
        });
        return view;
    }
}

