package cn.ac.ict.canalib.mode;

import android.content.Context;
import android.util.Log;

import static java.util.UUID.randomUUID;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class History {
    public long id;
    public Long userid;
    public String type;
    public String filePath;
    public boolean isUpload;
    public String createdTime;
    public String mark;
    public String level;
    public String ext;

    public History() {
    }

    public History(Long userid, String historyType, String filePath) {
        this.userid = userid;
        this.isUpload = false;
        this.filePath = filePath;
        this.type = historyType;
    }

    public History(Long userid, String historyType, String filePath, String mark) {
        this.userid = userid;
        this.isUpload = false;
        this.filePath = filePath;
        this.type = historyType;
        this.mark = mark;
    }

    public static String getFilePath(Context context, String type) {
        String ext;
        switch (type) {
            case "Face":
                ext = ".mp4";
                break;
            case "Sound":
                //ext = ".3gp";
                ext = ".wav";
                break;
            default:
                ext = ".txt";
        }
        String filePath = context.getFilesDir().getAbsolutePath() + "/" + randomUUID().toString() + ext;
        Log.i("filepath", filePath);
        return filePath;
    }

    public static String getSuffix(String type) {
        String suffix;
        switch (type) {
            case "Face":
                suffix = ".mp4";
                break;
            case "Sound":
                suffix = ".3gp";
                break;
            default:
                suffix = ".txt";
        }

        return suffix;
    }


    public History(long historyId, Long userid, String historyType, String historyFilePath, boolean historyIsUpload, String historyCreatedTime, String mark) {
        id = historyId;
        this.userid = userid;
        type = historyType;
        filePath = historyFilePath;
        isUpload = historyIsUpload;
        createdTime = historyCreatedTime;
        this.mark = mark;
    }

   /* public History(long id, Long userid, String type, String filePath, boolean isUpload, String createdTime, String level) {
        this.id = id;
        this.userid = userid;
        this.type = type;
        this.filePath = filePath;
        this.isUpload = isUpload;
        this.createdTime = createdTime;
        this.level = level;
    }*/
}
