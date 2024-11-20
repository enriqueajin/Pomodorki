package com.enriqueajin.pomidorki.features.common.utl

sealed interface AppError {
    // Todo: Make values for different exceptions
}

sealed interface Result <T>{
    data class Success <T> (val data: T) : Result<T>
    data class Failure<T>(val error: AppError) : Result<T>

    fun getOrNull(): T? {
        return if (this is Success) data else null
    }
}