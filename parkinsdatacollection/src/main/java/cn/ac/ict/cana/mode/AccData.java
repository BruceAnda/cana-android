package cn.ac.ict.cana.mode;

/**
 * 加速度数据
 * Created by zhaoliang on 2017/7/18.
 */

public class AccData {

    public long t;
    public double x;
    public double y;
    public double z;

    public AccData(long t, double x, double y, double z) {
        this.t = t;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
