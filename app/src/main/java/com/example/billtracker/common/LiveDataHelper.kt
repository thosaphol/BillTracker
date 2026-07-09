package com.example.billtracker.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData


fun <A, B, R> combineLatest(
    liveDataA: LiveData<A>,
    liveDataB: LiveData<B>,
    transform: (A, B) -> R
): LiveData<R> {
    val mediator = MediatorLiveData<R>()
    var lastA: A? = null
    var lastB: B? = null

    mediator.addSource(liveDataA) { a ->
        lastA = a
        val b = lastB
        if (b != null) mediator.value = transform(a, b)
    }
    mediator.addSource(liveDataB) { b ->
        lastB = b
        val a = lastA
        if (a != null) mediator.value = transform(a, b)
    }
    return mediator
}