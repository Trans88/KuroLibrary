package com.trs88.kuro_library

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trs88.kuro_ui.kuroItem.KuroDateItem
import com.trs88.kuro_ui.kuroItem.KuroViewHolder
import com.trs88.kurolibrary.log.KuroLog

class TopKuroItem:KuroDateItem<String,KuroViewHolder>(null) {
    override fun getItemLayoutRes(): Int {
        return R.layout.item_test
    }

//    override fun onBindData(holder: KuroViewHolder, position: Int) {
//        KuroLog.i("id :${holder.itemView} ")
//        val te = holder.itemView.findViewById<TextView>(R.id.tv_test_1)
//        KuroLog.i("textView:$te")
//        te.setText("positon:$position")
//    }

    override fun <VH : RecyclerView.ViewHolder> onBindData(holder: VH, position: Int) {
        KuroLog.i("id :${holder.itemView} ")
        val te = holder.itemView.findViewById<TextView>(R.id.tv_test_1)
        KuroLog.i("textView:$te")
        te.setText("positon:$position")
    }


//    override fun onBindData(holder: KuroViewHolder, position: Int) {
//        super.onBindData(holder, position)
//
//        KuroLog.i("id :${holder.itemView} ")
//        val te = holder.itemView.findViewById<TextView>(R.id.tv_test_1)
//        KuroLog.i("textView:$te")
//        te.setText("positon:$position")
//    }


}