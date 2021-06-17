package com.trs88.kurolibrary.restful

import android.text.TextUtils
import androidx.annotation.IntDef
import com.trs88.kurolibrary.restful.annotation.CacheStrategy
import com.trs88.kurolibrary.restful.annotation.POST
import java.io.File
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.URLEncoder

open class KuroRequest {
    private var cacheStrategyKey: String=""

    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var files: MutableMap<String, File>? = null
    var domainUrl: String? = null//请求域名
    var relativeUrl: String? = null//相对路径
    var returnType: Type? = null
    var formPost: Boolean =true//是否是表单提交
    var isFile:Boolean = false//是否是文件
    var cacheStrategy: Int=CacheStrategy.NET_ONLY
    
    @IntDef(value = [METHOD.GET,METHOD.POST])
    annotation class METHOD{
        companion object{
            const val GET =0
            const val POST =1
        }
    }

    //返回请求的完整Url
    fun endPointUrl(): String {
        if (relativeUrl==null){
            throw  IllegalStateException("relative url must bot be null")
        }

        if (!relativeUrl!!.startsWith("/")){
            return domainUrl+relativeUrl
        }

        val indexOf = domainUrl!!.indexOf("/")
        return domainUrl!!.substring(0,indexOf)+relativeUrl
    }

    fun addHeader(name: String, value: String) {
        if (headers==null){
            headers = mutableMapOf()
        }
        
        headers!![name] =value
        
    }

    fun getCacheKey(): String {
        if (!TextUtils.isEmpty(cacheStrategyKey)){
            return cacheStrategyKey
        }

        val builder =StringBuilder()
        val endUrl =endPointUrl()
        builder.append(endUrl)
        if (endUrl.indexOf("?")>0||endUrl.indexOf("&")>0){
            builder.append("&")
        }else{
            builder.append("?")
        }

        if (parameters!=null){
            for ((key,value)in parameters!!){
                try {
                    val encodeValue =URLEncoder.encode(value,"UTF-8")
                    builder.append(key).append("=").append(encodeValue).append("&")
                }catch (e:Exception){

                }
            }

            builder.deleteCharAt(builder.length-1)
            cacheStrategyKey =builder.toString()
        }else{
            cacheStrategyKey =endUrl
        }

        return cacheStrategyKey
    }
}
