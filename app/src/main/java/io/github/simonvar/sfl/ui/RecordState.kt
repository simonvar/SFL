package io.github.simonvar.sfl.ui

import android.content.res.Resources
import io.github.simonvar.sfl.R

sealed class RecordState {
    object Idle : RecordState()
    object Recording : RecordState()
    object Paused : RecordState()
    object Playing : RecordState()
}

fun RecordState.color(resources: Resources): Int {
    return when (this) {
        RecordState.Idle -> resources.getColor(R.color.blue)
        RecordState.Recording -> resources.getColor(R.color.red)
        RecordState.Playing -> resources.getColor(R.color.teal)
        RecordState.Paused -> resources.getColor(R.color.teal)
    }
}