package com.unitedpay.core.common.result

/**
 * Standard Result wrapper for all domain and network operations in United Pay.
 * Enforces exhaustive handling of Success, Error, and Loading states.
 */
sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val message: String, val cause: Throwable? = null, val code: Int? = null) : Resource<Nothing>
    object Loading : Resource<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}
