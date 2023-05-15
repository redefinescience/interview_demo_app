package com.kotlineering.interview.android.ui.todo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.databinding.ViewTodoListItemBinding
import com.kotlineering.interview.db.Todos

class ToDoListItemView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attr, defStyle) {
    private val binding = ViewTodoListItemBinding.inflate(
        LayoutInflater.from(context), this
    ).apply {
        root.setOnClickListener { item?.let { onClicked?.invoke(it) } }
        root.setOnLongClickListener { item?.let { onLongClicked?.invoke(it) } != null }
    }

    var item: Todos? = null
        set(v) {
            field = v
            binding.text.text = v?.title
            binding.check.isChecked = v?.completed?.let { it != 0L } == true
        }

    var onClicked: ((Todos) -> Unit)? = null
    var onLongClicked: ((Todos) -> Unit)? = null
}
