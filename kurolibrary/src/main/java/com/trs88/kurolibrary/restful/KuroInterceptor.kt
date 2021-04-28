package com.trs88.kurolibrary.restful

interface KuroInterceptor {
    fun intercept(chain: Chain): Boolean

    /**
     * Chain 对象会在派发拦截器时候创建
     */
    interface Chain {
        val isRequestPeriod:Boolean get() = false
        
        fun request(): KuroRequest

        /**
         * 这个response对象在网络发起之前，是为空的
         */
        fun response(): KuroResponse<*>?
    }
}