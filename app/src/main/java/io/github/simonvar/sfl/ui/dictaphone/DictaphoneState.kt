package io.github.simonvar.sfl.ui.dictaphone

import android.content.Context
import androidx.core.content.ContextCompat
import io.github.simonvar.sfl.R

sealed class DictaphoneState {
    object Idle : DictaphoneState()
    object Recording : DictaphoneState()
    data class ReadyForPlay(val duration: Long) : DictaphoneState()
    object Paused : DictaphoneState()
    object Playing : DictaphoneState()
}

fun DictaphoneState.color(context: Context): Int {
    return when (this) {
        is DictaphoneState.Idle -> ContextCompat.getColor(context, R.color.blue)
        is DictaphoneState.Recording -> ContextCompat.getColor(context, R.color.red)
        is DictaphoneState.ReadyForPlay -> ContextCompat.getColor(context, R.color.teal)
        is DictaphoneState.Playing -> ContextCompat.getColor(context, R.color.teal)
        is DictaphoneState.Paused -> ContextCompat.getColor(context, R.color.teal)
    }
}