package com.kotlineering.interview.android.ui.stocks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.stocks.StocksService
import kotlinx.coroutines.launch

class StocksViewModel(
    private val stocksService: StocksService,
    val developerRepository: DeveloperRepository
) : ViewModel() {
    private val mutableRefreshing = MutableLiveData(false)
    val refreshing = mutableRefreshing.distinctUntilChanged()

    private val mutableError = MutableLiveData<ServiceState.Error?>(null)
    val error = mutableError.distinctUntilChanged()

    private val stocksFlow = stocksService.getStocks()
    val stocks = stocksFlow.asLiveData().also {
        refreshStocks()
    }

    fun refreshStocks() = viewModelScope.launch {
        mutableError.postValue(null)
        stocksService.refreshStocks().collect {
            mutableRefreshing.postValue(it is ServiceState.Busy)
            mutableError.postValue(it.takeIf { it is ServiceState.Error } as ServiceState.Error?)
        }
    }
}
