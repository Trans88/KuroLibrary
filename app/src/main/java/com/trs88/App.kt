package com.trs88

import android.app.Application
import cn.trans88.taxiappkotlin.net.RetrofitCallFactory
import com.google.gson.Gson
import com.trs88.kurolibrary.log.*
import com.trs88.kurolibrary.restful.KuroRestful
import com.trs88.kurolibrary.restful.retrofit.KuroApiFactory

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        KuroApiFactory.init(KuroRestful("http://taxihub.cn:2346/v1/cms/taxi/", RetrofitCallFactory("http://taxihub.cn:2346/v1/cms/taxi/")))
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