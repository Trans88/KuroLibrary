package com.trs88.kurolibrary.restful

import androidx.lifecycle.LiveData
import java.lang.reflect.Type

interface KuroConvert {
    fun <T> convert(rawData: String, dataType: Type): KuroResponse<T>
}