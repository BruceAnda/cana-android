package cn.ac.ict.cana.newversion.bean

import cn.ac.ict.cana.newversion.mode.AccData
import cn.ac.ict.cana.newversion.mode.GyroData

/**
 * 加速度和陀螺仪数据
 * Created by zhaoliang on 2017/8/28.
 */
data class AccAndGyroData(var acc: List<AccData>, var gyro: List<GyroData>)