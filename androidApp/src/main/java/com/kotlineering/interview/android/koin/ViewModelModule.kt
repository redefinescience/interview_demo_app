package com.kotlineering.interview.android.koin

import com.kotlineering.interview.android.ui.stocks.StocksViewModel
import com.kotlineering.interview.android.ui.todo.EditTodoViewModel
import com.kotlineering.interview.android.ui.todo.ToDoHomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel { ToDoHomeViewModel(get(), get()) }
    viewModel { StocksViewModel(get(), get()) }
    viewModel { EditTodoViewModel(get()) }
}
