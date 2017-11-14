package cn.ac.ict.canalib.common

/**
 * 记忆模块数据模型
 */
class Memory(
        var type: String,   // 数据类型
        var level: Int,     // 达到的等级
        var data: ArrayList<MemoryData>     // 记忆模块数据
)

class MemoryData(
        var answer: String,
        var level: Int,
        var reply: String,
        var reply2: String
) {
    override fun toString(): String {
        return "(正确答案='$answer', 等级=$level, 第一次回答='$reply', 第二次回答='$reply2')"
    }
}

/**
 * 震颤模块数据模型
 */
class Tremor(
        var type: String,
        var data: TremorData
)

class TremorData(
        var acc: ArrayList<XYZ>,
        var gyro: ArrayList<XYZ>
)

/**
 * 声音
 */
class Sound(

)

/**
 * 站立平衡数据模型
 */
class Stand(
        var type: String,
        var data: StandData
)

class StandData(
        var acc: ArrayList<XYZ>,
        var gyro: ArrayList<XYZ>
)

/**
 * 行走平衡
 */
class Stride(
        var type: String,
        var data: StrideData
)

class StrideData(
        var acc: ArrayList<XYZ>,
        var gyro: ArrayList<XYZ>
)


/**
 * 左手敲击
 */
class Tapping(
        var type: String,
        var data: ArrayList<TappingData>
)

class TappingData(
        var btn: String,    // L/R
        var time: Long
)

/**
 * 视频
 */
class Video(

)

/**
 * 手臂下垂数据模型
 */
class ArmDroop(
        var type: String,
        var data: ArmDroopData
)

class ArmDroopData(
        var acc: ArrayList<XYZ>,
        var gyro: ArrayList<XYZ>
)

class XYZ(
        var t: Long,
        var x: Double,
        var y: Double,
        var z: Double,
        var a: Double
)