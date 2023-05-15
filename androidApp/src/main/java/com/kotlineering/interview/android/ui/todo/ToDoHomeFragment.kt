package com.kotlineering.interview.android.ui.todo

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.BuildConfig
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.ui.HeaderAdapter
import com.kotlineering.interview.android.ui.home.HomeFragment
import com.kotlineering.interview.domain.ServiceState
import org.koin.androidx.viewmodel.ext.android.viewModel

class ToDoHomeFragment : HomeFragment() {

    private val viewModel: ToDoHomeViewModel by viewModel()

    override fun createAdapters(): RecyclerView.Adapter<*> = ConcatAdapter(
        createHeaderAdapter(),
        createListAdapter()
    )

    private fun showEdit(id: Long? = null) =
        EditTodoDialogFragment.newInstance(id).show(parentFragmentManager, "edit-todo")

    private fun createHeaderAdapter() = HeaderAdapter(
        ToDoHeaderView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            if (BuildConfig.DEBUG) {
                enableDevButton()
                onDevOptsClicked = {
                    showDevOpts()
                }
            }

            // Refresh button
            onRefreshClicked = {
                viewModel.refresh()
            }

            viewModel.busy.observe(viewLifecycleOwner) {
                refreshEnabled = !it
            }

            onNewClicked = {
                showEdit()
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

    private fun createListAdapter() = TodoRecyclerAdapter().also { adapter ->
        adapter.onMoveComplete = { viewModel.updateTodoList(it) }
        adapter.onSwiped = { viewModel.removeTodo(it.id) }
        adapter.onItemClicked = { showEdit(it.id) }

        viewModel.todos.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.showCompleted.observe(viewLifecycleOwner) {
            adapter.allowReorder = it
        }
    }
}
