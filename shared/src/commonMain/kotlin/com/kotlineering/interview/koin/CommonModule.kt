package com.kotlineering.interview.koin

import com.kotlineering.interview.db.Database
import com.kotlineering.interview.domain.developer.DeveloperRepository
import com.kotlineering.interview.domain.stocks.StocksService
import com.kotlineering.interview.domain.stocks.StocksRepository
import com.kotlineering.interview.domain.stocks.StocksApi
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
