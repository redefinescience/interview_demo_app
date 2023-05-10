package com.kotlineering.interview.android.ui.stocks

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.BuildConfig
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.ui.HeaderAdapter
import com.kotlineering.interview.android.ui.home.HomeFragment
import com.kotlineering.interview.domain.ServiceState
import com.kotlineering.interview.domain.developer.DeveloperRepository
import org.koin.androidx.viewmodel.ext.android.viewModel

class StocksHomeFragment : HomeFragment() {

    private val viewModel: StocksViewModel by viewModel()
    override fun createAdapters(): RecyclerView.Adapter<*> = ConcatAdapter(
        createAndInitHeaderAdapter(),
        createAndInitStocksAdapter()
    )

    override fun getDeveloperRepository(): DeveloperRepository = viewModel.developerRepository

    private fun createAndInitStocksAdapter() = StocksRecyclerAdapter().apply {
        viewModel.stocks.observe(viewLifecycleOwner) {
            submitList(it)
        }
    }

    private fun createAndInitHeaderAdapter() = HeaderAdapter(
        PortfolioHeaderView(requireContext()).apply {
            // Setup header layout/visuals
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            name = resources.getString(
                R.string.default_portfolio
            )

            // Developer mode options
            if (BuildConfig.DEBUG) {
                enableDevButton()
                onDevOptsClicked = {
                    binding.devopts.visibility = View.VISIBLE
                }
            }

            // Refresh button
            onRefreshClicked = {
                viewModel.refreshStocks()
            }

            viewModel.refreshing.observe(viewLifecycleOwner) {
                refreshEnabled = !it
            }

            // Error visual
            viewModel.error.observe(viewLifecycleOwner) {
                error = when (it) {
                    is ServiceState.Error.Api -> resources.getString(R.string.error_api)
                    is ServiceState.Error.Runtime -> resources.getString(R.string.error_runtime)
                    is ServiceState.Error.Network -> resources.getString(R.string.error_network)
                    else -> ""
                }
            }
        }
    )
}
