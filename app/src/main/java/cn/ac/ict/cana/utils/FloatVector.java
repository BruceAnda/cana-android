package cn.ac.ict.cana.utils;

import java.io.Serializable;

/**
 * Author: saukymo
 * Date: 9/22/16
 */
public class FloatVector implements Serializable {
    public long timeStamp;
    public float x;
    public float y;
    public float z;

    public FloatVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = System.currentTimeMillis();
    }
    public FloatVector(){}

}


