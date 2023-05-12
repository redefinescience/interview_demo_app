package com.kotlineering.interview.android.ui.todo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlineering.interview.android.databinding.ViewTodoHeaderBinding

class ToDoHeaderView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : ConstraintLayout(context, attr, defStyle) {
    private val binding = ViewTodoHeaderBinding.inflate(
        LayoutInflater.from(context), this, true
    ).apply {
        refresh.setOnClickListener { if (it.isEnabled) onRefreshClicked?.invoke() }
        devopts.setOnClickListener { onDevOptsClicked?.invoke() }
        newTodo.setOnClickListener { onNewClicked?.invoke() }
        showCompleted.setOnCheckedChangeListener { _, isChecked ->
            onShowCompletedToggled?.invoke(isChecked)
        }
    }

    var onRefreshClicked: (() -> Unit)? = null
    var onDevOptsClicked: (() -> Unit)? = null
    var onNewClicked: (() -> Unit)? = null
    var onShowCompletedToggled: ((Boolean) -> Unit)? = null

    var refreshEnabled: Boolean
        get() = binding.refresh.isEnabled
        set(value) {
            binding.refresh.isEnabled = value
        }

    var error: CharSequence
        get() = binding.error.text ?: ""
        set(v) {
            binding.error.text = v
        }

    fun enableDevButton() {
        binding.devopts.visibility = View.VISIBLE
    }
}
