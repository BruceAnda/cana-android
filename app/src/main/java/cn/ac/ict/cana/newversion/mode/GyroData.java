package cn.ac.ict.cana.newversion.mode;

/**
 * 陀螺仪数据
 * Created by zhaoliang on 2017/7/18.
 */

public class GyroData {

    public long t;
    public double x;
    public double y;
    public double z;

    public GyroData(long t, double x, double y, double z) {
        this.t = t;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
