package cn.ac.ict.cana.newversion.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.lovearthstudio.duasdk.Dua;

import java.io.IOException;
import java.util.ArrayList;

import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.providers.UserProvider;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import zhaoliang.com.uploadfile.UploadUtils;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class HistoryProvider {
    private static final String TAG = HistoryProvider.class.getSimpleName();
    private SQLiteDatabase mDatabase;
    private UserProvider userProvider;
    private String[] mHistoryColumns = {DataBaseHelper.HISTORY_ID_NEW, DataBaseHelper.HISTORY_USER_ID_NEW, DataBaseHelper.HISTORY_TYPE_NEW, DataBaseHelper.HISTORY_FILE_NEW,
            DataBaseHelper.HISTORY_IS_UPLOADED_NEW, DataBaseHelper.HISTORY_LEVEL, "datetime(history_create_time, 'localtime') as history_create_time"};
    private int total;

    public HistoryProvider(DataBaseHelper dataBaseHelper) {
        userProvider = new UserProvider(dataBaseHelper);
        mDatabase = dataBaseHelper.getWritableDatabase();
    }

    public long InsertHistory(History history) {

        //TODO: check whether history.userId exists.
        ContentValues values = new ContentValues();

        values.put(DataBaseHelper.HISTORY_USER_ID_NEW, history.userid);
        values.put(DataBaseHelper.HISTORY_TYPE, history.type);
        values.put(DataBaseHelper.HISTORY_FILE, history.filePath);
        values.put(DataBaseHelper.HISTORY_IS_UPLOADED_NEW, history.isUpload ? 1 : 0);
        values.put(DataBaseHelper.HISTORY_LEVEL, history.level);
        values.put(DataBaseHelper.HISTORY_MARK, history.mark);
        return mDatabase.insert(DataBaseHelper.HISTORY_TABLE_NAME_NEW, null, values);
    }

    public void updateHistory(History history) {
        ContentValues values = new ContentValues();

        values.put(DataBaseHelper.HISTORY_USER_ID_NEW, history.userid);
        values.put(DataBaseHelper.HISTORY_TYPE, history.type);
        values.put(DataBaseHelper.HISTORY_FILE, history.filePath);
        values.put(DataBaseHelper.HISTORY_IS_UPLOADED_NEW, 1);
        values.put(DataBaseHelper.HISTORY_LEVEL, history.level);

        mDatabase.update(DataBaseHelper.HISTORY_TABLE_NAME_NEW, values, DataBaseHelper.HISTORY_ID_NEW + " = ?", new String[]{String.valueOf(history.id)});
    }

    public ArrayList<History> getHistories() {
        Cursor cursor = mDatabase.query(DataBaseHelper.HISTORY_TABLE_NAME_NEW, null, null, null, null, null, DataBaseHelper.HISTORY_ID_NEW);
        return loadHistoryFromCursor(cursor);
    }

    public ArrayList<History> getHistoriesNoUpLoad() {
        Cursor cursor = mDatabase.query(DataBaseHelper.HISTORY_TABLE_NAME_NEW, null, DataBaseHelper.HISTORY_IS_UPLOADED_NEW + " = ?", new String[]{String.valueOf(0)}, null, null, DataBaseHelper.HISTORY_ID_NEW);
        return loadHistoryFromCursor(cursor);
    }

    public History getHistoryById(Long queryId) {
        Cursor cursor = mDatabase.query(DataBaseHelper.HISTORY_TABLE_NAME_NEW, null, DataBaseHelper.HISTORY_ID_NEW + " = ?", new String[]{String.valueOf(queryId)}, null, null, null);
        History history = null;
        if (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.HISTORY_ID_NEW));
            long userId = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.HISTORY_USER_ID_NEW));
            String type = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_TYPE_NEW));
            String file = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_FILE_NEW));
            boolean isUploaded = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.HISTORY_IS_UPLOADED_NEW)) == 1;
            String createdTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_CREATE_TIME_NEW));
            String mark = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_MARK));
            history = new History(id, userId, type, file, isUploaded, createdTime, mark);
        }
        return history;
    }

    private ArrayList<History> loadHistoryFromCursor(Cursor cursor) {
        ArrayList<History> histories = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.HISTORY_ID_NEW));
                long userId = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.HISTORY_USER_ID_NEW));
                String type = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_TYPE_NEW));
                String file = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_FILE_NEW));
                boolean isUploaded = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.HISTORY_IS_UPLOADED_NEW)) == 1;
                String createdTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_CREATE_TIME_NEW));
                String mark = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_MARK));
                histories.add(new History(id, userId, type, file, isUploaded, createdTime, mark));
            }
            cursor.close();
        }
        return histories;
    }

   /* private ArrayList<History> getHistoriesByIds(ArrayList<Long> ids) {
        String idString = TextUtils.join(",", ids);
        String QueryString = String.format("SELECT * FROM " + DataBaseHelper.HISTORY_TABLE_NAME + " WHERE " + DataBaseHelper.HISTORY_ID + " IN (%s)", new String[]{idString});
        Log.d("HistoryProvider", "QueryString" + QueryString);
        Cursor cursor = mDatabase.rawQuery(QueryString, null);

        return loadHistoryFromCursor(cursor);
    }

    public History getHistory(Long id) {
        Cursor cursor = mDatabase.query(DataBaseHelper.HISTORY_TABLE_NAME, mHistoryColumns, "_id =" + id, null, null, null, null);
        return loadHistoryFromCursor(cursor).get(0);
    }*/

   /* private User getUser(ArrayList<User> users, History history) {
        for (int j = 0; j < users.size(); j++) {
            if (history.uuid.equals(users.get(j).uuid)) {
                return users.get(j);
            }
        }
        return null;
    }*/

   /* private History getHistory(ArrayList<History> histories, ContentValues item) {
        long id = (long) item.get("id");
        for (History history : histories) {
            if (history.id == id) {
                return history;
            }
        }
        return null;
    }*/


    public void uploadHistories(ArrayList<ContentValues> items) {
        UploadUtils.initOSS();
        for (ContentValues item : items) {
            System.out.println(item.get("id"));
            final History history = getHistoryById((Long) item.get("id"));
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
                        updateHistory(history);
                    }
                }, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    OkHttpClient client = new OkHttpClient();

    public void post(String url) {
        Log.i("httpurl:", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        /*client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });*/
    }
       /* ArrayList<Long> ids = getIds(items);
        ArrayList<History> histories = getHistoriesByIds(ids);
        ArrayList<String> uuids = getUuids(histories);
        ArrayList<User> users = userProvider.getUsersByUuids(uuids);
      //  callList = new ArrayList<>();
        total = items.size();
        for (int i = 0; i < items.size(); i++) {
            Log.d("uploadHistories", "Uploading #" + String.valueOf(i));
//            final History history = histories.get(i);
            ContentValues item = items.get(i);
            final History history = getHistory(histories, item);
            final User user = getUser(users, history);

            final int groupPosition = (int) item.get("groupPosition");
            final int childPosition = (int) item.get("childPosition");
            if (user == null) {
                EventBus.getDefault().post(new ResponseEvent(false, history.id, total, groupPosition, childPosition));
                continue;
            }
            //   Log.d("GetUploadRequest", "Filepath: " + history.filePath + "; User name: " + user.name + ";");

            UploadUtils.asyncPutFile("test" + Dua.getInstance().duaUser.name + System.currentTimeMillis(), history.filePath, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    boolean result = false;
                    try {
                        // TODO: Change to Gson
                        updateHistoryUploadedById(history.id);
                        result = true;
                    } catch (Exception e) {
                        Log.e("toJson", e.toString());
                    }
                    EventBus.getDefault().post(new ResponseEvent(result, history.id, total, groupPosition, childPosition));
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    //EventBus.getDefault().post(new ResponseEvent(false, history.id, total, groupPosition, childPosition));
                    EventBus.getDefault().post(new ResponseEvent(true, history.id, total, groupPosition, childPosition));
                }
            }, null);
        }*/
    // return callList;
}

    /*private void updateHistoryUploadedById(Long id) {
        ContentValues args = new ContentValues();
        args.put(DataBaseHelper.HISTORY_IS_UPLOADED, 1);
        mDatabase.update(DataBaseHelper.HISTORY_TABLE_NAME, args, "_id=" + id, null);
    }*/

  /*  public void deleteHistories(ArrayList<ContentValues> items) {
        ArrayList<Long> ids = getIds(items);

        String idString = TextUtils.join(",", ids);
        String QueryString = String.format("DELETE FROM " + DataBaseHelper.HISTORY_TABLE_NAME + " WHERE " + DataBaseHelper.HISTORY_ID + " IN (%s)", new String[]{idString});

        mDatabase.execSQL(QueryString);
    }*/

/*    private ArrayList<Long> getIds(ArrayList<ContentValues> items) {
        ArrayList<Long> ids = new ArrayList<>();
        for (ContentValues item : items) {
            ids.add((Long) item.get("id"));
        }
        return ids;
    }

    private ArrayList<String> getUuids(ArrayList<History> histories) {
        Log.d("HistoryProvider", "getUuids");
        ArrayList<String> uuids = new ArrayList<>();
        for (History history : histories) {
            uuids.add(history.uuid);
        }
        return uuids;
    }*/

