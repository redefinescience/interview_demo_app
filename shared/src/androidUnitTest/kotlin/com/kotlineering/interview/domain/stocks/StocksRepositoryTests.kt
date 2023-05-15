package com.kotlineering.interview.domain.stocks

import com.kotlineering.interview.TestComponents
import com.kotlineering.interview.db.Database
import com.kotlineering.interview.db.GetStocks
import com.kotlineering.interview.domain.ApiResult
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class StocksRepositoryTests {

    companion object {
        val testStocks = listOf(
            StockResult(
                "GSPC", "SP", "USD", 318157, null, 1681845832
            ), StockResult(
                "RUNINC", "Runners", "USD", 3614, 5, 1681845832
            ), StockResult(
                "BAC", "Bank", "USD", 2393, 10, 1681845832
            )
        )

        private fun getMockApi(): StocksApi = mock {
            onBlocking { getStocks() }.doReturn(ApiResult.Success(StocksResult(testStocks.map { it.copy() })))
            onBlocking { getStocksEmpty() }.doReturn(
                ApiResult.Success(
                    StocksResult(emptyList())
                )
            )
            onBlocking { getStocksMalformed() }.doReturn(
                ApiResult.Error("json error")
            )
        }

        fun getMockRepository(
            api: StocksApi = getMockApi(),
            devOpts: DeveloperRepository = DeveloperRepository(),
            db: Database = TestComponents.getDatabase()
        ) = StocksRepository(
            api = api,
            dev = devOpts,
            db = db
        )
    }

    @Test
    fun `when stocks are fetched, they are accessed from db in same order`() = runBlocking {
        val repository = getMockRepository()
        val timeStamp = Clock.System.now().toEpochMilliseconds()

        expect(ServiceState.Done) {
            repository.refreshPortfolio("", timeStamp)
        }

        expect(testStocks.mapIndexed { i, it ->
            GetStocks(
                it.ticker,
                it.name,
                it.currency,
                it.current_price_cents,
                it.quantity ?: 0,
                it.current_price_timestamp,
                timeStamp,
                "", // SqlDelight internal field
                "", // SqlDelight internal field
                i.toLong()
            )
        }) {
            repository.getPortfolio("").executeAsList().map {
                it.copy(
                    // Clear SqlDelight internal fields
                    name_ = "", ticker_ = ""
                )
            }
        }
    }

    @Test
    fun `when devOpt empty enabled, should get empty list`() = runBlocking {
        val repository = getMockRepository()
        repository.dev.setRefreshMode(DeveloperRepository.RefreshMode.EMPTY)
        expect(ServiceState.Done) {
            repository.refreshPortfolio("")
        }
        expect(emptyList()) {
            repository.getPortfolio("").executeAsList()
        }
    }

    @Test
    fun `when devOpt malformed enabled, should return Api error and get empty list`() =
        runBlocking {
            val repository = getMockRepository()
            repository.dev.setRefreshMode(DeveloperRepository.RefreshMode.MALFORMED)
            assertTrue {
                repository.refreshPortfolio("") is ServiceState.Error.Api
            }
            expect(emptyList()) {
                repository.getPortfolio("").executeAsList()
            }
        }

    @Test
    fun `when devOpt runtime error enabled, should return runtime error and get empty list`() =
        runBlocking {
            val repository = getMockRepository()
            repository.dev.setRefreshMode(DeveloperRepository.RefreshMode.RUNTIME_ERROR)
            assertTrue {
                repository.refreshPortfolio("") is ServiceState.Error.Runtime
            }
            expect(emptyList()) {
                repository.getPortfolio("").executeAsList()
            }
        }

    @Test
    fun `when api returns exception, should return network error and get empty list`() =
        runBlocking {
            val mockApi = mock<StocksApi> {
                onBlocking { getStocks() }.doReturn(
                    ApiResult.Exception(Exception())
                )
            }
            val repository = getMockRepository(
                api = mockApi
            )

            assertTrue {
                repository.refreshPortfolio("") is ServiceState.Error.Network
            }
            expect(emptyList()) {
                repository.getPortfolio("").executeAsList()
            }
        }
}
