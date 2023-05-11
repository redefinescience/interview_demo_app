package com.kotlineering.interview.android.ui.stocks

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.databinding.ViewStockListItemBinding
import com.kotlineering.interview.db.GetStocks
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

class StockListItemView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attr, defStyle) {
    private val binding = ViewStockListItemBinding.inflate(
        LayoutInflater.from(context), this
    )

    var item: GetStocks? = null
        set(v) {
            field = v
            binding.ticker.text = v?.ticker
            binding.name.text = v?.name
            binding.date.text = v?.let { dateString(it.current_price_timestamp) }
            binding.currency.text = v?.currency
            binding.price.text = v?.let { priceString(it.current_price_cents, it.currency) }
            binding.quantity.text = v?.quantity?.let { q ->
                resources.takeIf { q != 0L }?.getQuantityString(
                    R.plurals.num_shares, q.toInt(), q.toInt()
                )
            }
        }

    // TODO: Move this to util file, and unittest it
    private fun priceString(priceInCents: Long, currency: String) =
        NumberFormat.getAvailableLocales().find {
            NumberFormat.getCurrencyInstance(it).currency?.currencyCode == currency
        }?.let { currencyLocale ->
            NumberFormat.getCurrencyInstance(currencyLocale).format(priceInCents.toDouble() / 100)
        } ?: "${priceInCents / 100}.${priceInCents % 100} ($currency)"

    // TODO: Move this to util file, and unittest it
    private fun dateString(date: Long) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(
            resources.configuration.locales[0]
        ).withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(date))
    } else {
        SimpleDateFormat(
            "dd.MM.yyyy HH:mm",
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resources.configuration.locales[0]
            } else {
                resources.configuration.locale
            }
        ).format(Date(date))
    }
}
