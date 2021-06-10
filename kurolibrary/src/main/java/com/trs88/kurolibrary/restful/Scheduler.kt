package com.trs88.kurolibrary.restful

import com.trs88.kurolibrary.cache.KuroStorage
import com.trs88.kurolibrary.execute.KuroExecutor
import com.trs88.kurolibrary.log.KuroLog
import com.trs88.kurolibrary.restful.annotation.CacheStrategy
import com.trs88.kurolibrary.util.MainHandler

/**
 * 代理CallFactory创建出来的call对象，从而实现拦截器的派发动作
 */
class Scheduler(
    private val callFactory: KuroCall.Factory,
    private val interceptors: MutableList<KuroInterceptor>
) {
    fun newCall(request: KuroRequest): KuroCall<*> {
        val newCall = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }

    internal inner class ProxyCall<T>(
        private val delegate: KuroCall<T>,
        private val request: KuroRequest
    ) : KuroCall<T> {
        override fun execute(): KuroResponse<T> {
            dispatchInterceptor(request, null)

            if (request.cacheStrategy ==CacheStrategy.CACHE_FIRST){
                val cacheResponse =readCache<T>(request)
                if (cacheResponse.data!=null){
                    //抛到主线程中
                    return cacheResponse
                }
            }

            val response = delegate.execute()

            saveCacheIfNeed(response)

            dispatchInterceptor(request, response)

            return response
        }

        override fun enqueue(callback: KuroCallback<T>) {
            dispatchInterceptor(request, null)
            if (request.cacheStrategy ==CacheStrategy.CACHE_FIRST){
                KuroExecutor.execute(runnable = Runnable {
                    val cacheResponse =readCache<T>(request)
                    if (cacheResponse.data!=null){
//                        KuroLog.d("enqueue,cache data:${cacheResponse.data}")
                        //抛到主线程中
                        MainHandler.sendAtFrontOfQueue(runnable = Runnable {
                            callback.onSuccess(cacheResponse)
                        })
                        KuroLog.d("enqueue,cache:${request.getCacheKey()}")
                    }
                })
            }
            val response = delegate.enqueue(object : KuroCallback<T> {
                override fun onSuccess(response: KuroResponse<T>) {
                    dispatchInterceptor(request, response)
                    saveCacheIfNeed(response)
                    callback.onSuccess(response)
                }

                override fun onFailed(throwable: Throwable) {
                    callback.onFailed(throwable)
                }

            })

            return response
        }

        private fun saveCacheIfNeed(response: KuroResponse<T>) {
            if (request.cacheStrategy ==CacheStrategy.CACHE_FIRST||request.cacheStrategy ==CacheStrategy.NET_CACHE){
                if (response.data!=null){
                    KuroExecutor.execute(runnable = Runnable {
                        KuroStorage.saveCache(request.getCacheKey(),response.data)
                    })
                }
            }
        }

        private fun<T> readCache(request: KuroRequest): KuroResponse<T> {
            //kurostorage 查询缓存 需要提供cache key
            val cacheKey =request.getCacheKey()
            val cache=KuroStorage.getCache<T>(cacheKey)
            val cacheResponse =KuroResponse<T>()
            KuroLog.d("readCache:$cacheKey,data:$cache")
            cacheResponse.data =cache
            cacheResponse.code =KuroResponse.CACHE_SUCCESS
            cacheResponse.msg ="缓存获取成功"
            return cacheResponse
        }

        private fun dispatchInterceptor(request: KuroRequest, response: KuroResponse<T>?) {
            InterceptorChain(request, response).dispatch()
        }


        internal inner class InterceptorChain(
            private val request: KuroRequest,
            private val response: KuroResponse<T>?
        ) : KuroInterceptor.Chain {
            //分发的第几个拦截器
            var callIndex: Int = 0

            override val isRequestPeriod: Boolean
                get() = response == null

            override fun request(): KuroRequest {
                return request
            }

            override fun response(): KuroResponse<*>? {
                return response
            }

            fun dispatch() {
                if(interceptors.isEmpty()){
                    return
                }

                val interceptor = interceptors[callIndex]
                val intercept = interceptor.intercept(this)
                callIndex++
                if (!intercept && callIndex < interceptors.size) {
                    dispatch()
                }
            }

        }

    }

}