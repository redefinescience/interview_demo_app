package com.kotlineering.interview.domain.todo

import com.kotlineering.interview.db.Todos
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
        client.get("todos", mapOf("userId" to userId.toString()))
    }

    suspend fun deleteTodo(id: Long) = ApiResult.call<Any?> {
        client.delete("todos/$id")
    }

    suspend fun newTodo(todo: Todos) = ApiResult.call<Any?> {
        client.post(
            "todos",
            ToDoResult(
                todo.id,
                todo.userId,
                todo.title,
                todo.completed != 0L
            )
        )
    }

    suspend fun updateTodo(todo: Todos) = ApiResult.call<Any?> {
        client.put(
            "todos/${todo.id}",
            ToDoResult(
                todo.id,
                todo.userId,
                todo.title,
                todo.completed != 0L
            )
        )
    }
}
