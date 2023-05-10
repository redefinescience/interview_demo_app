package com.kotlineering.stocksapp.domain.stocks

import com.kotlineering.stocksapp.domain.ServiceState
import com.kotlineering.stocksapp.domain.stocks.repository.StocksRepositoryTests
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StocksServiceTests {

    private val service by lazy {
        StocksService(
            StocksRepositoryTests.getRepository(),
            Dispatchers.Default
        )
    }

    @Test
    fun `golden path should emit golden path stuff`() = runBlocking {
        assertTrue {
            service.getStocks().take(1).toList()[0].isEmpty()
        }

        expect(listOf(ServiceState.Busy, ServiceState.Done)) {
            service.refreshStocks().toList()
        }

        assertTrue {
            service.getStocks().take(1).toList()[0].isNotEmpty()
        }
    }
}
