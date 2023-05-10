package com.kotlineering.interview.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.BuildConfig
import com.kotlineering.interview.android.databinding.FragmentHomeBinding
import com.kotlineering.interview.domain.developer.DeveloperRepository

abstract class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    protected val binding get() = _binding!!

    protected abstract fun createAdapters(): RecyclerView.Adapter<*>

    protected abstract fun getDeveloperRepository() : DeveloperRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup recycler
        binding.recycler.adapter = createAdapters()

        // Dev options
        initDevOptsView(getDeveloperRepository())

        return binding.root
    }

    private fun initDevOptsView(developerRepository: DeveloperRepository) = binding.devopts.takeIf {
        BuildConfig.DEBUG
    }?.apply {
        onDevOptSelected = { opt ->
            developerRepository.setStocksRefreshMode(opt)
            binding.devopts.visibility = View.GONE
        }

        developerRepository.getStocksRefreshMode()
            .asLiveData().distinctUntilChanged().observe(viewLifecycleOwner) {
                setSelectedDevOpt(it)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}