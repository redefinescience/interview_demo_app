package com.kotlineering.interview.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import com.kotlineering.interview.android.databinding.DialogFragmentDevoptsBinding
import com.kotlineering.interview.domain.developer.DeveloperRepository
import org.koin.androidx.viewmodel.ext.android.viewModel

class DevOptsViewModel(
    val developerRepository: DeveloperRepository
) : ViewModel()

class DevOptsDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentDevoptsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DevOptsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentDevoptsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.devopts.apply {
            setSelectedDevOpt(viewModel.developerRepository.refreshMode)
            onDevOptSelected = { opt ->
                viewModel.developerRepository.setRefreshMode(opt)
                this@DevOptsDialogFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
