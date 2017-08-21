package cn.ac.ict.cana.newversion.modules.count;

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

public class CountEvaluation {
    public CountEvaluation() {

    }

    static public String evaluation(History history,Context context){

//        Do something here.

        boolean isRight = false;
        boolean isSound = false;
        String rightAnswer = "";
        String linet = "";
        ArrayList<String> tryInput = new ArrayList<String>();

        try{
            FileReader reader = new FileReader(history.filePath);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            linet = line;
            String[] strings = line.trim().split(";");
            rightAnswer = strings[0].trim();
            isRight = strings[1].trim().equals("0")?false:true;
            isSound = strings[2].trim().equals("0")?false:true;
            for(int i=3;i<strings.length;i++){
                tryInput.add(strings[i]);
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }

        String result = "";
        result += isSound?context.getString(R.string.count_test_version_sound):context.getString(R.string.count_test_version_picture);
        result += isRight?context.getString(R.string.countt_input_rightanswer):context.getString(R.string.countt_input_wronganswer);
        result += context.getString(R.string.countt_right_answer)+rightAnswer+"\n";
        result += context.getString(R.string.countt_try_input);
        for(String s:tryInput){
            result += s+"\n";
        }
//        return rightAnswer+":"+isRight+":"+linet;
        return result;

    }
}
