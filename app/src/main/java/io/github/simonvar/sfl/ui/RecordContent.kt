package io.github.simonvar.sfl.ui

import android.content.res.Resources
import io.github.simonvar.sfl.R

sealed class RecordContent {
    object Idle : RecordContent()
    object Record : RecordContent()
    object Pause : RecordContent()
    object Play : RecordContent()
}

fun RecordContent.color(resources: Resources): Int {
    return when (this) {
        RecordContent.Idle -> resources.getColor(R.color.blue)
        RecordContent.Record -> resources.getColor(R.color.red)
        RecordContent.Play -> resources.getColor(R.color.teal)
        RecordContent.Pause -> resources.getColor(R.color.teal)
    }
}