package com.trs88.kurolibrary.restful.annotation

import java.lang.annotation.RetentionPolicy

/**
 * @GET("/xxx/{province}")
 * fun test(@Path("province") int provinceId)
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path (val value:String)