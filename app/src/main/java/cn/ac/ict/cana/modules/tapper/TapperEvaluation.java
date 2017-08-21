package cn.ac.ict.cana.modules.tapper;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.models.History;

/**
 * Author: saukymo
 * Date: 9/28/16
 */

public class TapperEvaluation {
    public TapperEvaluation() {

    }

    static public String evaluation(History history,Context context){

        boolean isRight = false;
        ArrayList<Integer> handList = new ArrayList<>();
        ArrayList<Long> timeList = new ArrayList<>();
//        Do something here.
        try {
            FileReader reader = new FileReader(history.filePath);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            isRight = Boolean.parseBoolean(line);
            line = br.readLine();
            while(line!=null && !line.isEmpty())
            {
                if(line.startsWith("left"))
                {
                    handList.add(0);

                }else if (line.startsWith("right"))
                {
                    handList.add(1);
                }
                String[] t = line.split(":");
                timeList.add(Long.parseLong(t[1].trim()));

                line = br.readLine();

            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return context.getString(R.string.default_feedback);
    }
}
