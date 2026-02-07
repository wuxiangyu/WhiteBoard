package com.github.guanpy.library;

import android.os.Handler;
import android.os.Looper;
import com.github.guanpy.library.ann.ReceiveEvents;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private static final Map<Object, List<Method>> subscribers = new HashMap<>();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void registerAnnotatedReceiver(Object receiver) {
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = receiver.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(ReceiveEvents.class)) {
                method.setAccessible(true);
                methods.add(method);
            }
        }
        if (!methods.isEmpty()) {
            synchronized (subscribers) {
                subscribers.put(receiver, methods);
            }
        }
    }

    public static void unregisterAnnotatedReceiver(Object receiver) {
        synchronized (subscribers) {
            subscribers.remove(receiver);
        }
    }

    public static void postEvent(final Object event) {
        if (!(event instanceof String)) {
            return;
        }
        final String eventName = (String) event;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (subscribers) {
                    for (Map.Entry<Object, List<Method>> entry : subscribers.entrySet()) {
                        Object receiver = entry.getKey();
                        for (Method method : entry.getValue()) {
                            ReceiveEvents annotation = method.getAnnotation(ReceiveEvents.class);
                            if (annotation != null && eventName.equals(annotation.name())) {
                                try {
                                    method.invoke(receiver);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
