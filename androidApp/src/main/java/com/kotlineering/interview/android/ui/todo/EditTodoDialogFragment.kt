package com.kotlineering.interview.android.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.databinding.DialogFragmentEditBinding
import com.kotlineering.interview.domain.ServiceState
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditTodoDialogFragment : DialogFragment() {

    companion object {
        private const val KEY_ID = "key"
        fun newInstance(id: Long?) = EditTodoDialogFragment().apply {
            arguments = bundleOf(KEY_ID to id)
        }
    }

    private var _binding: DialogFragmentEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditTodoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentEditBinding.inflate(
            inflater, container, false
        )
        arguments?.getLong(KEY_ID)?.let { viewModel.setTodoId(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            viewModel.saveTodo(
                binding.editText.text?.toString() ?: "",
                binding.checkboxComplete.isChecked
            )
        }

        viewModel.isSaved.observe(viewLifecycleOwner) {
            if (it) {
                dismiss()
            }
        }

        viewModel.todo.observe(viewLifecycleOwner) {
            it?.let {
                binding.title.text = resources.getText(R.string.edit)
                binding.editText.setText(it.title)
                binding.checkboxComplete.isChecked = it.completed != 0L
            }
            binding.checkboxComplete.isVisible = it != null
            binding.editText.requestFocus()
        }

        viewModel.isBusy.observe(viewLifecycleOwner) {
            binding.editText.isEnabled = !it
            binding.buttonSave.isEnabled = !it
            binding.checkboxComplete.isEnabled = !it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.error.text = when (it) {
                is ServiceState.Error.Api -> resources.getString(R.string.error_api)
                is ServiceState.Error.Runtime -> resources.getString(R.string.error_runtime)
                is ServiceState.Error.Network -> resources.getString(R.string.error_network)
                else -> ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}