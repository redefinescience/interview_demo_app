package com.kotlineering.interview.android.ui.stocks

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.interview.db.GetStocks

class StocksRecyclerAdapter : ListAdapter<GetStocks, StocksRecyclerAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GetStocks>() {
        override fun areItemsTheSame(
            oldItem: GetStocks, newItem: GetStocks
        ): Boolean = oldItem.ticker == newItem.ticker

        override fun areContentsTheSame(
            oldItem: GetStocks, newItem: GetStocks
        ): Boolean = oldItem == newItem
    }
) {
    class ViewHolder(
        context: Context
    ) : RecyclerView.ViewHolder(
        StockListItemView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    ) {
        val view = itemView as StockListItemView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = ViewHolder(parent.context)

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        getItem(position).let { stock ->
            holder.view.apply {
                ticker = stock.ticker
                name = stock.name
                date = stock.current_price_timestamp
                setPrice(stock.current_price_cents, stock.currency)
                quantity = stock.quantity
            }
        }
    }
}
