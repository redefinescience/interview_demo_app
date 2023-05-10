package com.kotlineering.stocksapp.domain.developer.repository

import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class DeveloperRepositoryTests {

    private val repository by lazy { DeveloperRepository() }

    @Test
    fun `default refresh mode should be normal`() = runBlocking {
        expect(DeveloperRepository.RefreshStocksMode.NORMAL) {
            repository.getStocksRefreshMode().firstOrNull()
        }
    }

    @Test
    fun `when refresh mode is set accessor and flow update`() = runBlocking {
        repository.setStocksRefreshMode(
            DeveloperRepository.RefreshStocksMode.MALFORMED
        )

        expect(DeveloperRepository.RefreshStocksMode.MALFORMED) {
            repository.getStocksRefreshMode().firstOrNull()
        }

        expect(DeveloperRepository.RefreshStocksMode.MALFORMED) {
            repository.stocksRefreshMode
        }
    }
}
