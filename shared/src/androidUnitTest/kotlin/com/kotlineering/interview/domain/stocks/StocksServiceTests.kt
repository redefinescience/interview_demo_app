package com.kotlineering.interview.domain.stocks

import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.stocks.repository.StocksRepositoryTests
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class StocksServiceTests {

    companion object {
        fun getService() = StocksService(
            StocksRepositoryTests.getRepository(),
            Dispatchers.Default
        )
    }

    @Test
    fun `golden path should emit golden path stuff`() = runBlocking {
        val service = getService()
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
