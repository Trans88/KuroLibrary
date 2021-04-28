package com.trs88.kurolibrary.execute

import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import com.trs88.kurolibrary.log.KuroLog
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * 支持按任务的优先级去执行，
 * 支持线程池暂停，恢复（比如批量文件下载，上传），
 * 支持异步结果主动回调主线程
 * todo 线程池能力监控，耗时任务检测，定时，延迟
 */
object KuroExecutor {
    private val TAG: String = "KuroExecutor"
    private var isPaused: Boolean = false
    private var kuroExecutor: ThreadPoolExecutor
    private var lock: ReentrantLock = ReentrantLock()
    private var pauseCondition: Condition
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        pauseCondition = lock.newCondition()

        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePoolSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        val blockingQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()
        val keepAliveTime = 30L
        val unit = TimeUnit.SECONDS

        val seq = AtomicLong()//使用原子类作为序号

        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            //设置线程名称
            thread.name = "kuro-executor-" + seq.getAndIncrement()
            return@ThreadFactory thread
        }

        //创建线程池
        kuroExecutor = object : ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, blockingQueue as BlockingQueue<Runnable>, threadFactory) {
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                if (isPaused) {
                    lock.lock()
                    try {
                        pauseCondition.await()
                    } finally {
                        lock.unlock()
                    }
                }
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                //监控线程池耗时任务，线程创建数量，正在运行的数量
                KuroLog.it(TAG, "已执行完的任务的优先级是：" + (r as PriorityRunnable).priority)
            }
        }
    }

    @JvmOverloads
    fun execute(runnable: Runnable,@IntRange(from = 0, to = 10) priority: Int = 0 ) {
        kuroExecutor.execute(PriorityRunnable(priority, runnable))
    }

    @JvmOverloads
    fun execute(runnable: Callable<*>,@IntRange(from = 0, to = 10) priority: Int = 0 ) {
        kuroExecutor.execute(PriorityRunnable(priority, runnable))
    }

    abstract class Callable<T> : Runnable {
        override fun run() {
            mainHandler.post { onPrepare() }

            val t = onBackground()

            //移除所有消息，防止需要执行onComplete了，onPrepare还没有被执行，那就不需要执行了
            mainHandler.removeCallbacksAndMessages(null)
            mainHandler.post { onComplete(t) }

        }

        //任务执行前
        open fun onPrepare() {

        }

        //任务执行中
        abstract fun onBackground(): T

        //任务执行完
        abstract fun onComplete(t: T)
    }

    class PriorityRunnable(val priority: Int, val runnable: Runnable) : Runnable, Comparable<PriorityRunnable> {
        override fun run() {
            runnable.run()
        }

        override fun compareTo(other: PriorityRunnable): Int {
            return if (this.priority < other.priority) 1 else if (this.priority > other.priority) -1 else 0
        }

    }

    @Synchronized
    fun pause() {
        isPaused = true
        KuroLog.et(TAG, "KuroExecutor is paused")
    }

    @Synchronized
    fun resume() {
        isPaused = false
        lock.lock()
        try {
            //唤醒所有阻塞的线程
            pauseCondition.signalAll()
        } finally {
            lock.unlock()
        }
        KuroLog.et(TAG, "KuroExecutor is resumed")
    }
}