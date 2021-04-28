package com.trs88.kurolibrary.restful

import androidx.annotation.IntDef
import com.trs88.kurolibrary.restful.annotation.POST
import java.lang.reflect.Type

open class KuroRequest {
    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, Any>? = null
    var domainUrl: String? = null//请求域名
    var relativeUrl: String? = null//相对路径
    var returnType: Type? = null
    
    @IntDef(value = [METHOD.GET,METHOD.POST])
    internal annotation class METHOD{
        companion object{
            const val GET =0
            const val POST =1
        }
    }
}
