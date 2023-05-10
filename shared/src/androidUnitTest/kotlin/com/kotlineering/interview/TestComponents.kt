package com.kotlineering.interview

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kotlineering.interview.db.Database
import com.kotlineering.interview.domain.stocks.StocksApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class TestComponents {
    companion object {
        fun getDatabase() = Database(
            JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                Database.Schema.create(it)
            }
        )

        fun getClient() = HttpClient(CIO) {
            engine {
                // this: CIOEngineConfig
                maxConnectionsCount = 1000
                endpoint {
                    // this: EndpointConfig
                    maxConnectionsPerRoute = 100
                    pipelineMaxSize = 20
                    keepAliveTime = 5000
                    connectTimeout = 5000
                    connectAttempts = 5
                }
            }
            @OptIn(ExperimentalSerializationApi::class)
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }

        fun getStocksApi() = StocksApi(getClient(), "https://storage.googleapis.com/")
    }
}
