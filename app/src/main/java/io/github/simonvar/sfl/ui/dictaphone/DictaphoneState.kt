package io.github.simonvar.sfl.ui.dictaphone

import android.content.res.Resources
import io.github.simonvar.sfl.R

sealed class DictaphoneState {
    object Idle : DictaphoneState()
    object Recording : DictaphoneState()
    object Paused : DictaphoneState()
    object Playing : DictaphoneState()
}

fun DictaphoneState.color(resources: Resources): Int {
    return when (this) {
        DictaphoneState.Idle -> resources.getColor(R.color.blue)
        DictaphoneState.Recording -> resources.getColor(R.color.red)
        DictaphoneState.Playing -> resources.getColor(R.color.teal)
        DictaphoneState.Paused -> resources.getColor(R.color.teal)
    }
}