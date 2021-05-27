package com.trs88.kurolibrary.activity

import android.app.Application

/**
 * 对于组件化项目，不可能把项目实际使用的Application下沉到Base,而且各个module也不需要Application真实名字
 * 这样一次反射能获取全局的Application对象的方式相比于在Application onCreate中保存一份感觉更加的通用
 */
object AppGlobals {
    private var application:Application?=null
    fun get():Application?{
        if (application ==null){
            try {
                application=Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as Application
            }catch(ex:Exception) {
                ex.printStackTrace()
            }
        }

        return application
    }
}