package com.kotlineering.interview.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.BuildConfig
import com.kotlineering.interview.android.databinding.FragmentHomeBinding
import com.kotlineering.interview.domain.developer.DeveloperRepository

abstract class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    protected val binding get() = _binding!!

    protected abstract fun createAdapters(): RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup recycler
        binding.recycler.apply {
            adapter = createAdapters()
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }

        return binding.root
    }

    fun showDevOpts() = DevOptsDialogFragment().show(parentFragmentManager, "dev-opts")

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
