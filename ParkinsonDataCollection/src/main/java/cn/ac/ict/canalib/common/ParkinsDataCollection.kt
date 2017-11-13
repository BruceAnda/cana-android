package cn.ac.ict.canalib.common

import android.app.Application
import cn.ac.ict.canalib.common.audio.audioManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.lovearthstudio.duasdk.Dua
import com.lovearthstudio.duasdk.upload.UploadUtils

/**
 * 帕金森的初始化类，在这里做一些初始化工作
 * Created by zhaoliang on 2017/9/27.
 */
object ParkinsDataCollection {

    lateinit var uiIntent: UIIntent

    fun init(context: Application, uiIntent: UIIntent) {
        this.uiIntent = uiIntent

        Dua.init(context)
        UploadUtils.initOSS()
        Fresco.initialize(context)
        audioManager.init(context)
    }
}