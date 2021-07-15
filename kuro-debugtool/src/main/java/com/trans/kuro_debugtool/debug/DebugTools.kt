package com.trans.kuro_debugtool.debug

import com.trans.kuro_debugtool.BuildConfig

object DebugTools {
    fun buildVersion():String{
        return "构建版本："+BuildConfig.VERSION_NAME +"."+BuildConfig.VERSION_CODE
    }
    fun buildTime():String{
        return "构建时间："+BuildConfig.BUILD_TIME
    }

    fun buildEnvironment():String{
        if (BuildConfig.DEBUG){
            return "构建环境：测试"
        }else{
            return "构建环境：正式"
        }
    }
    @KuroDebug("一键开启Https降级","降级成http,可以使用抓包工具明文抓包")
    fun degrade2Http(){

    }

}