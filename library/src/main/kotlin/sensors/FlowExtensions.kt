package sensors

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
fun Flow<Boolean>.goesActive(): Flow<Boolean> =
    window(2)
    .transform {
        if(!it.first() && it.last())
         emit(true)
    }

fun Flow<Boolean>.goesInactive(): Flow<Boolean> =
    window(2)
    .transform {
        if(it.first() && !it.last())
            emit(true)
    }
