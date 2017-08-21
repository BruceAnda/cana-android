package cn.ac.ict.cana.newversion.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

/**
 * Created by zhaoliang on 2017/7/18.
 */
class CanaDBOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "cana_new.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}