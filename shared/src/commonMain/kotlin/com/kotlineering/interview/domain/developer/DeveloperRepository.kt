package com.kotlineering.interview.domain.developer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DeveloperRepository {
    enum class RefreshMode {
        NORMAL,
        MALFORMED,
        EMPTY,
        RUNTIME_ERROR
    }

    private var refreshModeFlow = MutableStateFlow(RefreshMode.NORMAL)

    fun setRefreshMode(mode: RefreshMode) {
        refreshModeFlow.value = mode
    }

    fun getRefreshMode(): Flow<RefreshMode> = refreshModeFlow

    val refreshMode: RefreshMode
        get() = refreshModeFlow.value
}
