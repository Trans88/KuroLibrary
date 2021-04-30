package com.trs88.kurolibrary.restful

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
            val response = delegate.execute()
            dispatchInterceptor(request, response)

            return response
        }

        override fun enqueue(callback: KuroCallback<T>) {
            dispatchInterceptor(request, null)
            val response = delegate.enqueue(object : KuroCallback<T> {
                override fun onSuccess(response: KuroResponse<T>) {
                    dispatchInterceptor(request, response)
                    if (callback != null) {
                        callback.onSuccess(response)
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    if (callback != null) {
                        callback.onFailed(throwable)
                    }
                }

            })

            return response
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