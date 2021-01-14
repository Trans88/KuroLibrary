package com.trs88.demo.tab

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.trs88.kuro_library.R
import com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo
import com.trs88.kuro_ui.tab.bottom.KuroTabBottomLayout
import com.trs88.kurolibrary.util.KuroDisplayUtil
import kotlinx.android.synthetic.main.activity_kuro_tab_bottom_demo.*

class KuroTabBottomDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuro_tab_bottom_demo)

        initTabBottom();
    }

    private fun initTabBottom() {
        kuro_tab_layout.setTabAlpha(0.85f)
        val bottomInfoList: MutableList<com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo<*>> = ArrayList()
        val homeInfo = com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo(
            "首页",
            "fonts/iconfont.ttf",
            getString(R.string.if_home),
            null,
            "#ff656667",
            "#ffd44949"
        )
        val infoRecommend = com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo(
            "收藏",
            "fonts/iconfont.ttf",
            getString(R.string.if_favorite),
            null,
            "#ff656667",
            "#ffd44949"
        )

//        val infoCategory = KuroTabBottomInfo(
//            "分类",
//            "fonts/iconfont.ttf",
//            getString(R.string.if_category),
//            null,
//            "#ff656667",
//            "#ffd44949"
//        )

        val bitmap =BitmapFactory.decodeResource(resources,R.drawable.fire,null)

        val infoCategory =
            com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo<String>(
                "分类",
                bitmap,
                bitmap
            )

        val infoChat = com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo(
            "推荐",
            "fonts/iconfont.ttf",
            getString(R.string.if_recommend),
            null,
            "#ff656667",
//            0xff004949,
            "#ffd44949"
        )

        val infoProfile = com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo(
            "我的",
            "fonts/iconfont.ttf",
            getString(R.string.if_profile),
            null,
            "#ff656667",
            "#ffd44949"
        )

        bottomInfoList.add(homeInfo)
        bottomInfoList.add(infoRecommend)
        bottomInfoList.add(infoCategory)
        bottomInfoList.add(infoChat)
        bottomInfoList.add(infoProfile)
        kuro_tab_layout.inflateInfo(bottomInfoList)
        kuro_tab_layout.addTabSelectedChangeListener { _, _, nextinfo ->
            Toast.makeText(this@KuroTabBottomDemoActivity, nextinfo.name, Toast.LENGTH_SHORT).show()
        }
        kuro_tab_layout.defaultSelected(homeInfo)

        val tabBottom = kuro_tab_layout.findTab(bottomInfoList[2])
        tabBottom?.apply {
            resetHeight(KuroDisplayUtil.dp2px(66f,resources))
        }
    }
}
