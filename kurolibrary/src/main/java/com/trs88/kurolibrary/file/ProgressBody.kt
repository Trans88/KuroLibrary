package com.trs88.kurolibrary.file

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*


class ProgressBody(
    val requestBody: RequestBody,
    val progressListener: ProgressListener
) : RequestBody() {
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }


    override fun writeTo(sink: BufferedSink) {
        //包装完成的BufferedSink
        if (sink is Buffer
            || sink.toString().contains(
                "com.android.tools.profiler.support.network.HttpTracker\$OutputStreamTracker"
            )
        ) {
            requestBody.writeTo(sink)
        } else {
            val bufferedSink = progressSink(sink).buffer()
            requestBody.writeTo(bufferedSink)
            bufferedSink.flush()
        }
    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private fun progressSink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten = 0L
            //总字节长度，避免多次调用contentLength()方法
            var contentLength = 0L
            //最后的进度
            var lastProgress =0

            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }

                bytesWritten += byteCount

                val currentProgress = ((bytesWritten.toDouble() / contentLength.toDouble())*100).toInt()

                if (currentProgress>lastProgress){
                    lastProgress =currentProgress
                    progressListener.onProgress(bytesWritten, contentLength,currentProgress)
                }

            }
        }
    }
}