package io.github.simonvar.sfl.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.simonvar.sfl.dictophone.AudioDataListener
import io.github.simonvar.sfl.dictophone.DictaphoneFeature
import io.github.simonvar.sfl.dictophone.SamplingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecordViewModel : ViewModel(), AudioDataListener {

    private val dictaphone = DictaphoneFeature(this)

    private val _state = MutableStateFlow<RecordState>(RecordState.Idle)
    val state: Flow<RecordState> = _state.asStateFlow()

    private val _levels = MutableStateFlow<List<Int>>(emptyList())
    val levels: Flow<List<Int>> = _levels.asStateFlow()

    private var levelsCount = 0
    private val levelsHistory = mutableListOf<Int>()

    init {
        dictaphone.init()
    }

    fun onChangeLevelsCount(value: Int) = viewModelScope.launch(Dispatchers.IO) {
        levelsCount = value
        for (i in 0 until levelsCount) {
            levelsHistory.add(0)
        }
        _levels.emit(levelsHistory)
    }

    fun onRecordClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Recording)
        dictaphone.record().collect { onAudioDataReceived(it) }
    }

    fun onStopClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Paused)
        dictaphone.stop()
    }

    fun onPlayClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Playing)
        dictaphone.play()
    }

    fun onPauseClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Paused)
        dictaphone.pause()
    }

    fun onResetClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Idle)
        dictaphone.reset()
    }

    override fun onAudioDataReceived(data: ShortArray) {
        viewModelScope.launch(Dispatchers.IO) {

            val extremes = SamplingUtils.getExtremes(data, data.size / 100)
            val heights = extremes.map { it[0] - it[1] }
//            Log.d("Wave", heights.joinToString())

            val levels = heights
                .map {
                    (it.toFloat() / (Short.MAX_VALUE * 2) * 100).toInt()
                }

            for (level in levels) {
                levelsHistory.add(0, level)
                levelsHistory.removeLast()
            }

            Log.d("Levels", levels.joinToString())
            Log.d("Levels History", levelsHistory.joinToString())

            val l = levelsHistory.toList().asReversed()
            _levels.emit(l)
        }
    }

    override fun onCleared() {
        super.onCleared()
        dictaphone.release()
    }
}
