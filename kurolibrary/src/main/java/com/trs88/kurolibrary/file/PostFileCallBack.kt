package com.trs88.kurolibrary.file

import okhttp3.Call
import okhttp3.Response
import java.io.IOException

interface PostFileCallBack {
    fun onFailure(call: Call, e: IOException)
    fun onResponse(call: Call, response: Response)
    fun onProgress(cur:Long,total:Long,progress:Int)
}