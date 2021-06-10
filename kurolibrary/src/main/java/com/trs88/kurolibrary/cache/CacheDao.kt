package com.trs88.kurolibrary.cache

import androidx.room.*

@Dao
interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCache(cache: Cache)

    @Query("select * from cache where cacheKey=:key")
    fun getCache(key:String):Cache?

    @Delete()
    fun deleteCache(cache:Cache)
}