package com.chadmarchand.kutna.event

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnEvent(val eventClass: KClass<*>)
