package cn.ac.ict.cana.modules.stand;

import java.util.ArrayList;

import cn.ac.ict.cana.utils.FloatVector;

/**
 * Created by zhongxi on 2016/9/27.
 */
public class StandUtil {

    public static ArrayList<FloatVector> getVariance(ArrayList<FloatVector> vector){
        ArrayList<FloatVector> reVector = new ArrayList<>();
        float meanX = 0;
        float meanY = 0;
        float meanZ = 0;
        float variX = 0;
        float variY = 0;
        float variZ = 0;
        int size = vector.size();
        for(FloatVector vec:vector){
            meanX += vec.x;
            meanY += vec.y;
            meanZ += vec.z;
            variX += vec.x*vec.x;
            variY += vec.y*vec.y;
            variZ += vec.z*vec.z;
        }
        meanX = meanX/size;
        meanY = meanY/size;
        meanZ = meanZ/size;
        variX = variX/size - meanX*meanX;
        variY = variY/size - meanY*meanY;
        variZ = variZ/size - meanZ*meanZ;
        reVector.add(new FloatVector(meanX,meanY,meanZ));
        reVector.add(new FloatVector(variX,variY,variZ));

        return reVector;
    }

    public static float getConfusionVariance(ArrayList<FloatVector> vector){
        int size = vector.size();
        float mean = 0;
        float squreMean = 0;
        double temp;
        for(FloatVector vec:vector){
            temp = Math.sqrt(vec.x*vec.x+vec.y*vec.y+vec.z*vec.z);
            mean += temp;
            squreMean += temp*temp;
        }
        return squreMean/size - (mean/size)*(mean/size);
    }
}
