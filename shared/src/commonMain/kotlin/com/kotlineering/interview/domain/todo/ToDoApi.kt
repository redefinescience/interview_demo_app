package com.kotlineering.interview.domain.todo

import com.kotlineering.interview.domain.ApiResult
import com.kotlineering.interview.domain.RemoteApi
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

@Serializable
data class ToDoResult(
    val id: Long,
    val userId: Long,
    val title: String,
    val completed: Boolean
)

class ToDoApi(
    private val client: HttpClient,
    domain: String
) : RemoteApi(domain) {
    suspend fun getTodos(userId: Long) = ApiResult.call<List<ToDoResult>> {
        client.fetch("todos", mapOf("userId" to userId.toString()))
    }
}
