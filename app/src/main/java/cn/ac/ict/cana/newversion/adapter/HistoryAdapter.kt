package cn.ac.ict.cana.newversion.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.ac.ict.cana.R
import cn.ac.ict.cana.newversion.mode.History

/**
 * 历史数据Adapter
 */
class HistoryAdapter(var context: Context, var datas: ArrayList<History>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 设置点击事件
        if (onListItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onListItemClickListener.onListItemClick(holder.itemView, position)
            }
        }
        // 绑定数据
        return holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.history_item_layout, parent, false))
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: History) {

        }
    }

    lateinit var onListItemClickListener: OnListItemClickListener

    interface OnListItemClickListener {
        fun onListItemClick(view: View, position: Int)
    }
}