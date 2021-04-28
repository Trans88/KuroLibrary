package com.trs88.kurolibrary.restful

import java.lang.reflect.Proxy

open class KuroRestful(val baseUrl: String, callFactory: KuroCall.Factory) {
    private var interceptors: MutableList<KuroInterceptor> = mutableListOf()

    fun addInterceptor(interceptor: KuroInterceptor) {
        interceptors.add(interceptor)
    }

    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { proxy, method, args ->

        } as T
    }
}