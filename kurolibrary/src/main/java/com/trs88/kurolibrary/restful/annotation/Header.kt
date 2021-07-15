package com.trs88.kurolibrary.restful.annotation

import java.lang.annotation.RetentionPolicy

/**
 * @Headers({"connection"}) 
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Header (val value:String)