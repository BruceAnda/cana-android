package cn.ac.ict.cana

import android.app.Application

import com.pgyersdk.crash.PgyCrashManager

import cn.ac.ict.cana.common.CanaUIIntent
import cn.ac.ict.canalib.common.ParkinsDataCollection
import com.lovearthstudio.duasdk.upload.UploadUtils


/**
 * Created by zhaoliang on 2017/6/22.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        UploadUtils.initOSS()

        PgyCrashManager.register(this)

        ParkinsDataCollection.init(this, CanaUIIntent())
    }
}
