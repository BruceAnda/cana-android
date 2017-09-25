package cn.ac.ict.cana.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.lovearthstudio.duasdk.Dua;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.models.History;
import cn.ac.ict.cana.models.User;
import zhaoliang.com.uploadfile.UploadUtils;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class HistoryProvider {
    private SQLiteDatabase mDatabase;
    //  private OkHttpClient client;
    private UserProvider userProvider;
    private String baseUrl = "yubozu.ngrok.cc";
    //    private String baseUrl = "test.tunnel.mkdef.com:8080";
    private String[] mHistoryColumns = {DataBaseHelper.HISTORY_ID, DataBaseHelper.HISTORY_USER_UUID, DataBaseHelper.HISTORY_TYPE, DataBaseHelper.HISTORY_FILE,
            DataBaseHelper.HISTORY_IS_UPLOADED, "datetime(history_create_time, 'localtime') as history_create_time", DataBaseHelper.HISTORY_DOCTOR, DataBaseHelper.HISTORY_RATING};
    private int total;
    //  private ArrayList<Call> callList;

    public HistoryProvider(DataBaseHelper dataBaseHelper) {
        userProvider = new UserProvider(dataBaseHelper);
        mDatabase = dataBaseHelper.getWritableDatabase();
        //      client = new OkHttpClient.Builder()
        //             .connectTimeout(10, TimeUnit.SECONDS)
        ///             .writeTimeout(60, TimeUnit.SECONDS)
        //             .readTimeout(60, TimeUnit.SECONDS)
        //             .build();
    }

    public long InsertHistory(History history) {

        //TODO: check whether history.userId exists.
        ContentValues values = new ContentValues();

        values.put(DataBaseHelper.HISTORY_USER_UUID, history.uuid);
        values.put(DataBaseHelper.HISTORY_TYPE, history.type);
        values.put(DataBaseHelper.HISTORY_FILE, history.filePath);
        values.put(DataBaseHelper.HISTORY_IS_UPLOADED, history.isUpload);
        values.put(DataBaseHelper.HISTORY_RATING, history.rating);
        values.put(DataBaseHelper.HISTORY_DOCTOR, history.doctor);
        return mDatabase.insert(DataBaseHelper.HISTORY_TABLE_NAME, null, values);
    }

    public ArrayList<History> getHistories() {
        Cursor cursor = mDatabase.query(DataBaseHelper.HISTORY_TABLE_NAME, mHistoryColumns, null, null, null, null, DataBaseHelper.HISTORY_ID);
        return loadHistoryFromCursor(cursor);
    }

    private ArrayList<History> loadHistoryFromCursor(Cursor cursor) {
        ArrayList<History> histories = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.HISTORY_ID));
                String uuid = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_USER_UUID));
                String type = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_TYPE));
                String file = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_FILE));
                boolean isUploaded = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.HISTORY_IS_UPLOADED)) == 1;
                String createdTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_CREATE_TIME));
                int rating = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.HISTORY_RATING));
                String doctor = cursor.getString(cursor.getColumnIndex(DataBaseHelper.HISTORY_DOCTOR));
                histories.add(new History(id, uuid, type, file, isUploaded, createdTime, rating, doctor));
            }
            cursor.close();
        }
        return histories;
    }

    private ArrayList<History> getHistoriesByIds(ArrayList<Long> ids) {
        String idString = TextUtils.join(",", ids);
        String QueryString = "";//String.format("SELECT * FROM " + DataBaseHelper.HISTORY_TABLE_NAME + " WHERE " + DataBaseHelper.HISTORY_ID + " IN (%s)", new String[]{String.valueOf(idString)});
        Log.d("HistoryProvider", "QueryString" + QueryString);
        Cursor cursor = mDatabase.rawQuery(QueryString, null);

        return loadHistoryFromCursor(cursor);
    }

    public History getHistory(Long id) {
        Cursor cursor = mDatabase.query(DataBaseHelper.HISTORY_TABLE_NAME, mHistoryColumns, "_id =" + id, null, null, null, null);
        return loadHistoryFromCursor(cursor).get(0);
    }

    private User getUser(ArrayList<User> users, History history) {
        for (int j = 0; j < users.size(); j++) {
            if (history.uuid.equals(users.get(j).uuid)) {
                return users.get(j);
            }
        }
        return null;
    }

    private History getHistory(ArrayList<History> histories, ContentValues item) {
        long id = (long) item.get("id");
        for (History history : histories) {
            if (history.id == id) {
                return history;
            }
        }
        return null;
    }

    /*public ArrayList<Call> uploadHistories(ArrayList<ContentValues> items) {
        ArrayList<Long> ids = getIds(items);
        ArrayList<History> histories = getHistoriesByIds(ids);
        ArrayList<String> uuids = getUuids(histories);
        ArrayList<User> users = userProvider.getUsersByUuids(uuids);
        callList = new ArrayList<>();
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
            Log.d("GetUploadRequest", "Filepath: " + history.filePath + "; User name: " + user.name + ";");
            String url = "http://" + baseUrl + "/cana-api/upload";
            File file = new File(history.filePath);
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", file.getName(),
                            RequestBody.create(MediaType.parse("text/plain"), file))
                    .addFormDataPart("uuid", user.uuid)
                    .addFormDataPart("name", user.name)
                    .addFormDataPart("gender", String.valueOf(user.gender))
                    .addFormDataPart("age", String.valueOf(user.age))
                    .addFormDataPart("date", history.createdTime)
                    .addFormDataPart("type", history.type)
                    .addFormDataPart("rating", String.valueOf(history.rating))
                    .addFormDataPart("doctor", history.doctor)
                    .build();
            Request request = new Request.Builder().url(url).post(formBody).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callList.remove(call);
                    Log.e("Upload Failed", e.toString());
                    EventBus.getDefault().post(new ResponseEvent(false, history.id, total, groupPosition, childPosition));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callList.remove(call);
                    boolean result = false;
                    String jsonData = response.body().string();
                    try {
                        // TODO: Change to Gson
                        Log.d("Respond", jsonData);
                        JSONObject Jobject = new JSONObject(jsonData);
                        Log.d("toJson", Jobject.toString());
                        updateHistoryUploadedById(history.id);
                        result = true;
                    }catch (JSONException e) {
                        Log.e("toJson", e.toString());
                    }
                    EventBus.getDefault().post(new ResponseEvent(result, history.id, total, groupPosition, childPosition));
                }
            });
            callList.add(call);
        }
        return  callList;
    } */

    public void uploadHistories(ArrayList<ContentValues> items) {
        ArrayList<Long> ids = getIds(items);
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
           /* if (user == null) {
                EventBus.getDefault().post(new ResponseEvent(false, history.id, total, groupPosition, childPosition));
                continue;
            }*/
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
                  //  EventBus.getDefault().post(new ResponseEvent(result, history.id, total, groupPosition, childPosition));
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    //EventBus.getDefault().post(new ResponseEvent(false, history.id, total, groupPosition, childPosition));
                 //   EventBus.getDefault().post(new ResponseEvent(true, history.id, total, groupPosition, childPosition));
                }
            }, null);
        }
        // return callList;
    }

    private void updateHistoryUploadedById(Long id) {
        ContentValues args = new ContentValues();
        args.put(DataBaseHelper.HISTORY_IS_UPLOADED, 1);
        mDatabase.update(DataBaseHelper.HISTORY_TABLE_NAME, args, "_id=" + id, null);
    }

    public void deleteHistories(ArrayList<ContentValues> items) {
        ArrayList<Long> ids = getIds(items);

        String idString = TextUtils.join(",", ids);
        String QueryString = "";//String.format("DELETE FROM " + DataBaseHelper.HISTORY_TABLE_NAME + " WHERE " + DataBaseHelper.HISTORY_ID + " IN (%s)", new String[]{String.valueOf(idString)});

        mDatabase.execSQL(QueryString);
    }

    private ArrayList<Long> getIds(ArrayList<ContentValues> items) {
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
    }

}
