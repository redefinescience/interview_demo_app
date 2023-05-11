package com.kotlineering.interview.domain

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val error: String) : ApiResult<Nothing>
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>

    companion object {
        suspend inline fun <reified T> call(exec: () -> HttpResponse) = try {
            val result = exec.invoke()
            if (result.status.isSuccess()) {
                Success(result.body<T>())
            } else {
                Error(result.status.description)
            }
        } catch (t: Throwable) {
            // TODO: Generic logger mapped to platform logger - log this exception
            when (t) {
                is JsonConvertException -> Error("JSON decoding error.")
                else -> Exception(t)
            }
        }
    }
}

suspend fun <T> ApiResult<T>.toServiceState(onDone: (suspend (data: T) -> ServiceState)? = null) = when(this) {
    is ApiResult.Error -> ServiceState.Error.Api(this.error)
    is ApiResult.Exception -> ServiceState.Error.Network(
        this.throwable.message ?: this.throwable.toString()
    )
    is ApiResult.Success -> onDone?.invoke(this.data) ?: ServiceState.Done
}
