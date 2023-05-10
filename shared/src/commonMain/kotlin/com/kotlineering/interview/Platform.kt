package com.kotlineering.interview

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform