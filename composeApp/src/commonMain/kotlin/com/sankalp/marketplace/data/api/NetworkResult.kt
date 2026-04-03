package com.sankalp.marketplace.data.api

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    sealed class Error : NetworkResult<Nothing>() {
        data class GeneralError(
            val message: String,
            val code: Int? = null
        ) : Error()

        data class NetworkError(
            val message: String
        ) : Error()
    }

    object Loading : NetworkResult<Nothing>()
}