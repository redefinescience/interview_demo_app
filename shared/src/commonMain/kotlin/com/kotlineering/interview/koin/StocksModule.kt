package com.kotlineering.interview.koin

import com.kotlineering.interview.domain.stocks.StocksApi
import com.kotlineering.interview.domain.stocks.StocksRepository
import com.kotlineering.interview.domain.stocks.StocksService
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun stocksModule() = module {
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
}
