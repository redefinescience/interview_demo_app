package com.kotlineering.stocksapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform