package com.kotlineering.interview.android.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.kotlineering.interview.android.R
import com.kotlineering.interview.android.databinding.ViewDevOptsBinding
import com.kotlineering.interview.domain.developer.DeveloperRepository

class DevOptsView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyle: Int = 0
) : CardView(context, attr, defStyle) {

    private val binding = ViewDevOptsBinding.inflate(
        LayoutInflater.from(context), this
    ).apply {
        radiogroup.setOnCheckedChangeListener { _, checkedId ->
            onDevOptSelected?.invoke(
                when (checkedId) {
                    R.id.devempty -> DeveloperRepository.RefreshMode.EMPTY
                    R.id.devmalformed -> DeveloperRepository.RefreshMode.MALFORMED
                    R.id.devruntimeerror -> DeveloperRepository.RefreshMode.RUNTIME_ERROR
                    else -> DeveloperRepository.RefreshMode.NORMAL
                }
            )
        }
    }

    var onDevOptSelected: ((DeveloperRepository.RefreshMode) -> Unit)? = null

    fun setSelectedDevOpt(
        opt: DeveloperRepository.RefreshMode
    ) = binding.radiogroup.check(
        when (opt) {
            DeveloperRepository.RefreshMode.EMPTY -> R.id.devempty
            DeveloperRepository.RefreshMode.MALFORMED -> R.id.devmalformed
            DeveloperRepository.RefreshMode.RUNTIME_ERROR -> R.id.devruntimeerror
            else -> R.id.devnormal
        }
    )
}
