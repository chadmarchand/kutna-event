package com.chadmarchand.kutna.event

abstract class Event(
    val eventTypeId: String
) {
    override fun toString(): String {
        return eventTypeId
    }
}
