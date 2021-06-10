package cn.trans88.taxiappkotlin.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trs88.kurolibrary.log.KuroLog
import com.trs88.kurolibrary.restful.KuroConvert
import com.trs88.kurolibrary.restful.KuroResponse
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

class GsonConvert :KuroConvert{
    private var gson:Gson = Gson()

    override fun <T> convert(rawData: String, dataType: Type): KuroResponse<T> {
        val response:KuroResponse<T> =KuroResponse<T>()
        try {
            KuroLog.d("rawData:$rawData")
            val jsonObject=JSONObject(rawData)
            response.code =jsonObject.optInt("code")
            response.msg =jsonObject.optString("msg")
            val data = jsonObject.optString("data")

            if (response.code ==KuroResponse.SUCCESS){
                KuroLog.dt("convert","data:$data,dataType:$dataType")
                response.data =gson.fromJson(rawData,dataType)
            }else{
                response.errorData=gson.fromJson<MutableMap<String,String>>(data,object :TypeToken<MutableMap<String,String>>(){

                }.type)
            }
        }catch (e:JSONException) {
            e.printStackTrace()
            response.code=-1
            response.msg=e.message
        }

        response.rawData =rawData
        return response
    }

}