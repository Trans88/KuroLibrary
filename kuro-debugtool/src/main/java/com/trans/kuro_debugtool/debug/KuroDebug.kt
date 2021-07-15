package com.trans.kuro_debugtool.debug
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class KuroDebug (val name:String,val desc:String)