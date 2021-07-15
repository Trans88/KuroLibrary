package com.trs88.kurolibrary.restful

import android.util.Log
import com.trs88.kurolibrary.log.KuroLog
import com.trs88.kurolibrary.restful.annotation.*
import java.io.File
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MethodParser(
    val baseUrl: String,
    method: Method,
    args: Array<Any>
) {
    private var domainUrl: String?=null
    private var formPost: Boolean=true
    private var httpMethod: Int=0
    private var relativeUrl: String?=null
    private var returnType: Type? =null
    private var headers:MutableMap<String,String> = mutableMapOf()
    private var parameters:MutableMap<String,String> = mutableMapOf()
    private var cacheStrategy:Int =CacheStrategy.NET_ONLY
    private var files: MutableMap<String, File> = mutableMapOf()
    private var isFile : Boolean =false

    init {
        //parse method annotations such get headers,post,baseUrl
        parseMethodAnnotations(method)

        //parse method parameters such as path,filed
        parseMethodParameters(method,args)

        //parse method genric return type
        parseMethodReturnType(method)
    }

    companion object{
        fun parse(baseUrl:String,method:Method,args:Array<Any>):MethodParser{
            return MethodParser(baseUrl,method,args)
        }
    }

    private fun parseMethodReturnType(method: Method) {
        if (method.returnType!=KuroCall::class.java){
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
        val parameterAnnotations = method.parameterAnnotations
        val equals = parameterAnnotations.size == args.size
        require(equals){
            String.format("arguments annotations count %s dont match expect count %s",parameterAnnotations.size,args.size)
        }

        for (index in args.indices) {
            val annotations = parameterAnnotations[index]
            require(annotations.size<=1){
                "filed can only has one annotation:index =$index"
            }
            
            val arg =args[index]

            require(isPrimitive(arg)){
                "Only supported 8 basic types、String or File for now ,index=$index"
            }

            val annotation =annotations[0]
            if (annotation is Filed){
                val key = annotation.value
                val value = args[index]
                if (annotation.isFile){
                    isFile = true
                    if (value is File){
                        files[key] =value
                    }else{
                        throw IllegalArgumentException("annotation isFile is true but this value not File,please check it")
                    }

                }else{
                    parameters[key] =value.toString()
                }
            }else if (annotation is Path){
                val replaceName = annotation.value
                val replacement = arg.toString()
                if (replaceName!=null && replacement!=null){
                    val newRelativeUrl = relativeUrl?.replace("{$replaceName}", replacement)
                    relativeUrl =newRelativeUrl
                }
            }else if (annotation is CacheStrategy){
                cacheStrategy = arg as Int
            }else if(annotation is Header){
                val name = annotation.value
                headers[name] =arg.toString()
            }else if (annotation is BaseUrl){
                domainUrl = arg.toString()
//                KuroLog.i("domainUrl :$domainUrl")
            }else{
                throw IllegalStateException("cannot handle parameter annotation:${annotation.javaClass.toString()}")
            }
        }
    }

    /**
     * 判断是否是基础数据类型和String类型
     */
    private fun isPrimitive(value: Any) :Boolean{
        if (value.javaClass ==String::class.java){
            return true
        }

        if (value.javaClass == File::class.java){
            return true
        }

        try {
            val field = value.javaClass.getField("TYPE")
            val clazz = field[null] as Class<*>
            if (clazz.isPrimitive){
                return true
            }
        }catch (e:IllegalAccessException){
            e.printStackTrace()
        }catch (e:NoSuchFieldException){
            e.printStackTrace()
        }
        return false
        
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
            }else if (annotation is CacheStrategy){
                cacheStrategy =annotation.value
            }else{
                throw IllegalStateException("cannot handle method annotation:"+annotation.javaClass.toString())
            }
        }

        require((httpMethod == KuroRequest.METHOD.GET) || (httpMethod == KuroRequest.METHOD.POST)){
            String.format("method %s must has one of GET,POST",method.name)
        }


        if (domainUrl ==null){
            domainUrl =baseUrl
        }
    }

    fun newRequest():KuroRequest {
        val request=KuroRequest()
        request.domainUrl =domainUrl
        request.returnType =returnType
        request.headers =headers
        request.httpMethod =httpMethod
        request.parameters =parameters
        request.relativeUrl =relativeUrl
        request.formPost =formPost
        request.isFile =isFile
        request.files =files
        request.cacheStrategy =cacheStrategy
        return request
    }
}