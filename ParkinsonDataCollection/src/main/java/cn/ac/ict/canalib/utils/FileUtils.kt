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

    var batch = ""

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
