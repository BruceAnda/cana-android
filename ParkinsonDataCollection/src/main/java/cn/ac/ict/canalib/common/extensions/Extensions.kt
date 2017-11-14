package cn.ac.ict.canalib.common.extensions

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.activities.TestingReportNewActivity

/**
 * Created by zhaoliang on 2017/9/27.
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

/**
 * 跳转到报告页面
 */
fun Context.toReport() {
    startActivity(Intent(this, TestingReportNewActivity::class.java))
}

/**
 * 跳转到评分界面
 */
fun Context.toScore(modelName: String) {
    val intent = Intent(this, ScoreActivity::class.java)
    intent.putExtra("modelName", modelName)
    startActivity(intent)
}