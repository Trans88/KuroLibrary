package com.trs88.kurolibrary.restful.annotation

import java.lang.annotation.RetentionPolicy

/**
 * @Headers({"connection:keep-alive","auth-token:token"})
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Headers (vararg val value:String)