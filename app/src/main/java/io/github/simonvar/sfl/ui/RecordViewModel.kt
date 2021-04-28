package io.github.simonvar.sfl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.simonvar.sfl.record.AudioDataListener
import io.github.simonvar.sfl.record.DictaphoneFeature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RecordViewModel : ViewModel(), AudioDataListener {

    private val recordFeature = DictaphoneFeature(this)

    private val _state = MutableStateFlow<RecordState>(RecordState.Idle)
    val state: Flow<RecordState> = _state

    private val _levels = MutableStateFlow<List<Int>>(emptyList())
    val levels: Flow<List<Int>> = _levels

    private var levelsCount = 0

    fun onChangeLevelsCount(value: Int) = viewModelScope.launch {
        levelsCount = value
        val l = List(levelsCount) { 0 }
        _levels.emit(l)
    }

    fun onRecordClick() = viewModelScope.launch {
        _state.emit(RecordState.Recording)
        recordFeature.record()
    }

    fun onStopClick() = viewModelScope.launch {
        _state.emit(RecordState.Paused)
        recordFeature.stop()
    }

    fun onPlayClick() = viewModelScope.launch {
        _state.emit(RecordState.Playing)
    }

    fun onPauseClick() = viewModelScope.launch {
        _state.emit(RecordState.Paused)
    }

    fun onResetClick() = viewModelScope.launch {
        _state.emit(RecordState.Idle)
    }

    override fun onAudioDataReceived(data: ShortArray) {
        viewModelScope.launch {
            val random = Random(System.currentTimeMillis())

            val l = List(levelsCount) { 0 }

            _levels.emit(l)
        }
    }
}
