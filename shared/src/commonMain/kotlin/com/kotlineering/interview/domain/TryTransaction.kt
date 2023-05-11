package com.kotlineering.interview.domain

import com.kotlineering.interview.db.Database

data class TryTransactionResult(
    val result: Throwable? = null
)

fun Database.tryTransaction(call: () -> Unit) = this.transactionWithResult {
    try {
        call.invoke()
        return@transactionWithResult TryTransactionResult()
    } catch (t: Throwable) {
        // TODO: Generic logger mapped to platform logger - log this exception
        rollback(
            TryTransactionResult(t)
        )
    }
}

suspend fun TryTransactionResult.toServiceState(onDone: (suspend () -> ServiceState)? = null) = result?.let {
    // There was an exception during DB update
    ServiceState.Error.Runtime(it.message.orEmpty())
} ?: onDone?.invoke() ?: ServiceState.Done
