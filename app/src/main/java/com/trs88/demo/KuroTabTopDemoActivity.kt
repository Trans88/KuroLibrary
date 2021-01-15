package com.trs88.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trs88.kuro_library.R
import com.trs88.kuro_ui.tab.common.IKuroTabLayout
import com.trs88.kuro_ui.tab.top.KuroTabTopInfo
import com.trs88.kuro_ui.tab.top.KuroTabTopLayout
import com.trs88.kurolibrary.log.KuroLog
import kotlinx.android.synthetic.main.activity_kuro_tab_top_demo.*

class KuroTabTopDemoActivity : AppCompatActivity() {
    val tabsStr = listOf<String>(
        "热门",
        "服装",
        "数码",
        "鞋子",
        "零食",
        "家电",
        "汽车",
        "百货",
        "家具",
        "装修",
        "运动"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuro_tab_top_demo)
        if (tabsStr.size<11){

        }else{
            for (index in 0..10){
                KuroLog.i(tabsStr[index])
            }
        }

        initTabTop()
    }

    private fun initTabTop() {
        val infoList = mutableListOf<KuroTabTopInfo<*>>()
        val defaultColor = resources.getColor(R.color.tabBottomDefaultColor)
        val tintColor = resources.getColor(R.color.tabBottomTintColor)
        for (s in tabsStr) {
            val info = KuroTabTopInfo(s, defaultColor, tintColor)
            infoList.add(info)

        }
        tab_top_layout.inflateInfo(infoList)
        tab_top_layout.addTabSelectedChangeListener(object :IKuroTabLayout.OnTabSelectedListener<KuroTabTopInfo<*>>{
            override fun OnTabSelectedChange(
                index: Int,
                prevInfo: KuroTabTopInfo<*>?,
                nextInfo: KuroTabTopInfo<*>
            ) {
                KuroLog.i(nextInfo.name)
            }

        })
        tab_top_layout.defaultSelected(infoList[0])
    }
}
