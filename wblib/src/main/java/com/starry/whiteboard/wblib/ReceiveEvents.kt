package com.starry.whiteboard.wblib

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ReceiveEvents(val name: String)
