package com.kotlineering.interview.domain

sealed interface ServiceState {
    object Busy : ServiceState
    object Done : ServiceState
    sealed class Error(val message: String) : ServiceState {
        class Network(message: String) : Error(message)
        class Runtime(message: String) : Error(message)
        class Api(message: String) : Error(message)
    }
}
