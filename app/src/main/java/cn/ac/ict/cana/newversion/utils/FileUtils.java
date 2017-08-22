package cn.ac.ict.cana.newversion.utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import cn.ac.ict.cana.newversion.mode.AccData;
import cn.ac.ict.cana.newversion.mode.CountData;
import cn.ac.ict.cana.newversion.mode.GyroData;
import cn.ac.ict.cana.newversion.mode.TapperData;

/**
 * Created by zhaoliang on 2017/7/1.
 */

public class FileUtils {

    public static String PATIENT_NAME = "";
    public static String PATIENT_SEX = "";
    public static String PATIENT_AGE = "";
    public static String PATIENT_MEDICINE = "";
    public static String SWITCHING_PERIOD = "";

    // 记忆数据
    public static List<CountData> countDataList;

    // 震颤数据
    public static List<AccData> tremor_lr_accdatalist;
    public static List<GyroData> tremor_lr_gyrodatalist;

    public static List<AccData> tremor_lp_accdatalist;
    public static List<GyroData> tremor_lp_gyrodatalist;

    public static List<AccData> tremor_rr_accdatalist;
    public static List<GyroData> tremor_rr_gyrodatalist;

    public static List<AccData> tremor_rp_accdatalist;
    public static List<GyroData> tremor_rp_gyrodatalist;

    // 站立平衡
    public static List<AccData> accLDatalist;
    public static List<GyroData> gyroLDataList;

    public static List<AccData> accRDatalist;
    public static List<GyroData> gyroRDataList;

    // 行走平衡
    public static List<AccData> accSDatalist;
    public static List<GyroData> gyroSDataList;

    // 手指灵敏L
    public static List<TapperData> tapperLDatas;
    public static List<TapperData> tapperRDatas;

    public static String batch = "";


    public static void writeToFile(String data, String filePath) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, true);
            fileWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String filePath = "";
    private static RandomAccessFile randomAccessFile;
    public static int score_lefthand_still = 0;
    public static int score_lefthand_motion = 0;
    public static int score_righthand_still = 0;
    public static int score_righthand_motion = 0;

    public static void writeData(String data) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rwd");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(data.getBytes());
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void open() {
        try {
            randomAccessFile = new RandomAccessFile(filePath, "rwd");
            randomAccessFile.seek(randomAccessFile.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String data) {
        try {
            randomAccessFile.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
