package cn.ac.ict.canalib.common

/**
 * 记忆模块数据模型
 */
class Memory(
        var type: String,
        var data: ArrayList<MemoryData>
)

class MemoryData(
        var answer: String,
        var level: Int,
        var reply: String,
        var reply2: String
)

/**
 * 震颤模块数据模型
 */
class Tremor(
        var type: String,
        var data: TremorData
)

class TremorData(
        var acc: ArrayList<Acc>,
        var gyro: ArrayList<Gyro>
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
        var acc: ArrayList<Acc>,
        var gyro: ArrayList<Gyro>
)

/**
 * 行走平衡
 */
class Stride(
        var type: String,
        var data: StrideData
)

class StrideData(
        var acc: ArrayList<Acc>,
        var gyro: ArrayList<Gyro>
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
        var acc: ArrayList<Acc>,
        var gyro: ArrayList<Gyro>
)

class Acc(
        var t: Long,
        var x: Double,
        var y: Double,
        var z: Double
)

class Gyro(
        var t: Long,
        var x: Double,
        var y: Double,
        var z: Double
)