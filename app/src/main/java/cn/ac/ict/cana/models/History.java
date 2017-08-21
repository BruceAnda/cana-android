package cn.ac.ict.cana.models;

import android.content.Context;
import android.util.Log;

import static java.util.UUID.randomUUID;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class History {
    public long id;
    public final String uuid;
    public final String type;
    public String filePath;
    public boolean isUpload;
    public String createdTime;
    public String ext;
    public int rating;
    public String doctor;

    public History(String uuid, String historyType, String filePath, int rating, String doctor) {
        this.uuid = uuid;
        this.isUpload = false;
        this.filePath = filePath;
        this.type = historyType;
        this.rating = rating;
        this.doctor = doctor;
    }

    public static String getFilePath(Context context, String type) {
        String ext;
        switch (type) {
            case "Face":
                ext = ".mp4";
                break;
            case "Sound":
                ext = ".3gp";
                break;
            default:
                ext = ".txt";
        }
        String filePath = context.getFilesDir().getAbsolutePath() + "/" + randomUUID().toString() + ext;
        Log.i("filepath", filePath);
        return filePath;
    }

    public History(long historyId, String uuid, String historyType, String historyFilePath, boolean historyIsUpload, String historyCreatedTime, int rating, String doctor) {
        id = historyId;
        this.uuid = uuid;
        type = historyType;
        filePath = historyFilePath;
        isUpload = historyIsUpload;
        createdTime = historyCreatedTime;
        this.rating = rating;
        this.doctor = doctor;
    }

}
