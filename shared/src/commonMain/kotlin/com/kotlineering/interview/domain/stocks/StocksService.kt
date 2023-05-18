package com.kotlineering.interview.domain.stocks

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kotlineering.interview.db.GetStocks
import com.kotlineering.interview.domain.ServiceState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// Repository supports multiple portfolios, but app
// will only use one for now..
private const val DEFAULT_PORTFOLIO = ""

open class StocksService(
    private val repository: StocksRepository,
    private val dispatcher: CoroutineDispatcher
) {
    open fun getStocks(): Flow<List<GetStocks>> = repository.getPortfolio(
        DEFAULT_PORTFOLIO
    ).asFlow().mapToList(dispatcher).distinctUntilChanged().flowOn(dispatcher)

    fun refreshStocks(): Flow<ServiceState> = flow {
        emit(ServiceState.Busy)
        emit(repository.refreshPortfolio(DEFAULT_PORTFOLIO))
    }.distinctUntilChanged().flowOn(dispatcher)
}
