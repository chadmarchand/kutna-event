package com.chadmarchand.kutna.event

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

fun _getEventHandlerMethods(eventHandlerClass: KClass<*>): List<KFunction<*>> {
    return eventHandlerClass.memberFunctions.filter {
        it.findAnnotation<OnEvent>() != null
    }
}

fun _getEventHandlerEventId(eventHandler: KFunction<*>): String {
    val onEventAnnotation = eventHandler.findAnnotation<OnEvent>()
    return onEventAnnotation!!.eventClass.qualifiedName!!
}

class EventBus {
    private val subscribers: MutableMap<
            String,
            MutableCollection<Pair<Any, KFunction<*>>>
    > = mutableMapOf()

    fun <T : Event> publish(event: T) {
        val eventSubscribers = subscribers[event.eventTypeId]
        if (eventSubscribers != null) {
            eventSubscribers.stream().forEach { subscriber ->
                subscriber.second.call(subscriber.first, event)
            }
        }
    }

    inline fun <reified T : Any> registerSubscriber(
        eventHandler: T
    ) {
        _getEventHandlerMethods(T::class).forEach { handlerMethod ->
            _subscribe(
                _getEventHandlerEventId(handlerMethod),
                eventHandler,
                handlerMethod
            )
        }
    }

    // Only public so registerSubscriber can be reified
    fun _subscribe(eventTypeId: String, eventHandler: Any, handlerMethod: KFunction<*>) {
        val subscriber = Pair(eventHandler, handlerMethod)

        val eventTypeSubscribers = subscribers[eventTypeId]
        if (eventTypeSubscribers != null) {
            eventTypeSubscribers.add(subscriber)
        } else {
            subscribers[eventTypeId] = mutableListOf(subscriber)
        }
    }
}
