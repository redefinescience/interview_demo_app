package com.kotlineering.interview.domain.stocks

import com.kotlineering.interview.TestComponents
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class StocksServiceTests {

    companion object {
        fun getMockService() = StocksService(
            StocksRepositoryTests.getMockRepository(),
            Dispatchers.Default
        )
    }

    @Test
    fun `real world empty test (accesses real network)`() = runBlocking {
        val service = StocksService(
            StocksRepository(
                TestComponents.getStocksApi(),
                TestComponents.getDatabase(),
                DeveloperRepository().apply {
                    setStocksRefreshMode(DeveloperRepository.RefreshStocksMode.EMPTY)
                }
            ),
            Dispatchers.Default
        )

        assertTrue {
            service.getStocks().take(1).toList()[0].isEmpty()
        }

        expect(listOf(ServiceState.Busy, ServiceState.Done)) {
            service.refreshStocks().toList()
        }

        assertTrue {
            service.getStocks().take(1).toList()[0].isEmpty()
        }
    }

    @Test
    fun `real world non-empty test (accesses real network)`() = runBlocking {
        val service = StocksService(
            StocksRepository(
                TestComponents.getStocksApi(),
                TestComponents.getDatabase(),
                DeveloperRepository()
            ),
            Dispatchers.Default
        )

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

    @Test
    fun `golden path should emit golden path stuff`() = runBlocking {
        val service = getMockService()
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
