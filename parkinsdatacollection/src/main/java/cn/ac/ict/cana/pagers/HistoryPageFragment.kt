package cn.ac.ict.cana.pagers

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.ac.ict.cana.activities.HistoryDetailActivity
import cn.ac.ict.cana.adapter.HistoryAdapter
import cn.ac.ict.cana.db.bean.Batch
import cn.ac.ict.cana.db.database
import cn.ac.ict.cana.db.parser.BatchParser
import cn.ac.ict.cana.parkionsdatacollection.R
import kotlinx.android.synthetic.main.fragment_history_page.*
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.select

/**
 * 历史记录
 */
class HistoryPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_history_page, container, false)
    }

    private val onListItemClickListener = object : HistoryAdapter.OnListItemClickListener {

        override fun onListItemClick(view: View, position: Int) {
            val intent = Intent(context, HistoryDetailActivity::class.java)
            intent.putExtra("batch", list[position])
            startActivity(intent)
        }
    }

    private val REQUST_HISTORY_DETAL = 10009
    lateinit var list: List<Batch>
    private val TAG = HistoryPageFragment::class.java.simpleName

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()
        context.database.use {
            list = select(Batch.TABLE_NAME).orderBy(Batch.TIME, SqlOrderDirection.DESC).parseList(BatchParser<Batch>())
            val historyAdapter = HistoryAdapter(context, list)
            historyAdapter.onListItemClickListener = onListItemClickListener
            historyList.adapter = historyAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUST_HISTORY_DETAL) {
            Log.i(TAG, "历史操作完成！")
        }
    }
}
