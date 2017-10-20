package cn.ac.ict.canalib.common

import android.content.Context

/**
 * UI意义图类，完成界面跳转
 * Created by zhaoliang on 2017/9/27.
 */
interface UIIntent {

    /**
     * 上传数据完成后处理任务
     */
    fun uploadFinish(context: Context)

    /**
     * 跳转到病人信息填写页面
     */
    fun toPationInfo(context: Context)

    /**
     * 注册成功跳转到测试
     */
    fun toTest(context: Context)

    /**
     * 注册成功跳转到自定义用户信息
     */
    fun toCustomProfileInfo(context: Context)

    /**
     * 登录更新用户信息成功
     */
    fun loginUpdateProfileSuccess(context: Context)

    /**
     * 登录更新用户信息失败
     */
    fun loginUpdateProfileFailer(context: Context)
}