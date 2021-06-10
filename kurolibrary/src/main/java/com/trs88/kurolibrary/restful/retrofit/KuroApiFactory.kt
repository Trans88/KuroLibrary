package com.trs88.kurolibrary.restful.retrofit

import cn.trans88.taxiappkotlin.net.RetrofitCallFactory
import com.trs88.kurolibrary.restful.KuroInterceptor
import com.trs88.kurolibrary.restful.KuroRestful

object KuroApiFactory{
    private var kuroRestful:KuroRestful ? = null

    fun init(kuroRestful: KuroRestful){
        this.kuroRestful =kuroRestful
    }

    fun initRetrofit(baseUrl:String):KuroApiFactory{
        this.kuroRestful= KuroRestful(baseUrl,RetrofitCallFactory(baseUrl))
        return this
    }

    fun setInterceptor(interceptor: KuroInterceptor):KuroApiFactory{
        kuroRestful?.addInterceptor(interceptor)?:throw IllegalArgumentException("setInterceptor error KuroRestful is null,please first init KuroApiFactory")
        return this
    }

    fun <T> create(service:Class<T>):T{
        return kuroRestful?.create(service)?:throw IllegalArgumentException("create KuroApiFactory error KuroRestful is null,please first init KuroApiFactory")
    }
}