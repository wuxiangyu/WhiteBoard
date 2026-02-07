package com.github.guanpy.library.ann

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ReceiveEvents(val name: String)
