package com.kotlineering.interview.domain.developer.repository

import com.kotlineering.interview.domain.developer.DeveloperRepository
import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class DeveloperRepositoryTests {

    private val repository by lazy { DeveloperRepository() }

    @Test
    fun `default refresh mode should be normal`() = runBlocking {
        expect(DeveloperRepository.RefreshMode.NORMAL) {
            repository.getRefreshMode().firstOrNull()
        }
    }

    @Test
    fun `when refresh mode is set accessor and flow update`() = runBlocking {
        repository.setRefreshMode(
            DeveloperRepository.RefreshMode.MALFORMED
        )

        expect(DeveloperRepository.RefreshMode.MALFORMED) {
            repository.getRefreshMode().firstOrNull()
        }

        expect(DeveloperRepository.RefreshMode.MALFORMED) {
            repository.refreshMode
        }
    }
}
