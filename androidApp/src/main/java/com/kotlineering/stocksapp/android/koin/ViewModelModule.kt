package com.kotlineering.stocksapp.android.koin

import com.kotlineering.stocksapp.android.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModuleModule = module {
    viewModel { HomeViewModel(get(), get()) }
}
