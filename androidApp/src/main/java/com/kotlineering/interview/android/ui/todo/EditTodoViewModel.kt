package com.kotlineering.interview.android.ui.todo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.todo.ToDoService
import kotlinx.coroutines.launch

class EditTodoViewModel(
    private val service: ToDoService
) : ViewModel() {
    private val mutableIsBusy = MutableLiveData(false)
    val isBusy = mutableIsBusy.distinctUntilChanged()

    private val mutableError = MutableLiveData<ServiceState.Error?>(null)
    val error = mutableError.distinctUntilChanged()

    private val mutableIsSaved = MutableLiveData(false)
    val isSaved = mutableIsSaved.distinctUntilChanged()

    private val mutableTodoId = MutableLiveData<Long?>(null)
    val todo = mutableTodoId.switchMap { id ->
        if (id != null) {
            service.getTodo(id).asLiveData()
        } else {
            MutableLiveData(null)
        }
    }

    private fun newOrEdit(title: String, completed: Boolean) = todo.value?.let { todo ->
        service.updateTodo(
            todo.copy(
                title = title,
                completed = if (completed) {
                    1
                } else {
                    0
                }
            )
        )
    } ?: service.newTodo(title)

    fun saveTodo(title: String, completed: Boolean) = viewModelScope.launch {
        newOrEdit(title, completed).collect {
            mutableIsSaved.postValue(it is ServiceState.Done)
            mutableIsBusy.postValue(it !is ServiceState.Done)
            mutableError.postValue(it.takeIf { it is ServiceState.Error } as ServiceState.Error?)
        }
    }

    fun setTodoId(id: Long) {
        mutableTodoId.postValue(id)
    }
}