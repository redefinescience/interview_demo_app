package com.kotlineering.interview.android.ui.todo

import android.util.Log
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

class ToDoHomeFragment : HomeFragment() {

    private val viewModel: ToDoHomeViewModel by viewModel()

    override fun createAdapters(): RecyclerView.Adapter<*> = ConcatAdapter(
        HeaderAdapter(
            ToDoHeaderView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                if (BuildConfig.DEBUG) {
                    enableDevButton()
                    onDevOptsClicked = {
                        binding.devopts.visibility = View.VISIBLE
                    }
                }

                // Refresh button
                onRefreshClicked = {
                    viewModel.refresh()
                }

                viewModel.refreshing.observe(viewLifecycleOwner) {
                    refreshEnabled = !it
                }

                onNewClicked = {
                    EditTodoDialogFragment().show(parentFragmentManager, "new-note")
                }

                onShowCompletedToggled = {
                    viewModel.setShowCompleted(it)
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
    ).also {
        viewModel.todos.observe(viewLifecycleOwner) {
            Log.d("CHRIS", "$it")
        }
    }

    override fun getDeveloperRepository(): DeveloperRepository = viewModel.developerRepository
}
