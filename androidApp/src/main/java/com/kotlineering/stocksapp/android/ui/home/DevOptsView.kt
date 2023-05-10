package com.kotlineering.stocksapp.android.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.kotlineering.stocksapp.android.R
import com.kotlineering.stocksapp.android.databinding.ViewDevOptsBinding
import com.kotlineering.stocksapp.domain.developer.repository.DeveloperRepository

class DevOptsView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : CardView(context, attr, defStyle) {

    private val binding = ViewDevOptsBinding.inflate(
        LayoutInflater.from(context), this
    ).apply {
        radiogroup.setOnCheckedChangeListener { _, checkedId ->
            onDevOptSelected?.invoke(
                when (checkedId) {
                    R.id.devempty -> DeveloperRepository.RefreshStocksMode.EMPTY
                    R.id.devmalformed -> DeveloperRepository.RefreshStocksMode.MALFORMED
                    R.id.devruntimeerror -> DeveloperRepository.RefreshStocksMode.RUNTIME_ERROR
                    else -> DeveloperRepository.RefreshStocksMode.NORMAL
                }
            )
        }
    }

    var onDevOptSelected: ((DeveloperRepository.RefreshStocksMode) -> Unit)? = null

    fun setSelectedDevOpt(
        opt: DeveloperRepository.RefreshStocksMode
    ) = binding.radiogroup.check(
        when (opt) {
            DeveloperRepository.RefreshStocksMode.EMPTY -> R.id.devempty
            DeveloperRepository.RefreshStocksMode.MALFORMED -> R.id.devmalformed
            DeveloperRepository.RefreshStocksMode.RUNTIME_ERROR -> R.id.devruntimeerror
            else -> R.id.devnormal
        }
    )
}
