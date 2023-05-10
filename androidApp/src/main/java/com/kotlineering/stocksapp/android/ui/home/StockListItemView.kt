package com.kotlineering.stocksapp.android.ui.home

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlineering.stocksapp.android.R
import com.kotlineering.stocksapp.android.databinding.ViewStockListItemBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

class StockListItemView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : ConstraintLayout(context, attr, defStyle) {
    private val binding = ViewStockListItemBinding.inflate(
        LayoutInflater.from(context), this
    )

    var ticker: CharSequence
        get() = binding.ticker.text ?: ""
        set(v) {
            binding.ticker.text = v
        }

    var name: CharSequence
        get() = binding.name.text ?: ""
        set(v) {
            binding.name.text = v
        }

    var priceText: CharSequence
        get() = binding.price.text ?: ""
        set(v) {
            binding.price.text = v
        }

    var currency: CharSequence
        get() = binding.currency.text ?: ""
        set(v) {
            binding.currency.text = v
        }

    fun setPrice(priceInCents: Long, currency: String) {
        this.currency = currency

        // TODO: Move this to util file, and unittest it
        priceText = NumberFormat.getAvailableLocales().find {
            NumberFormat.getCurrencyInstance(it).currency?.currencyCode == currency
        }?.let { currencyLocale ->
            NumberFormat.getCurrencyInstance(currencyLocale).format(priceInCents.toDouble() / 100)
        } ?: "${priceInCents / 100}.${priceInCents % 100} ($currency)"
    }

    var date: Long = 0
        set(v) {
            field = v

            // TODO: Move this to util file, and unittest it
            binding.date.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(
                    resources.configuration.locales[0]
                ).withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(v))
            } else {
                SimpleDateFormat(
                    "dd.MM.yyyy HH:mm",
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        resources.configuration.locales[0]
                    } else {
                        resources.configuration.locale
                    }
                ).format(Date(v))
            }
        }

    var quantity: Long = 0
        set(v) {
            field = v
            binding.quantity.text = resources.takeIf { v != 0L }?.getQuantityString(
                R.plurals.num_shares, v.toInt(), v.toInt()
            )
        }
}
