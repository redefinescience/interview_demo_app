package com.kotlineering.interview.koin

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect fun platformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration? = null) = startKoin {
    appDeclaration?.invoke(this)
    modules(
        platformModule(),
        commonModule(),
        stocksModule(),
        todoModule()
    )
}
