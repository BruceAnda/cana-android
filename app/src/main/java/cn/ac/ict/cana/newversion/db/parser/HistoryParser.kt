package cn.ac.ict.cana.newversion.db.parser

import cn.ac.ict.cana.newversion.db.bean.History
import org.jetbrains.anko.db.RowParser

/**
 * Created by zhaoliang on 2017/9/8.
 */
class HistoryParser<T> : RowParser<History> {
    override fun parseRow(columns: Array<Any?>): History {
        return History(columns[0].toString(), columns[1].toString(), columns[2].toString(), columns[3].toString(), columns[4].toString(), columns[5].toString())
    }
}