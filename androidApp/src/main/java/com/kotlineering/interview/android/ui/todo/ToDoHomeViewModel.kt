package com.kotlineering.interview.android.ui.todo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.todo.ToDoRepository
import com.kotlineering.interview.domain.todo.ToDoService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ToDoHomeViewModel(
    val service: ToDoService,
    val developerRepository: DeveloperRepository
) : ViewModel() {

    private val mutableRefreshing = MutableLiveData(false)
    val refreshing = mutableRefreshing.distinctUntilChanged()

    private val mutableError = MutableLiveData<ServiceState.Error?>(null)
    val error = mutableError.distinctUntilChanged()

    private val mutableShowCompleted = MutableLiveData(false)
    val showCompleted = mutableShowCompleted.distinctUntilChanged()

    val todos = showCompleted.switchMap { showCompleted ->
        service.getTodos(showCompleted).asLiveData()
    }

    fun refresh() = viewModelScope.launch {
        mutableError.postValue(null)
        service.refreshTodoList().collect {
            mutableRefreshing.postValue(it is ServiceState.Busy)
            mutableError.postValue(it.takeIf { it is ServiceState.Error } as ServiceState.Error?)
        }
    }

    fun setShowCompleted(showCompleted: Boolean) = mutableShowCompleted.postValue(showCompleted)
}