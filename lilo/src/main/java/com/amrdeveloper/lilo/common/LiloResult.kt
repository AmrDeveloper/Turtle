package com.amrdeveloper.lilo.common

sealed interface LiloResult<out T> {
    data class Success<T>(val data: T) : LiloResult<T>
    data class Failure<E>(val error: E) : LiloResult<Nothing>
}

fun <T> LiloResult<T>.toSuccessData(): T = (this as LiloResult.Success<T>).data

fun <T> LiloResult<T>.toFailure(): LiloResult.Failure<*> = this as LiloResult.Failure<*>

fun <E> LiloResult<*>.toFailureError(): E = (this as LiloResult.Failure<E>).error

fun <T> LiloResult<T>.isSuccess(): Boolean {
    return this is LiloResult.Success
}

fun <T> LiloResult<T>.isFailure(): Boolean {
    return this is LiloResult.Failure<*>
}