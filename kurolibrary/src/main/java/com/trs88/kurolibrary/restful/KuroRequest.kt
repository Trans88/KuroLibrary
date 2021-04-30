package com.trs88.kurolibrary.restful

import androidx.annotation.IntDef
import com.trs88.kurolibrary.restful.annotation.POST
import java.lang.IllegalStateException
import java.lang.reflect.Type

open class KuroRequest {


    

    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var domainUrl: String? = null//请求域名
    var relativeUrl: String? = null//相对路径
    var returnType: Type? = null
    var formPost: Boolean =true//是否是表单提交
    
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
}
