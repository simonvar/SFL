package io.github.simonvar.sfl.ui.dictaphone

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.simonvar.sfl.dictaphone.DictaphoneFeature
import io.github.simonvar.sfl.dictaphone.SamplingUtils
import io.github.simonvar.sfl.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.pow

class DictaphoneViewModel : BaseViewModel() {

    private val dictaphone = DictaphoneFeature()

    val state = MutableStateFlow<DictaphoneState>(DictaphoneState.Idle)
    val levels = MutableStateFlow<List<Int>>(emptyList())

    private var levelsCount = 0
    private val levelsHistory = mutableListOf<Int>()

    fun onChangeLevelsCount(value: Int) = launch {
        levelsCount = value
        for (i in 0 until levelsCount) {
            levelsHistory.add(0)
        }
        levels.emit(levelsHistory)
    }

    fun onPermissionGranted() {
        dictaphone.init()
    }

    fun onRecordClick() = launch {
        state.emit(DictaphoneState.Recording)
        dictaphone.record().collect { onAudioDataReceived(it) }
    }

    fun onStopClick() = launch {
        state.emit(DictaphoneState.Paused)
        dictaphone.stop()
    }

    fun onPlayClick() = launch {
        state.emit(DictaphoneState.Playing)
        dictaphone.play()
    }

    fun onPauseClick() = launch {
        state.emit(DictaphoneState.Paused)
        dictaphone.pause()
    }

    fun onResetClick() = viewModelScope.launch(Dispatchers.IO) {
        state.emit(DictaphoneState.Idle)
        dictaphone.reset()
    }

    private fun onAudioDataReceived(data: ShortArray) {
        launch {
            SamplingUtils
                .getExtremes(data, data.size / 80)
                .map { it[0] - it[1] }
                .map {
                    (it.toFloat() / (Short.MAX_VALUE * 2) * 100).toInt()
                }
                .forEach {
                    levelsHistory.add(0, it)
                    levelsHistory.removeLast()
                }

            levels.emit(levelsHistory.toList().asReversed())
        }
    }

    override fun onCleared() {
        super.onCleared()
        dictaphone.release()
    }
}
