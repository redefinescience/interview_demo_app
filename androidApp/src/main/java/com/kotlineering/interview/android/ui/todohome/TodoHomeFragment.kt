package com.kotlineering.interview.android.ui.todohome

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.android.ui.home.HomeFragment
import com.kotlineering.interview.domain.developer.DeveloperRepository
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodoHomeViewModel(
    val developerRepository: DeveloperRepository
) : ViewModel()

class TodoHomeFragment : HomeFragment() {

    private val viewModel: TodoHomeViewModel by viewModel()

    override fun createAdapters(): RecyclerView.Adapter<*> = ConcatAdapter(

    )

    override fun getDeveloperRepository(): DeveloperRepository = viewModel.developerRepository
}
