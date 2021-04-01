package com.trs88.kurolibrary.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * 获取栈顶Activity和对于前后台切换的通知管理
 */
class ActivityManager private constructor() {

    private val activityRefs = ArrayList<WeakReference<Activity>>()
    private val frontBackCallback = ArrayList<FrontBackCallback>() //前后台切换监听存储
    private var activityStartCount = 0
    private var front = true//标志位，判断activity是否在前台

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(InnerActivityLifecycleCallbacks())
    }

    inner class InnerActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
            activityStartCount++
            //activityStartCount >0说明应用处在可见状态，也就是前台
            //!front 判断之前是不是在后台
            if (!front && activityStartCount > 0) {
                //之前在后台，发送了切前台的动作
                front = true
                onFrontBackChanged(front)
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            for (activityRef in activityRefs) {
                if (activityRef != null && activityRef.get() == activity) {
                    activityRefs.remove(activityRef)
                    break
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
            activityStartCount--
            if (activityStartCount <= 0 && front) {
                front = false;
                onFrontBackChanged(front)
            }
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityRefs.add(WeakReference(activity))
        }

        override fun onActivityResumed(activity: Activity) {
        }

    }

    /**
     * 应用发生了前后台的变化
     */
    private fun onFrontBackChanged(front: Boolean) {
        for (callback in frontBackCallback) {
            callback.onChange(front)
        }
    }

    val topActivity: Activity?
        get() {
            return if (activityRefs.size <= 0) {
                null
            } else {
                activityRefs[activityRefs.size-1].get()
            }
        }

    fun addFrontBackCallback(callback: FrontBackCallback) {
        frontBackCallback.add(callback)
    }

    fun removeFrontBackCallback(callback: FrontBackCallback) {
        frontBackCallback.remove(callback)
    }

    interface FrontBackCallback {
        fun onChange(front: Boolean)
    }

    companion object {
        val instance: ActivityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }
}