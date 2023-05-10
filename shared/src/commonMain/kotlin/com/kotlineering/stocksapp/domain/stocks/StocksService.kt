package com.kotlineering.stocksapp.domain.stocks

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kotlineering.stocksapp.db.GetStocks
import com.kotlineering.stocksapp.domain.ServiceState
import com.kotlineering.stocksapp.domain.stocks.repository.StocksRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// Repository supports multiple portfolios, but app
// will only use one for now..
private const val DEFAULT_PORTFOLIO = ""

class StocksService(
    private val repository: StocksRepository,
    private val dispatcher: CoroutineDispatcher
) {
    fun getStocks(): Flow<List<GetStocks>> = repository.getPortfolio(
        DEFAULT_PORTFOLIO
    ).asFlow().mapToList(dispatcher).distinctUntilChanged().flowOn(dispatcher)

    fun refreshStocks(): Flow<ServiceState> = flow {
        emit(ServiceState.Busy)
        emit(repository.refreshPortfolio(DEFAULT_PORTFOLIO))
    }.distinctUntilChanged().flowOn(dispatcher)
}
