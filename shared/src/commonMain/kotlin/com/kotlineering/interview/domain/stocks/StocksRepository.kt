package com.kotlineering.interview.domain.stocks

import com.kotlineering.interview.db.Database
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.ApiResult
import com.kotlineering.interview.domain.developer.DeveloperRepository
import kotlinx.datetime.Clock

class StocksRepository(
    private val api: StocksApi,
    private val db: Database,
    private val dev: DeveloperRepository
) {
    private fun updatePortfolio(
        portfolio: String, stocks: List<StockResult>, timeStamp: Long
    ): Throwable? = db.transactionWithResult {
        try {
            // Clear and update portfolio
            db.databaseQueries.clearPortfolioStocks(portfolio)
            db.databaseQueries.upsertPortfolio(
                name = portfolio,
                size = stocks.size.toLong(),
                update_timestamp = timeStamp
            )
            // Update individual stocks, and the portfolio list
            stocks.map {
                db.databaseQueries.upsertStock(
                    ticker = it.ticker,
                    name = it.name,
                    currency = it.currency,
                    current_price_cents = it.current_price_cents,
                    quantity = it.quantity ?: 0,
                    current_price_timestamp = it.current_price_timestamp,
                    update_timestamp = timeStamp
                )
                return@map it.ticker
            }.forEachIndexed { i, it ->
                db.databaseQueries.upsertPortfolioStock(
                    name = portfolio,
                    ticker = it,
                    sequence = i.toLong()
                )
            }
            // Throw exception if dev options indicate to do so...
            // (done here to test rollback)
            if (dev.stocksRefreshMode == DeveloperRepository.RefreshStocksMode.RUNTIME_ERROR) {
                throw Exception("Developer Mode Exception")
            }
            return@transactionWithResult null
        } catch (t: Throwable) {
            // TODO: Generic logger mapped to platform logger - log this exception
            rollback(t)
        }
    }

    fun getPortfolio(
        portfolio: String
    ) = db.databaseQueries.getStocks(portfolio)

    suspend fun refreshPortfolio(
        portfolio: String,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = when (
        // Call appropriate endpoint based on dev mode
        val result = when (dev.stocksRefreshMode) {
            DeveloperRepository.RefreshStocksMode.MALFORMED -> api.getStocksMalformed()
            DeveloperRepository.RefreshStocksMode.EMPTY -> api.getStocksEmpty()
            else -> api.getStocks()
        }
    ) {
        // Error with endpoint or payload
        is ApiResult.Error -> ServiceState.Error.Api(result.error)
        // An exception was thrown while calling remote api
        is ApiResult.Exception -> ServiceState.Error.Network(
            result.throwable.message ?: result.throwable.toString()
        )
        // Good, try to update the DB.
        is ApiResult.Success -> updatePortfolio(
            portfolio, result.data.stocks, timeStamp
        )?.let {
            // There was an exception during DB update
            ServiceState.Error.Runtime(it.message.orEmpty())
        } ?: ServiceState.Done // Good to go!
    }
}
