package cn.ac.ict.cana.newversion.db.bean

/**
 * 检测历史数据表
 * Created by zhaoliang on 2017/9/7.
 */
class History(var Batch: String, var userID: String, var filePath: String, var isUpload: String, var type: String, var mark: String) {

    companion object {

        val TABLE_NAME = "History"

        val BATCH = "Batch"
        val USERID = "UserId"
        val TYPE = "Type"
        val FILEPATH = "FilePath"
        val MARK = "Mark"
        val ISUPLOAD = "IsUpload"
    }
}