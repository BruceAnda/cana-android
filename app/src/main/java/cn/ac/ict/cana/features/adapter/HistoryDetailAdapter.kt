package cn.ac.ict.cana.features.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.R
import kotlinx.android.synthetic.main.history_detail_list.view.*

/**
 * Created by zhaoliang on 2017/9/10.
 */
class HistoryDetailAdapter(var context: Context, var datas: List<HistoryData>, var titles: HashMap<String, String>) : RecyclerView.Adapter<HistoryDetailAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 设置点击事件
        /* if (onListItemClickListener != null) {
             holder.itemView.setOnClickListener {
                 onListItemClickListener.onListItemClick(holder.itemView, position)
             }
         }*/
        // 绑定数据
        return holder.bind(datas[position], titles)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.history_detail_list, parent, false))
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: HistoryData, titles: HashMap<String, String>) {
            itemView.tv_test_title.text = titles[data.type]
            if (data.isUpload.contains("1")) {
                itemView.iv_text_icon.setImageResource(R.drawable.is_upload_new)
            } else {
                itemView.iv_text_icon.setImageResource(R.drawable.un_upload_new)
            }
        }
    }

    /* lateinit var onListItemClickListener: OnListItemClickListener

     interface OnListItemClickListener {
         fun onListItemClick(view: View, position: Int)
     }*/
}