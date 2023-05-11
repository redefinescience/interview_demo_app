package com.kotlineering.interview.domain.todo

import com.kotlineering.interview.db.Database
import com.kotlineering.interview.db.Todos
import com.kotlineering.interview.domain.ApiResult
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.toServiceState
import com.kotlineering.interview.domain.tryTransaction
import kotlinx.datetime.Clock

class ToDoRepository(
    internal val api: ToDoApi,
    internal val db: Database,
    internal val dev: DeveloperRepository
) {
    private fun updateTodos(
        userId: Long, todos: List<Todos>, timeStamp: Long
    ) = db.tryTransaction {
        db.databaseQueries.clearTodos(userId)
        todos.forEachIndexed { i, it ->
            db.databaseQueries.upsertTodo(
                id = it.id,
                userId = userId,
                title = it.title,
                completed = it.completed,
                sequence = i.toLong()
            )
        }
        // Throw exception if dev options indicate to do so...
        // (done here to test rollback)
        if (dev.stocksRefreshMode == DeveloperRepository.RefreshStocksMode.RUNTIME_ERROR) {
            throw Exception("Developer Mode Exception")
        }
    }

    private fun updateTodoItem(
        todo: Todos,
        timeStamp: Long
    ) = db.tryTransaction {
        db.databaseQueries.upsertTodo(
            todo.id, todo.userId, todo.title, todo.completed, todo.sequence
        )
    }

    private fun insertNewTodo(
        todo: Todos,
        timeStamp: Long
    ) = db.tryTransaction {
        db.databaseQueries.upsertTodo(
            id = db.databaseQueries.getGreatestTodoId().executeAsOne() + 1,
            userId = todo.userId,
            title = todo.title,
            completed = todo.completed,
            sequence = db.databaseQueries.getLowestTodoSequence().executeAsOne() - 1
        )
    }

    suspend fun updateTodo(
        todo: Todos,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = updateTodoItem(todo, timeStamp).toServiceState {
        // Upload to server
        ServiceState.Done
    }

    suspend fun newTodo(
        todo: Todos,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = insertNewTodo(todo, timeStamp).toServiceState {
        // Upload to server
        ServiceState.Done
    }

    suspend fun updateTodoList(
        userId: Long,
        todos: List<Todos>,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = updateTodos(userId, todos, timeStamp).toServiceState {
        // Remote Update
        ServiceState.Done
    }

    private fun deleteTodo(id: Long) = db.tryTransaction {
        db.databaseQueries.deleteTodo(id)
    }

    suspend fun removeTodo(
        id: Long
    ) = deleteTodo(id).toServiceState {
        // Remote delete
        ServiceState.Done
    }

    suspend fun refreshTodoList(
        userId: Long,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = when (
        val result = api.getTodos(userId)
    ) {
        is ApiResult.Error -> ServiceState.Error.Api(result.error)
        is ApiResult.Exception -> ServiceState.Error.Network(
            result.throwable.message ?: result.throwable.toString()
        )

        is ApiResult.Success -> updateTodos(
            userId, result.data.mapIndexed { i, it ->
                Todos(
                    id = it.id,
                    userId = userId,
                    title = it.title,
                    completed = if (it.completed) {
                        1
                    } else {
                        0
                    },
                    sequence = i.toLong()
                )
            }, timeStamp
        ).toServiceState()
    }
}
