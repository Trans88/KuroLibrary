package com.trs88.kurolibrary.restful

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

open class KuroRestful(val baseUrl: String, val callFactory: KuroCall.Factory) {
    private var interceptors: MutableList<KuroInterceptor> = mutableListOf()
    private var methodService: ConcurrentHashMap<Method, MethodParser> =
        ConcurrentHashMap()//使用ConcurrentHashMap防止并发的情况发生
    private var scheduler: Scheduler

    init {
        scheduler = Scheduler(callFactory, interceptors)
    }
    

    fun addInterceptor(interceptor: KuroInterceptor) {
        interceptors.add(interceptor)
    }


    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { _, method, args ->
            var methodParser = methodService[method]
            if (methodParser == null) {
                methodParser = MethodParser.parse(baseUrl, method, args)
                methodService[method] = methodParser
            }
            val parse = MethodParser.parse(baseUrl, method, args)

            val request = methodParser.newRequest()
            
            scheduler.newCall(request)
        } as T
    }
}