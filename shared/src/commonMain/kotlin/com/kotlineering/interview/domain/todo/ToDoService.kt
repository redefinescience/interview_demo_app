package com.kotlineering.interview.domain.todo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kotlineering.interview.db.Todos
import com.kotlineering.interview.domain.ServiceState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val DEFAULT_USERID = 1L

class ToDoService(
    private val repository: ToDoRepository,
    private val dispatcher: CoroutineDispatcher
) {

    fun getTodos(completed: Boolean): Flow<List<Todos>> = repository.getTodoList(
        DEFAULT_USERID, completed
    ).asFlow().mapToList(dispatcher).distinctUntilChanged().flowOn(dispatcher)

    fun refreshTodoList(): Flow<ServiceState> = flow {
        emit(ServiceState.Busy)
        emit(repository.refreshTodoList(DEFAULT_USERID))
    }.distinctUntilChanged().flowOn(dispatcher)

    fun newTodo(title: String): Flow<ServiceState> = flow {
        emit(ServiceState.Busy)
        emit(
            repository.newTodo(
                Todos(0, DEFAULT_USERID, title, 0, 0)
            )
        )
    }.flowOn(dispatcher)

    fun updateTodo(todo: Todos): Flow<ServiceState> = flow {
        emit(ServiceState.Busy)
        emit(
            repository.updateTodo(todo)
        )
    }.distinctUntilChanged().flowOn(dispatcher)
}
