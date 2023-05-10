package com.kotlineering.stocksapp.koin

import com.kotlineering.stocksapp.db.Database
import com.kotlineering.stocksapp.domain.developer.repository.DeveloperRepository
import com.kotlineering.stocksapp.domain.stocks.StocksService
import com.kotlineering.stocksapp.domain.stocks.repository.StocksRepository
import com.kotlineering.stocksapp.domain.stocks.repository.remote.StocksApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
fun commonModule() = module {
    // HTTP
    single {
        HttpClient(
            engine = get()
        ) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }
    }

    // SqlDelight DB
    single {
        Database(
            driver = get()
        )
    }

    // Stocks Domain
    single(named("stocks-api-domain")) {
        "https://storage.googleapis.com/"
    }
    single {
        StocksApi(
            client = get(),
            domain = get(named("stocks-api-domain"))
        )
    }
    single {
        StocksRepository(
            api = get(),
            db = get(),
            dev = get()
        )
    }
    single {
        StocksService(
            repository = get(),
            dispatcher = get()
        )
    }

    // Developer Mode Domain
    single {
        DeveloperRepository()
    }
}
