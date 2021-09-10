package com.trs88.kuro_library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trs88.kuro_ui.kuroItem.KuroAdapter
import com.trs88.kuro_ui.kuroItem.KuroDateItem
import com.trs88.kuro_ui.kuroItem.KuroViewHolder
import com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo
import com.trs88.kurolibrary.log.KuroLog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val homeInfo = com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo(
//            "首页",
//            "fonts/iconfont.ttf",
//            getString(R.string.if_home),
//            null,
//            "#ff656667",
//            "#ffd44949"
//        )
//        tab_bottom.setKuroTabInfo(homeInfo)

        val kuroAdapter=KuroAdapter(this)

        rv_test.adapter =kuroAdapter
        rv_test.layoutManager =LinearLayoutManager(this)

        val dateSets = ArrayList<KuroDateItem<*,out RecyclerView.ViewHolder>>()

        dateSets.add(TopKuroItem())
        dateSets.add(TopKuroItem())

        kuroAdapter.addItems(dateSets,false)
    }
}
