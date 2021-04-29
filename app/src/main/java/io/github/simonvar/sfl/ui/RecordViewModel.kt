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
import kotlinx.coroutines.launch
import kotlin.random.Random

class RecordViewModel : ViewModel(), AudioDataListener {

    private val recordFeature = DictaphoneFeature(this)

    private val _state = MutableStateFlow<RecordState>(RecordState.Idle)
    val state: Flow<RecordState> = _state

    private val _levels = MutableStateFlow<List<Int>>(emptyList())
    val levels: Flow<List<Int>> = _levels

    private var levelsCount = 0
    private val levelsHistory = mutableListOf<Int>()

    fun onChangeLevelsCount(value: Int) = viewModelScope.launch(Dispatchers.IO) {
        levelsCount = value
        for (i in 0 until levelsCount) {
            levelsHistory.add(0)
        }
        _levels.emit(levelsHistory)
    }

    fun onRecordClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Recording)
        recordFeature.record()
    }

    fun onStopClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Paused)
        recordFeature.stop()
    }

    fun onPlayClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Playing)
    }

    fun onPauseClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Paused)
    }

    fun onResetClick() = viewModelScope.launch(Dispatchers.IO) {
        _state.emit(RecordState.Idle)
    }

    override fun onAudioDataReceived(data: ShortArray) {
        viewModelScope.launch(Dispatchers.IO) {

            val extremes = SamplingUtils.getExtremes(data, data.size / 20)
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


            val l = levelsHistory.toList()
            _levels.emit(l)
        }
    }
}
