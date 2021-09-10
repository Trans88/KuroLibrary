package com.trs88.kurolibrary.execute

import android.app.job.JobScheduler
import com.trs88.kurolibrary.log.KuroLog
import java.lang.IllegalArgumentException
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong

object KuroScheduledExecutor {
    private val cpuCount = Runtime.getRuntime().availableProcessors()
    private val corePoolSize = cpuCount + 1

    private val seq = AtomicLong()//使用原子类作为序号

    private val threadFactory = ThreadFactory {
        val thread = Thread(it)
        //设置线程名称
        thread.name = "kuro-executor-time-" + seq.getAndIncrement()
        return@ThreadFactory thread
    }

//    private val mScheduledExecutorService : ScheduledExecutorService =Executors.newScheduledThreadPool(corePoolSize, threadFactory)
    private var mScheduledExecutorService : ScheduledExecutorService? =null

    fun execute(task:AbstractScheduledTask){
        if (mScheduledExecutorService==null){
            mScheduledExecutorService=Executors.newScheduledThreadPool(corePoolSize, threadFactory)
        }
        
        val scheduledFuture:ScheduledFuture<*> = mScheduledExecutorService?.scheduleAtFixedRate(
            task,
            task.delay,
            task.period,
            task.timeUnit
        ) ?: throw IllegalArgumentException("mScheduledExecutorService is null")
        scheduledFuture.cancel(true)

        //todo 用map存放这个future 用来停止任务

    }

    fun cancel(){
        mScheduledExecutorService?.shutdown()
        mScheduledExecutorService =null
    }

    abstract class AbstractScheduledTask(val delay:Long,val period:Long,val timeUnit:TimeUnit) :Runnable{
//        //执行延迟
//        abstract var delay:Long
//        abstract var period:Long
//        abstract var timeUnit:TimeUnit

        abstract fun task()

        override fun run() {
            try {
                task()
            }catch (e:Exception){
                KuroLog.e("run AbstractScheduledTask is error message ：${e.message}")
            }

        }
    }
}