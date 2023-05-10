package com.kotlineering.stocksapp.domain

import io.ktor.client.call.HttpClientCall
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ApiResultTests {
    @Test
    fun `when remote call successful should return ApiResultSuccess`() = runBlocking {
        val payload = "PAYLOAD"
        val mockCall: HttpClientCall = mock {
            onBlocking { bodyNullable(anyOrNull()) }.doReturn(payload)
        }
        val response: HttpResponse = mock {
            on { status }.doReturn(HttpStatusCode.OK)
            onBlocking { call }.doReturn(mockCall)
        }

        expect(ApiResult.Success(payload)) {
            ApiResult.call {
                response
            }
        }
    }

    @Test
    fun `when remote call not should return ApiResultError`() = runBlocking {
        val response: HttpResponse = mock {
            on { status }.doReturn(HttpStatusCode.NotFound)
        }
        expect(ApiResult.Error(HttpStatusCode.NotFound.description)) {
            ApiResult.call<Any> {
                response
            }
        }
    }

    @Test
    fun `when remote call throws JsonConvertException should return ApiResultError`() = runBlocking {
        expect(ApiResult.Error("JSON decoding error.")) {
            ApiResult.call<Any> { throw JsonConvertException("") }
        }
    }

    @Test
    fun `when remote call throws other exception should return ApiResultException`() = runBlocking {
        val t = Exception("oops")
        expect(ApiResult.Exception(t)) {
            ApiResult.call<Any> { throw t }
        }
    }
}
