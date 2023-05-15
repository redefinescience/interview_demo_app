package com.kotlineering.interview.domain.stocks

import com.kotlineering.interview.db.Database
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.toServiceState
import com.kotlineering.interview.domain.tryTransaction
import kotlinx.datetime.Clock

class StocksRepository(
    internal val api: StocksApi,
    internal val db: Database,
    internal val dev: DeveloperRepository
) {
    private fun updatePortfolio(
        portfolio: String, stocks: List<StockResult>, timeStamp: Long
    ) = db.tryTransaction {
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
        if (dev.refreshMode == DeveloperRepository.RefreshMode.RUNTIME_ERROR) {
            throw Exception("Developer Mode Exception")
        }
    }

    fun getPortfolio(
        portfolio: String
    ) = db.databaseQueries.getStocks(portfolio)

    suspend fun refreshPortfolio(
        portfolio: String,
        timeStamp: Long = Clock.System.now().toEpochMilliseconds()
    ) = when (dev.refreshMode) {
        DeveloperRepository.RefreshMode.MALFORMED -> api.getStocksMalformed()
        DeveloperRepository.RefreshMode.EMPTY -> api.getStocksEmpty()
        else -> api.getStocks()
    }.toServiceState { data ->
        updatePortfolio(
            portfolio, data.stocks, timeStamp
        ).toServiceState()
    }
}
