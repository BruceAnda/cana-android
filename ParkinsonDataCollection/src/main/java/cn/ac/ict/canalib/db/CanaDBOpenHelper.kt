package cn.ac.ict.canalib.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import cn.ac.ict.canalib.db.bean.Batch
import cn.ac.ict.canalib.db.bean.HistoryData
import org.jetbrains.anko.db.*

/**
 * 使用 anko 重新设计的Sqlite
 */
class CanaDBOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "cananew", null, 1) {

    companion object {
        private var instance: CanaDBOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): CanaDBOpenHelper {
            if (instance == null) {
                instance = CanaDBOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    private val TAG = CanaDBOpenHelper::class.java.simpleName

    override fun onCreate(db: SQLiteDatabase) {
        // 创建Batch表
        Log.i(TAG, "onCreate" + db.version + ":" + db.isOpen + ":" + db.isReadOnly)
        db.createTable(Batch.TABLE_NAME, true,
                Batch.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Batch.BATCH to TEXT,
                Batch.TIME to TEXT,
                Batch.PATIENT_NAME to TEXT,
                Batch.PATIENT_AGE to TEXT,
                Batch.PATIENT_SEX to TEXT,
                Batch.PATIENT_MEDICINE to TEXT,
                Batch.PATIENT_OPEN to TEXT
        )

        // 创建历史数据表
        db.createTable(HistoryData.TABLE_NAME, true,
                HistoryData.BATCH to TEXT,
                HistoryData.USERID to TEXT,
                HistoryData.FILEPATH to TEXT,
                HistoryData.ISUPLOAD to TEXT,
                HistoryData.TYPE to TEXT,
                HistoryData.MARK to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // current do nothing
    }

}

val Context.database: CanaDBOpenHelper
    get() = CanaDBOpenHelper.getInstance(applicationContext)