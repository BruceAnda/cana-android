package cn.ac.ict.canalib.db.parser

import cn.ac.ict.canalib.db.bean.HistoryData
import org.jetbrains.anko.db.RowParser

/**
 * Created by zhaoliang on 2017/9/8.
 */
class HistoryParser<T> : RowParser<HistoryData> {
    override fun parseRow(columns: Array<Any?>): HistoryData {
        return HistoryData(columns[0].toString(), columns[1].toString(), columns[2].toString(), columns[3].toString(), columns[4].toString(), columns[5].toString(), columns[6].toString())
    }
}