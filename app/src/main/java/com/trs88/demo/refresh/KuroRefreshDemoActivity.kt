package com.trs88.demo.refresh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.trs88.kuro_library.R
import com.trs88.kuro_ui.refresh.KuroRefresh
import com.trs88.kuro_ui.refresh.KuroTextOverView
import kotlinx.android.synthetic.main.activity_kuro_refresh_demo.*

class KuroRefreshDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuro_refresh_demo)
        val xOverView=KuroLottieOverView(this)
        refresh_layout.setRefreshOverView(xOverView)
        refresh_layout.setRefreshListener(object :KuroRefresh.KuroRefreshListener{
            override fun enableRefresh(): Boolean {
                return true
            }

            override fun onRefresh() {
                Handler().postDelayed({refresh_layout.refreshFinished()},1000)
            }

        })
        refresh_layout.setDisableRefreshScroll(false)
    }
}
