package com.kotlineering.interview.android.ui.stocks

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.kotlineering.interview.android.databinding.ViewPortfolioHeaderBinding

class PortfolioHeaderView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attr, defStyle) {
    private val binding = ViewPortfolioHeaderBinding.inflate(
        LayoutInflater.from(context), this
    ).apply {
        refresh.setOnClickListener { if (it.isEnabled) onRefreshClicked?.invoke() }
        devopts.setOnClickListener { onDevOptsClicked?.invoke() }
        searchField.doOnTextChanged { text, _, _, _ -> onSearchChanged?.invoke(text.toString()) }
    }

    var onRefreshClicked: (() -> Unit)? = null
    var onDevOptsClicked: (() -> Unit)? = null
    var onSearchChanged: ((String) -> Unit)? = null

    var name: CharSequence
        get() = binding.name.text ?: ""
        set(v) {
            binding.name.text = v
        }

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
