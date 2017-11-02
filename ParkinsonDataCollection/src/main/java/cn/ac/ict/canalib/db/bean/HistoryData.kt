package cn.ac.ict.canalib.db.bean

/**
 * 检测历史数据表
 * Created by zhaoliang on 2017/9/7.
 */
class HistoryData(var batch: String, var userID: String, var filePath: String, var isUpload: String, var type: String, var mark: String, var other: String) {

    companion object {

        val TABLE_NAME = "HistoryData"

        val BATCH = "batch"
        val USERID = "UserId"
        val TYPE = "Type"
        val FILEPATH = "FilePath"
        val MARK = "Mark"
        val ISUPLOAD = "IsUpload"
        val OTHER = "Other"
    }

    override fun toString(): String {
        return "HistoryData(batch='$batch', userID='$userID', filePath='$filePath', isUpload='$isUpload', type='$type', mark='$mark', other='$other')"
    }
}