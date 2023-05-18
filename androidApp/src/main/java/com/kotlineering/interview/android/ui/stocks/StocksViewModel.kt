package com.kotlineering.interview.android.ui.stocks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.kotlineering.interview.db.GetStocks
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.stocks.StocksService
import kotlinx.coroutines.launch

open class StocksViewModel(
    private val stocksService: StocksService,
) : ViewModel() {
    private val mutableRefreshing = MutableLiveData(false)
    val refreshing = mutableRefreshing.distinctUntilChanged()

    private val mutableError = MutableLiveData<ServiceState.Error?>(null)
    val error = mutableError.distinctUntilChanged()

    private val mutableFilter = MutableLiveData("")

    fun setFilter(filter: String) {
        mutableFilter.postValue(filter)
    }

    val stocks = mutableFilter.distinctUntilChanged().switchMap { filter ->
        stocksService.getStocks().asLiveData().map { result ->
            result.filter { it.ticker.startsWith(filter) }
        }
    }

    fun refreshStocks() = viewModelScope.launch {
        mutableError.postValue(null)
        stocksService.refreshStocks().collect {
            mutableRefreshing.postValue(it is ServiceState.Busy)
            mutableError.postValue(it.takeIf { it is ServiceState.Error } as ServiceState.Error?)
        }
    }
}
