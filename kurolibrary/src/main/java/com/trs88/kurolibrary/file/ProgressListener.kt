package com.trs88.kurolibrary.file

interface ProgressListener {
    fun onProgress(cur :Long,total:Long,progress:Int)
}