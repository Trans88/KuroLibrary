package com.trs88.kurolibrary.ext

import com.trs88.asproj.alotservice.ext.Preference
import com.trs88.kurolibrary.activity.AppGlobals
import kotlin.reflect.jvm.jvmName

inline fun <reified R, T> R.pref(default: T) = Preference(AppGlobals.get()!!, "", default, R::class.jvmName)