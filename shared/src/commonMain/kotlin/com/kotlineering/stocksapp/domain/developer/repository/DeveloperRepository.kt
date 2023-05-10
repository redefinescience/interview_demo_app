package com.kotlineering.stocksapp.domain.developer.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DeveloperRepository {
    enum class RefreshStocksMode {
        NORMAL,
        MALFORMED,
        EMPTY,
        RUNTIME_ERROR
    }

    private var refreshStocksModeFlow = MutableStateFlow(RefreshStocksMode.NORMAL)

    fun setStocksRefreshMode(mode: RefreshStocksMode) {
        refreshStocksModeFlow.value = mode
    }

    fun getStocksRefreshMode(): Flow<RefreshStocksMode> = refreshStocksModeFlow

    val stocksRefreshMode: RefreshStocksMode
        get() = refreshStocksModeFlow.value
}
