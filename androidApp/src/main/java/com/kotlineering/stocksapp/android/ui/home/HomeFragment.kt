package com.kotlineering.stocksapp.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.ConcatAdapter
import com.kotlineering.stocksapp.android.R
import com.kotlineering.stocksapp.android.databinding.FragmentHomeBinding
import com.kotlineering.stocksapp.domain.ServiceState
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup recycler
        binding.recycler.adapter = ConcatAdapter(
            createAndInitHeaderAdapter(),
            createAndInitStocksAdapter()
        )

        // Setup dev options
        initDevOptsView()

        return binding.root
    }

    private fun createAndInitStocksAdapter() = StocksAdapter().apply {
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
            if (viewModel.isDebug) {
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

    private fun initDevOptsView() = binding.devopts.takeIf { viewModel.isDebug }?.apply {
        onDevOptSelected = { opt ->
            viewModel.developerRepository.setStocksRefreshMode(opt)
            binding.devopts.visibility = View.GONE
        }

        viewModel.developerRepository.getStocksRefreshMode()
            .asLiveData().distinctUntilChanged().observe(
                viewLifecycleOwner,
                this::setSelectedDevOpt
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
