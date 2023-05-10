package com.kotlineering.interview.domain.stocks

import com.kotlineering.interview.domain.ApiResult
import com.kotlineering.interview.domain.RemoteApi
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

@Serializable
data class StockResult(
    val ticker: String,
    val name: String,
    val currency: String,
    val current_price_cents: Long,
    val quantity: Long?,
    val current_price_timestamp: Long
)

@Serializable
data class StocksResult(
    val stocks: List<StockResult>
)

class StocksApi(
    private val client: HttpClient,
    domain: String
) : RemoteApi(domain) {

    suspend fun getStocks() = ApiResult.call<StocksResult> {
        client.fetch("cash-homework/cash-stocks-api/portfolio.json")
    }

    suspend fun getStocksEmpty() = ApiResult.call<StocksResult> {
        client.fetch("cash-homework/cash-stocks-api/portfolio_empty.json")
    }

    suspend fun getStocksMalformed() = ApiResult.call<StocksResult> {
        client.fetch("cash-homework/cash-stocks-api/portfolio_malformed.json")
    }
}
