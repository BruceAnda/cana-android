package cn.ac.ict.cana.newversion.pagers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.adapter.HistoryAdapter
import cn.ac.ict.cana.newversion.mode.History
import kotlinx.android.synthetic.main.fragment_history_page.*

/**
 * 历史记录
 */
class HIstoryPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_history_page, container, false)
    }

    private val onListItemClickListener = object : HistoryAdapter.OnListItemClickListener {
        override fun onListItemClick(view: View, position: Int) {
            //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val datas = ArrayList<History>()
        datas.add(History())
        datas.add(History())
        datas.add(History())
        datas.add(History())
        datas.add(History())
        datas.add(History())
        datas.add(History())
        datas.add(History())
        val historyAdapter = HistoryAdapter(context, datas)
        historyAdapter.onListItemClickListener = onListItemClickListener
        historyList.adapter = historyAdapter
    }
}
