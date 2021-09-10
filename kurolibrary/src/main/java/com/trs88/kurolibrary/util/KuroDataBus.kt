package com.trs88.kurolibrary.util

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap


object KuroDataBus {
    private val eventMap =ConcurrentHashMap<String,StickyLiveData<*>>()

    fun <T>with(eventName:String):StickyLiveData<T>{
        //基于事件名称 订阅、分发消息，由于一个livedata 只能发送一种数据类型
        //所以 不同的event事件，需要使用不同的livedata实例去分发
        var liveData = eventMap[eventName]


        if(liveData ==null){
            liveData = StickyLiveData<T>(eventName)
            eventMap[eventName] =liveData
        }

        return liveData as StickyLiveData<T>
    }

    class StickyLiveData<T> (private val eventName:String):LiveData<T>(){
        private var mStickyData:T?= null
        private var mVersion =0

        fun setStickyData(stickyData:T){
            mStickyData =stickyData
            setValue(stickyData)
        }

        fun postStickyData(stickyData:T){
            mStickyData =stickyData
            postValue(stickyData)
        }

        override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        override fun postValue(value: T) {
            mVersion++
            super.postValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observerSticky(owner,false,observer)
        }

        fun observerSticky(owner: LifecycleOwner,sticky:Boolean,observer: Observer<in T>){
            //允许指定注册的观察者，是否需要接受粘性事件
            owner.lifecycle.addObserver(LifecycleEventObserver{source, event ->
                //监听宿主发送销毁事件主动把livedata移除掉
                if (event==Lifecycle.Event.ON_DESTROY){
                    eventMap.remove(eventName)
                }
            })

            super.observe(owner,StickyObserver(this,sticky,observer))
        }

        class StickyObserver<T>(
            private val stickyLiveData: StickyLiveData<T>,
            private val sticky: Boolean,
            val observer: Observer<in T>
        ) : Observer<T> {
            //lastVersion和mVersion对齐的原因，就是控制粘性事件的分发
            private var lastVersion =stickyLiveData.mVersion
            override fun onChanged(t: T) {
                if (lastVersion >=stickyLiveData.mVersion){
                    //就说明stickyLiveData 没有更新的数据需要发送
                    if (sticky&&stickyLiveData.mStickyData!=null){
                        observer.onChanged(stickyLiveData.mStickyData)
                    }
                    return
                }
                lastVersion =stickyLiveData.mVersion
                observer.onChanged(t)
            }

        }

    }

}