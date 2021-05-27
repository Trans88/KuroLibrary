package com.trs88.kurolibrary.restful

/**
 * 响应报文
 */
open class KuroResponse <T>{
    companion object{
        const val SUCCESS:Int =0
        const val CACHE_SUCCESS:Int =304 //请求缓存成功
    }

    var rawData:String? =null//原始数据
    var code =0//业务状态码 0成功 非0失败
    var data:T? =null //业务数据
    var errorData:Map<String,String>? =null//错误状态下的数据

    var msg :String ? =null//错误信息
}