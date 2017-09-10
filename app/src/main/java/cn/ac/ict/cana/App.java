package cn.ac.ict.cana;

import android.app.Application;

import com.cengalabs.flatui.FlatUI;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lovearthstudio.duasdk.Dua;
import com.pgyersdk.crash.PgyCrashManager;

import zhaoliang.com.uploadfile.UploadUtils;

/**
 * Created by zhaoliang on 2017/6/22.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PgyCrashManager.register(this);

        Dua.init(this);
        UploadUtils.initOSS();
        Fresco.initialize(this);

        FlatUI.initDefaultValues(this);
    }
}
