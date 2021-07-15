package com.trs88.kurolibrary.file

import com.trs88.kurolibrary.execute.KuroExecutor
import com.trs88.kurolibrary.log.KuroLog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*

object KuroFileUtil {
    /**
     * 通过表单上传文件到指定的Url
     */
    fun postFileToUrl(file:File,url:String,headers:Map<String,String>,callback: PostFileCallBack){
        KuroExecutor.execute(Runnable {
            val okHttpClient = OkHttpClient()
            val parse = ("application/json;charset=UTF-8").toMediaTypeOrNull()
            val requestBody = file.asRequestBody(parse)

            val body =ProgressBody(requestBody,object:ProgressListener{
                override fun onProgress(cur: Long, total: Long,progress:Int) {
                    callback.onProgress(cur,total,progress)
                }
            })

            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("file", file.name, body)

            val requestBuilder = Request.Builder()
                .url(url) //要访问的链接
                .post(builder.build())

            for (header in headers) {
                requestBuilder.addHeader(header.key,header.value)
            }



            val request = requestBuilder.build()

            val call = okHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(call,e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onResponse(call,response)
                }
            })
        })
    }

    /**
     * 复制旧文件到新的地址
     */
    fun copyFile(src: File, des: File): Boolean {
        if (!src.exists()) {
            KuroLog.e("copyFile not exist:" + src.absolutePath)
            return false
        }
        if (!des.parentFile.isDirectory && !des.parentFile.mkdirs()) {
            KuroLog.e("copyFile mkdir failed:" + des.parent)
            return false
        }
        KuroLog.i("开始复制文件")
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(src))
            bos = BufferedOutputStream(FileOutputStream(des))
            val buffer = ByteArray(4 * 1024)
            var count: Int
            while (bis.read(buffer, 0, buffer.size).also { count = it } != -1) {
                if (count > 0) {
                    bos.write(buffer, 0, count)
                }
            }
            bos.flush()
            return true
        } catch (e: Exception) {
            KuroLog.e("copyFile exception:", e)
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

}