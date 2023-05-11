package com.kotlineering.interview.android.ui.todo

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.db.Todos
import java.util.Collections

class TodoRecyclerAdapter : ListAdapter<Todos, TodoRecyclerAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Todos>() {
        override fun areItemsTheSame(
            oldItem: Todos, newItem: Todos
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Todos, newItem: Todos
        ): Boolean = oldItem == newItem
    }
) {
    class ViewHolder(
        context: Context
    ) : RecyclerView.ViewHolder(
        ToDoListItemView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    ) {
        val view = itemView as ToDoListItemView
    }

    var onItemClicked: ((Todos) -> Unit)? = null
    var onItemLongClicked: ((Todos) -> Unit)? = null
    var onMoveComplete: ((List<Todos>) -> Unit)? = null
    var onSwiped: ((Todos) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = ViewHolder(parent.context).apply {
        view.onClicked = { onItemClicked?.invoke(it) }
        view.onLongClicked = { onItemLongClicked?.invoke(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.item = getItem(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    var allowReorder = false

    private val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        var hasMoved = false

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int = if (viewHolder is ViewHolder) {
            makeMovementFlags(
                if (allowReorder) {
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
                } else {
                    0
                },
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        } else {
            makeMovementFlags(0, 0)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (viewHolder == null) {
                if (hasMoved) {
                    hasMoved = false
                    onMoveComplete?.invoke(currentList)
                }
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.bindingAdapterPosition
            val to = target.bindingAdapterPosition
            hasMoved = true
            submitList(
                currentList.toMutableList().apply {
                    if (from < to) {
                        for (i in from until to) {
                            Collections.swap(this, i, i + 1)
                        }
                    } else {
                        for (i in from downTo to + 1) {
                            Collections.swap(this, i, i - 1)
                        }
                    }
                }
            )
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (viewHolder is ViewHolder) {
                viewHolder.view.item?.let {
                    onSwiped?.invoke(it)
                }
            }
        }
    })
}
