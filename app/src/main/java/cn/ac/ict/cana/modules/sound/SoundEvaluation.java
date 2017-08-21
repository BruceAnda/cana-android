package cn.ac.ict.cana.modules.sound;

import android.content.Context;
import android.media.MediaPlayer;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.models.History;

/**
 * Author: saukymo
 * Date: 9/28/16
 */

public class SoundEvaluation {
    public SoundEvaluation() {

    }

    static public String evaluation(History history,Context context){
        String content = "";
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(history.filePath);
            content = String.valueOf(mp.getDuration());
        }catch (Exception e)
        {
            content = e.toString();
        }
        return context.getString(R.string.default_feedback);
    }
}
