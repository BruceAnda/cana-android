package cn.ac.ict.canalib.utils

import cn.ac.ict.canalib.common.*
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.io.RandomAccessFile

/**
 * Created by zhaoliang on 2017/7/1.
 */

object FileUtils {

    /**
     * 重置状态
     */
    fun resetStats() {
        hasTestOne = false
        hasTestTwo = false
        hasTestThree = false
        hasTestFour = false
        hasTestFive = false
        hasTestSix = false
        hasTestSeven = false
        hasTestEight = false
        isTestingEnter = true
    }

    /**
     * 是否生成对应的每项的测试报告
     */
    var hasTestOne = false  // 是否测试第一项
    var hasTestTwo = false
    var hasTestThree = false
    var hasTestFour = false
    var hasTestFive = false
    var hasTestSix = false
    var hasTestSeven = false
    var hasTestEight = false
    var isTestingEnter = true   // 是否是从测试模块进入

    /**
     * 测试报告的特征
     */
    // 震颤特征
    var rrFrequency = "未实现"
    var rrAmplitude = "未实现"
    var lrFrequency = "未实现"
    var lrAmplitude = "未实现"
    var rpFrequency = "未实现"
    var rpAmplitude = "未实现"
    var lpFrequency = "未实现"
    var lpAmplitude = "未实现"
    // 语言特征
    var tone = "未实现"
    var volume = "未实现"
    // 平衡特征
    var rVariance = "未实现"
    var rRime = "未实现"
    var lVariance = "未实现"
    var lTime = "未实现"
    // 行走特征
    var step = "未实现"
    // tapping特征
    var rAlternatingRatio = "未实现"
    var rAvgspeed = "未实现"
    var lAlternatingRatio = "未实现"
    var lAvgspeed = "未实现"
    // 面具脸特征
    var blinkTimes = "未实现"
    var smileAngle = "未实现"
    // 手臂下垂特征
    var rArmDroopCount = "未实现"
    var lArmDroopCount = "未实现"

    var batch = ""
    var DOCTOR = ""
    var PATIENT_NAME = ""
    var PATIENT_SEX = ""
    var PATIENT_AGE = ""
    var PATIENT_MEDICINE = ""
    var SWITCHING_PERIOD = ""

    // 记忆模块数据
    lateinit var memory: Memory

    // 震颤数据
    lateinit var tremorRR: Tremor   // 右手静止震颤
    lateinit var tremorLR: Tremor   // 左手静止震颤
    lateinit var tremorRP: Tremor   // 右手运动震颤
    lateinit var tremorLP: Tremor   // 左手运动震颤
    lateinit var tremorData: TremorData

    // 站立平衡
    lateinit var standL: Stand
    lateinit var standR: Stand
    lateinit var standData: StandData

    // 行走平衡
    lateinit var stride: Stride
    lateinit var strideData: StrideData

    // 手指灵敏
    lateinit var tappingL: Tapping
    lateinit var tappingR: Tapping

    // 手臂下垂
    lateinit var armDroopL: ArmDroop
    lateinit var armDroopR: ArmDroop
    lateinit var armDroopData: ArmDroopData


    var filePath = ""
    private lateinit var randomAccessFile: RandomAccessFile

    fun writeToFile(data: String, filePath: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(filePath, true)
            fileWriter.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun open() {
        try {
            randomAccessFile = RandomAccessFile(filePath, "rwd")
            randomAccessFile!!.seek(randomAccessFile!!.length())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun close() {
        try {
            randomAccessFile!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
