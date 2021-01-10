package com.trs88

import android.app.Application
import com.google.gson.Gson
import com.trs88.kurolibrary.log.*

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        KuroLogManager.init(object :KuroLogConfig(){
            override fun injectJsonParser(): JsonParser {
                return JsonParser {
                    Gson().toJson(it)
                }
            }

            override fun getGlobalTag(): String {
                return "MyApplication"
            }

            override fun enable(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 1
            }

            override fun printLogLevel(): Int {
                return KuroLogType.V
            }
        },KuroConsolePrinter(),KuroFileLogPrinter.getInstance(applicationContext.cacheDir.absolutePath, 0))
    }
}