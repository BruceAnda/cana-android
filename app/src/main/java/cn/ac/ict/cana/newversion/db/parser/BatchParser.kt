package cn.ac.ict.cana.newversion.db.parser

import cn.ac.ict.cana.newversion.db.bean.Batch
import org.jetbrains.anko.db.RowParser

/**
 * Created by zhaoliang on 2017/9/10.
 */
class BatchParser<T> : RowParser<Batch> {
    override fun parseRow(columns: Array<Any?>): Batch {
        return Batch(columns[3].toString(), columns[2].toString().toLong(), columns[1].toString())
    }
}