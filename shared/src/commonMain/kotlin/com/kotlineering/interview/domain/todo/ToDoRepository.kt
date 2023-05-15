package com.kotlineering.interview.domain.todo

import com.kotlineering.interview.db.Database
import com.kotlineering.interview.db.Todos
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
        if (dev.refreshMode == DeveloperRepository.RefreshMode.RUNTIME_ERROR) {
            throw Exception("Developer Mode Exception")
        }
    }

    private fun deleteTodo(id: Long) = db.tryTransaction {
        db.databaseQueries.deleteTodo(id)
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
            id = (db.databaseQueries.getGreatestTodoId().executeAsOneOrNull() ?: 0) + 1,
            userId = todo.userId,
            title = todo.title,
            completed = todo.completed,
            sequence = (db.databaseQueries.getLowestTodoSequence().executeAsOneOrNull() ?: 0) - 1
        )
    }

    suspend fun updateTodo(
        todo: Todos,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = updateTodoItem(todo, timeStamp).toServiceState {
        // Update on server
        api.updateTodo(todo).toServiceState()
    }

    suspend fun newTodo(
        todo: Todos,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = insertNewTodo(todo, timeStamp).toServiceState {
        api.newTodo(todo).toServiceState()
    }

    suspend fun updateTodoList(
        userId: Long,
        todos: List<Todos>,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = updateTodos(userId, todos, timeStamp).toServiceState {
        // Update to server
        // Make a fake call to simulate network request being made
        // to server to indicate the order has been changed..
        api.getTodos(1).toServiceState()
    }

    suspend fun removeTodo(
        id: Long
    ) = deleteTodo(id).toServiceState {
        // Remote delete
        api.deleteTodo(id).toServiceState()
    }

    suspend fun refreshTodoList(
        userId: Long,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = when (dev.refreshMode) {
        DeveloperRepository.RefreshMode.EMPTY -> api.getTodosEmpty()
        else -> api.getTodos(userId)
    }.toServiceState { data ->
        updateTodos(
            userId, data.mapIndexed { i, it ->
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
