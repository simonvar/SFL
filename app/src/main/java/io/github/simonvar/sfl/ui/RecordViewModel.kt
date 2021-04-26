package io.github.simonvar.sfl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordViewModel : ViewModel() {

    private val _state = MutableStateFlow<RecordContent>(RecordContent.Idle)
    val state: Flow<RecordContent> = _state

    fun onRecordClick() = viewModelScope.launch {
        _state.emit(RecordContent.Record)
    }

    fun onStopClick() = viewModelScope.launch {
        _state.emit(RecordContent.Pause)
    }

    fun onPlayClick() = viewModelScope.launch {
        _state.emit(RecordContent.Play)
    }

    fun onPauseClick() = viewModelScope.launch {
        _state.emit(RecordContent.Pause)
    }

    fun onResetClick() = viewModelScope.launch {
        _state.emit(RecordContent.Idle)
    }
}
