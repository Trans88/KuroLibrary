package com.trs88.kurolibrary.cache

import com.trs88.kurolibrary.log.KuroLog
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object KuroStorage {
    fun <T> saveCache(key: String, body: T) {
        val cache = Cache()
        cache.cacheKey = key
        cache.data = toByteArray(body)
//        KuroLog.d("saveCache: body: $body,cache.data:${cache.data}")
        CacheDatabase.get().cacheDao.saveCache(cache)
    }

    fun <T>getCache(key: String):T?{
        val cache=CacheDatabase.get().cacheDao.getCache(key)
//        if (cache==null){
//            KuroLog.d("getCache cache is null")
//        }
//        if (cache?.data ==null){
//            KuroLog.d("getCache data is null")
//        }
        return (if (cache?.data!=null){
            KuroLog.d("getCache data:${cache.data}")
            toObject(cache.data)
        }else{
            null
        }) as? T

    }

    fun deleteCache(key: String){
        val cache=Cache()
        cache.cacheKey=key
        CacheDatabase.get().cacheDao.deleteCache(cache)
    }




    private fun <T> toByteArray(body: T): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(body)
            oos.flush()
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            baos?.close()
            oos?.close()
        }

        return ByteArray(0)
    }

    private fun toObject(data: ByteArray?): Any? {
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bais = ByteArrayInputStream(data)
            ois = ObjectInputStream(bais)

            return ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bais?.close()
            ois?.close()
        }

        return null
    }
}