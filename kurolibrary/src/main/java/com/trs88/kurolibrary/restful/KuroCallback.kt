package com.trs88.kurolibrary.restful

/**
 * callback 回调
 */
interface KuroCallback <T>{
    fun onSuccess(response:KuroResponse<T>)
    fun onFailed(throwable: Throwable)
}