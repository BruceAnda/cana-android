package cn.ac.ict.cana.common

import android.content.Context
import android.content.Intent
import cn.ac.ict.cana.features.activities.MainActivity
import cn.ac.ict.canalib.common.UIIntent

/**
 * Created by zhaoliang on 2017/9/27.
 */
class CanaUIIntent : UIIntent {

    override fun toPationInfo(context: Context) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uploadFinish(context: Context) {
      //  toMain(context)
    }

    override fun toTest(context: Context) {
        toMain(context)
    }

    override fun toCustomProfileInfo(context: Context) {
        toMain(context, 1)
    }

    override fun loginUpdateProfileFailer(context: Context) {
        toMain(context)
    }

    override fun loginUpdateProfileSuccess(context: Context) {
        toMain(context)
    }

    /**
     * 跳转到程序的主界面
     */
    private fun toMain(context: Context, page: Int = 0) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("page", page)
        context.startActivity(intent)
    }
}