package com.kotlineering.interview.koin

import com.kotlineering.interview.db.Database
import com.kotlineering.interview.domain.developer.DeveloperRepository
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
    single(named("runtime")) {
        Database(
            driver = get(named("runtime"))
        )
    }

    // Developer Mode Domain
    single {
        DeveloperRepository()
    }
}
