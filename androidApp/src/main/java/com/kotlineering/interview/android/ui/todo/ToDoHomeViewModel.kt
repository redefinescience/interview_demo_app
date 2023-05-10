package com.kotlineering.interview.android.ui.todo

import androidx.lifecycle.ViewModel
import com.kotlineering.interview.domain.developer.DeveloperRepository

class ToDoHomeViewModel(
    val developerRepository: DeveloperRepository
) : ViewModel()