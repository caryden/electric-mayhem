package edu.ncssm.ftc.electricmayhem.core.sensors

import edu.ncssm.ftc.electricmayhem.core.sensors.data.SensorData
import kotlinx.coroutines.flow.*

infix fun Flow<Boolean>.and(other: Flow<Boolean>): Flow<Boolean> =
    this.combine(other) { a, b -> a && b }

infix fun Flow<Boolean>.or(other: Flow<Boolean>): Flow<Boolean> =
    this.combine(other) { a, b -> a || b }

fun Flow<Boolean>.not(): Flow<Boolean> =
    this.map { value -> !value }

fun <T> Flow<T>.window(size: Int): Flow<List<T>> = flow {
    val window = ArrayDeque<T>(size)
    collect { value ->
        if (window.size == size) window.removeFirst()
        window.addLast(value)
        if (window.size == size) emit(window.toList())
    }
}
fun <T> Flow<SensorData<T>>.value() = map { it.value }

fun Flow<SensorData<Boolean>>.goesActive(): Flow<Boolean> =
    window(2)
    .transform {
        if(!it.first().value && it.last().value)
         emit(true)
    }

fun Flow<SensorData<Boolean>>.goesInactive(): Flow<Boolean> =
    window(2)
    .transform {
        if(it.first().value && !it.last().value)
            emit(true)
    }
