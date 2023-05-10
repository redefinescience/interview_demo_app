package com.kotlineering.interview.android.ui.todo

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.BuildConfig
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.ui.HeaderAdapter
import com.kotlineering.interview.android.ui.home.HomeFragment
import com.kotlineering.interview.db.Todos
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.todo.ToDoService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditTodoViewModel(
    val service: ToDoService
) : ViewModel() {
    private val mutableIsBusy = MutableLiveData(false)
    val isBusy = mutableIsBusy.distinctUntilChanged()

    private val mutableError = MutableLiveData<ServiceState.Error?>(null)
    val error = mutableError.distinctUntilChanged()

    private val mutableIsSaved = MutableLiveData(false)
    val isSaved = mutableIsSaved.distinctUntilChanged()

    private val mutableTodoId = MutableLiveData<Long?>()
    val isNew = mutableTodoId.map { it == null }.distinctUntilChanged()

    fun newTodo(title: String) = viewModelScope.launch {
        service.newTodo(title).collect {
            mutableIsBusy.postValue(it !is ServiceState.Done)
            mutableIsSaved.postValue(it is ServiceState.Done)
            mutableError.postValue(it.takeIf { it is ServiceState.Error } as ServiceState.Error?)
        }
    }

    fun editTodo(todo: Todos) = viewModelScope.launch {
        service.updateTodo(todo).collect {
            mutableIsBusy.postValue(it !is ServiceState.Done)
            mutableIsSaved.postValue(it is ServiceState.Done)
            mutableError.postValue(it.takeIf { it is ServiceState.Error } as ServiceState.Error?)
        }
    }

    fun setTodoId(id: Long) {
        mutableTodoId.postValue(id)
    }
}

class EditTodoDialogFragment : DialogFragment() {
    private val viewModel: EditTodoViewModel by viewModel()

}

class ToDoHomeFragment : HomeFragment() {

    private val viewModel: ToDoHomeViewModel by viewModel()

    override fun createAdapters(): RecyclerView.Adapter<*> = ConcatAdapter(
        HeaderAdapter(
            ToDoHeaderView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                if(BuildConfig.DEBUG) {
                    enableDevButton()
                    onDevOptsClicked = {
                        binding.devopts.visibility = View.VISIBLE
                    }
                }

                // Refresh button
                onRefreshClicked = {
                    viewModel.refresh()
                }

                viewModel.refreshing.observe(viewLifecycleOwner) {
                    refreshEnabled = !it
                }

                // Error visual
                viewModel.error.observe(viewLifecycleOwner) {
                    error = when (it) {
                        is ServiceState.Error.Api -> resources.getString(R.string.error_api)
                        is ServiceState.Error.Runtime -> resources.getString(R.string.error_runtime)
                        is ServiceState.Error.Network -> resources.getString(R.string.error_network)
                        else -> ""
                    }
                }
            }
        )
    ).also {
        viewModel.todos.observe(viewLifecycleOwner) {
            Log.d("CHRIS", "$it")
        }
    }

    override fun getDeveloperRepository(): DeveloperRepository = viewModel.developerRepository
}
