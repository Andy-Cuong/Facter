package com.andyc.core.domain.util

// out annotation: generics of subtype of T can be safely assigned to generics of type T, but T can
// only be used as return type (produced) in its class functions i.e. get out of its class
// in annotation: generics of supertype of T can be safely assigned to generics of type T, but T can
// only be used as parameter type (consumed) in its class functions i.e. get into its class
sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: com.andyc.core.domain.util.Error>(val error: E): Result<Nothing, E>
}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E: Error> Result<T, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}

// typealias is used just to give a class an alternative (typically shorter) name
typealias EmptyResult<E> = Result<Unit, E>