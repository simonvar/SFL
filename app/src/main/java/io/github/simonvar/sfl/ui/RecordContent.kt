package io.github.simonvar.sfl.ui

sealed class RecordContent {
    object Idle : RecordContent()
    object Record : RecordContent()
    object Play : RecordContent()
}