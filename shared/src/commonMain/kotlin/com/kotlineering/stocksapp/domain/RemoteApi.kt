package com.kotlineering.stocksapp.domain

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import io.ktor.serialization.JsonConvertException

// Provide methods to centralize configuration of remote calls
// (eg: setting base domain, configuring Content-Type, etc...)
abstract class RemoteApi(
    private val domain: String
) {
    protected suspend fun HttpClient.fetch(path: String) =
        get {
            url {
                accept(ContentType.Application.Json)
                takeFrom(domain)
                encodedPath = path
            }
        }
}

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
