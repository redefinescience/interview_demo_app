package com.kotlineering.interview.android.ui.todo

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.ui.home.HomeFragment
import com.kotlineering.interview.domain.developer.DeveloperRepository
import org.koin.androidx.viewmodel.ext.android.viewModel

class ToDoHomeFragment : HomeFragment() {

    private val viewModel: ToDoHomeViewModel by viewModel()

    override fun createAdapters(): RecyclerView.Adapter<*> = ConcatAdapter(

    )

    override fun getDeveloperRepository(): DeveloperRepository = viewModel.developerRepository
}
