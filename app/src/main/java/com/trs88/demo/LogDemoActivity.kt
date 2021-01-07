package com.trs88.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trs88.kuro_library.R
import com.trs88.kurolibrary.log.*
import kotlinx.android.synthetic.main.activity_log_demo.*

class LogDemoActivity : AppCompatActivity() {
    var viewPrinter:KuroViewPrinter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_demo)

        viewPrinter = KuroViewPrinter(this)

        btn_log.setOnClickListener {
            printLog()
        }
        viewPrinter!!.viewProvider.showFloatingView()
    }

    private fun printLog(){
        KuroLogManager.getInstance().addPrinter(viewPrinter)
        KuroLog.log(object :KuroLogConfig(){
            override fun includeTread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 0
            }
        },KuroLogType.E,"______","5566")
        KuroLog.i("9900","hah")
    }
}
