package com.github.guanpy.library

import android.os.Handler
import android.os.Looper
import com.github.guanpy.library.ann.ReceiveEvents
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.HashMap

object EventBus {
    private val subscribers: MutableMap<Any, List<Method>> = HashMap()
    private val mainHandler = Handler(Looper.getMainLooper())

    @JvmStatic
    fun registerAnnotatedReceiver(receiver: Any) {
        val methods: MutableList<Method> = ArrayList()
        val declaredMethods = receiver.javaClass.declaredMethods
        for (method in declaredMethods) {
            if (method.isAnnotationPresent(ReceiveEvents::class.java)) {
                method.isAccessible = true
                methods.add(method)
            }
        }
        if (methods.isNotEmpty()) {
            synchronized(subscribers) {
                subscribers[receiver] = methods
            }
        }
    }

    @JvmStatic
    fun unregisterAnnotatedReceiver(receiver: Any) {
        synchronized(subscribers) {
            subscribers.remove(receiver)
        }
    }

    @JvmStatic
    fun postEvent(event: Any) {
        if (event !is String) {
            return
        }
        val eventName = event
        mainHandler.post {
            synchronized(subscribers) {
                for ((receiver, methods) in subscribers) {
                    for (method in methods) {
                        val annotation = method.getAnnotation(ReceiveEvents::class.java)
                        if (annotation != null && eventName == annotation.name) {
                            try {
                                method.invoke(receiver)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }
}
