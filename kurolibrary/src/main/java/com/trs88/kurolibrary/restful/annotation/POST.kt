package com.trs88.kurolibrary.restful.annotation

import java.lang.annotation.RetentionPolicy

/**
 * @POST("/xxxx/xxxx")
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class POST (val value:String,val formPost:Boolean =true)