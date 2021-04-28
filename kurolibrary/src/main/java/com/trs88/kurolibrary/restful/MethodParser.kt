package com.trs88.kurolibrary.restful

import android.util.Log
import com.trs88.kurolibrary.restful.annotation.BaseUrl
import com.trs88.kurolibrary.restful.annotation.GET
import com.trs88.kurolibrary.restful.annotation.Headers
import com.trs88.kurolibrary.restful.annotation.POST
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MethodParser(
    baseUrl: String,
    method: Method,
    args: Array<Any>
) {
    private var domainUrl: String?=null
    private var formPost: Boolean=true
    private var httpMethod: Int=0
    private var relativeUrl: String?=null
    private var returnType: Type? =null
    private var headers:MutableMap<String,String> = mutableMapOf()

    init {
        //parse method annotations such get headers,post,baseUrl
        parseMethodAnnotations(method)

        //parse method parameters such as path,filed
        parseMethodParameters(method,args)

        //parse method genric return type
        parseMethodReturnType(method)
    }

    private fun parseMethodReturnType(method: Method) {
        if (method.returnType!=KuroCall::class){
            throw IllegalStateException(String.format("method %s must be type of KuroCall.class",method.name))
        }
        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType){
            val actualTypeArguments = genericReturnType.actualTypeArguments
            require(actualTypeArguments.size==1){
                String.format("method %s can only has one generic return type",method.name)
            }
            returnType =actualTypeArguments[0]
        }else{
            throw IllegalStateException(String.format("method %s must has one gerneric return type",method.name))
        }
    }

    private fun parseMethodParameters(method: Method, args: Array<Any>) {

    }

    private fun parseMethodAnnotations(method: Method) {
        val annotations =method.annotations
        for (annotation in annotations) {
            if (annotation is GET){
                relativeUrl =annotation.value
                httpMethod =KuroRequest.METHOD.GET
            }else if (annotation is POST){
                relativeUrl =annotation.value
                httpMethod =KuroRequest.METHOD.POST
                formPost = annotation.formPost
            }else if (annotation is Headers){
                val headersArray = annotation.value
                for (header in headersArray) {
                    val colon = header.indexOf(":")
                    check(!(colon==0||colon ==-1)){
                        String.format("@headers value must be in the form [name:value],but found [%s]",header)
                    }

                    val name = header.substring(0,colon)
                    val value =header.substring(colon+1).trim()
                    headers[name] =value
                }
            }else if (annotation is BaseUrl){
                domainUrl = annotation.value
            }else{
                throw IllegalStateException("cannot handle method annotation:"+annotation.javaClass.toString())
            }

            require(!(httpMethod!=KuroRequest.METHOD.GET)&&!(httpMethod!=KuroRequest.METHOD.POST)){
                String.format("method %s must has one of GET,POST",method.name)
            }
        }

    }

    companion object{
        fun parse(baseUrl:String,method:Method,args:Array<Any>):MethodParser{
            return MethodParser(baseUrl,method,args)
        }
    }
}