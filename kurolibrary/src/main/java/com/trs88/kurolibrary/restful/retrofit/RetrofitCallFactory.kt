package cn.trans88.taxiappkotlin.net

import android.util.Log
import com.trs88.kurolibrary.log.KuroLog
import com.trs88.kurolibrary.restful.*
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.concurrent.TimeUnit.SECONDS

class RetrofitCallFactory(val baseUrl:String):KuroCall.Factory {
    private var kuroConvert: KuroConvert
    private var apiService:ApiService
    init {
        val retrofit = Retrofit.Builder()
            .client(OkHttpHolder().okHttpClient)
            .baseUrl(baseUrl)
            .build()
        apiService = retrofit.create(ApiService::class.java)
        kuroConvert = GsonConvert()
    }

    override fun newCall(request: KuroRequest): KuroCall<Any> {
        return RetrofitCall(request)
    }

    internal inner class RetrofitCall<T>(val request: KuroRequest) :KuroCall<T>{
        override fun execute(): KuroResponse<T> {
            val realCall = createRealCall(request)
            val response = realCall.execute()
            return parseResponse(response)
        }

        override fun enqueue(callback: KuroCallback<T>) {
            val realCall = createRealCall(request)
            realCall.enqueue(object :Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback.onFailed(throwable = t)
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val response = parseResponse(response)
                    callback.onSuccess(response)
                }

            })
        }

        private fun parseResponse(response: Response<ResponseBody>): KuroResponse<T> {
            var rawData:String?= null
            if (response.isSuccessful){
                val body = response.body()
                if (body!=null){
                    rawData =body.string()
                }
            }else{
                val body = response.errorBody()
                if (body!=null){
                    rawData =body.string()
                }
            }

            return kuroConvert.convert(rawData!!,request.returnType!!)

        }

        private fun createRealCall(request: KuroRequest):Call<ResponseBody> {
            when (request.httpMethod) {
                KuroRequest.METHOD.GET -> {
                    return apiService.get(request.headers,request.endPointUrl(),request.parameters)
                }
                KuroRequest.METHOD.POST -> {
                    val parameters = request.parameters
                    val builder = FormBody.Builder()
                    var requestBody:RequestBody?=null
                    val jsonObject =JSONObject()
                    for ((key:String,value:String) in parameters!!){
                        if (request.formPost){
                            builder.add(key,value)
                        }else{
                            jsonObject.put(key,value)
                        }
                    }

                    if (request.formPost){
                        requestBody =builder.build()
                    }else{
                        requestBody =jsonObject.toString().toRequestBody("application/json;utf-8".toMediaTypeOrNull())
                    }

                    return apiService.post(request.headers,request.endPointUrl(),requestBody)
                }
                else -> {
                    throw IllegalStateException("KuroRestful only support GET or POST")
                }
            }

        }
    }

    inner class OkHttpHolder{
        private val TIME_OUT =60L
        private var builder=OkHttpClient.Builder()
        val okHttpClient:OkHttpClient

        init {
            okHttpClient =builder.connectTimeout(TIME_OUT,SECONDS).build()
        }
    }

    interface ApiService{
        @GET
        fun get(@HeaderMap headers:MutableMap<String,String>?,@Url url:String,@QueryMap(encoded = true) params:MutableMap<String ,String>?):Call<ResponseBody>

        @POST
        fun post(@HeaderMap headers:MutableMap<String,String>?,@Url url:String,@Body body:RequestBody?):Call<ResponseBody>
    }
}