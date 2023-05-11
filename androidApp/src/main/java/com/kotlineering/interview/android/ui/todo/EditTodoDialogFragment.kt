package com.kotlineering.interview.android.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.kotlineering.interview.android.databinding.DialogFragmentEditBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditTodoDialogFragment(
    private val todoId: Long? = null
) : DialogFragment() {
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
        todoId?.let { viewModel.setTodoId(it) }
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
            binding.checkboxComplete.isVisible = it != null
            it?.let {
                binding.editText.setText(it.title)
                binding.checkboxComplete.isChecked = it.completed != 0L
            }
            binding.editText.requestFocus()
        }

        viewModel.isBusy.observe(viewLifecycleOwner) {
            binding.editText.isEnabled = !it
            binding.buttonSave.isEnabled = !it
            binding.checkboxComplete.isEnabled = !it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}