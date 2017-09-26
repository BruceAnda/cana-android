package cn.ac.ict.cana.db.bean

/**
 * 检测历史数据表
 * Created by zhaoliang on 2017/9/7.
 */
class History(var batch: String, var userID: String, var filePath: String, var isUpload: String, var type: String, var mark: String) {

    companion object {

        val TABLE_NAME = "History"

        val BATCH = "batch"
        val USERID = "UserId"
        val TYPE = "Type"
        val FILEPATH = "FilePath"
        val MARK = "Mark"
        val ISUPLOAD = "IsUpload"
    }
}