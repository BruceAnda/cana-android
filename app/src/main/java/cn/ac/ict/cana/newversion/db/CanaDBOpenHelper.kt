package cn.ac.ict.cana.newversion.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import cn.ac.ict.cana.newversion.db.bean.Batch
import cn.ac.ict.cana.newversion.db.bean.History
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
        /* // 创建病人信息表
         db.createTable(PatientInfo.TABLE_NAME, true,
                 PatientInfo.NAME to TEXT,
                 PatientInfo.AGE to TEXT,
                 PatientInfo.SEX to TEXT,
                 PatientInfo.BATCH to TEXT,
                 PatientInfo.MEDICINE to TEXT,
                 PatientInfo.OPEN to TEXT
         )*/
        // 创建历史数据表
        db.createTable(History.TABLE_NAME, true,
                History.BATCH to TEXT,
                History.USERID to TEXT,
                History.FILEPATH to TEXT,
                History.ISUPLOAD to BLOB,
                History.TYPE to TEXT,
                History.MARK to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // current do nothing
    }

}

val Context.database: CanaDBOpenHelper
    get() = CanaDBOpenHelper.getInstance(applicationContext)