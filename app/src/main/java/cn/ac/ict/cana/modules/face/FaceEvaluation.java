package cn.ac.ict.cana.modules.face;

import android.content.Context;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.models.History;

/**
 * Author: saukymo
 * Date: 9/28/16
 */

public class FaceEvaluation {
    public FaceEvaluation() {

    }

    static public String evaluation(History history,Context context){

//        Do something here.

        return context.getString(R.string.default_feedback);
    }
}
