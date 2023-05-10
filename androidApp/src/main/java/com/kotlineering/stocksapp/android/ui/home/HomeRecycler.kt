package com.kotlineering.stocksapp.android.ui.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlineering.stocksapp.db.GetStocks

class HeaderAdapter(private val view: View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = object : RecyclerView.ViewHolder(view) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    override fun getItemCount() = 1
}

class StockViewHolder(
    context: Context
) : RecyclerView.ViewHolder(
    StockListItemView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
) {
    val stockView = itemView as StockListItemView
}

class StocksAdapter : ListAdapter<GetStocks, StockViewHolder>(
    object : DiffUtil.ItemCallback<GetStocks>() {
        override fun areItemsTheSame(
            oldItem: GetStocks, newItem: GetStocks
        ): Boolean = oldItem.ticker == newItem.ticker

        override fun areContentsTheSame(
            oldItem: GetStocks, newItem: GetStocks
        ): Boolean = oldItem == newItem
    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = StockViewHolder(parent.context)

    override fun onBindViewHolder(
        holder: StockViewHolder, position: Int
    ) {
        getItem(position).let { stock ->
            holder.stockView.apply {
                ticker = stock.ticker
                name = stock.name
                date = stock.current_price_timestamp
                setPrice(stock.current_price_cents, stock.currency)
                quantity = stock.quantity
            }
        }
    }
}
