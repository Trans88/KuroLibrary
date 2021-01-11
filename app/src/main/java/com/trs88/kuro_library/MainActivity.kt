package com.trs88.kuro_library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trs88.kurolibrary.tab.bottom.KuroTabBottomInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val homeInfo =KuroTabBottomInfo("首页","fonts/iconfont.ttf",getString(R.string.if_home),null,"#ff656667","#ffd44949")
        tab_bottom.setKuroTabInfo(homeInfo)
    }
}
