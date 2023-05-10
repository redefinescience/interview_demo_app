package com.kotlineering.interview.android.koin

import com.kotlineering.interview.android.ui.stockshome.StocksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModuleModule = module {
    viewModel { StocksViewModel(get(), get()) }
}
