package com.kotlineering.interview.android.ui.stocks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asFlow
import com.kotlineering.interview.db.GetStocks
import com.kotlineering.interview.domain.stocks.StocksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class StocksViewModelTest {
    val testData =  listOf(
        GetStocks(
            "ABC",
            "ABC Co",
            "USD",
            55,
            2,
            23523,
            1341,
            "",
            "",
            0
        ),
        GetStocks(
            "AB",
            "AB Co",
            "USD",
            55,
            2,
            23523,
            1341,
            "",
            "",
            1
        ),
        GetStocks(
            "COOL Stuff",
            "COOL Co",
            "USD",
            55,
            2,
            23523,
            1341,
            "",
            "",
            2
        )
    )
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `tests that tests work`() = runBlocking {
        val mockService: StocksService = mock {
            onGeneric { getStocks() }.doReturn(
                MutableStateFlow(testData.toList())
            )
        }
        val vm = StocksViewModel(mockService)

        assert(vm.stocks.asFlow().first() == testData)

        vm.setFilter("")
    }
}
