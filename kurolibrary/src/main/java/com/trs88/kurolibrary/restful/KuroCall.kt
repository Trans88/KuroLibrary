package com.trs88.kurolibrary.restful

import java.io.IOException

interface KuroCall<T> {
    @Throws(IOException::class)
    fun execute():KuroResponse<T>
    
    fun enqueue(callback:KuroCallback<T>)
    
    interface Factory{
        fun newCall (request:KuroRequest):KuroCall<*>
    }
}