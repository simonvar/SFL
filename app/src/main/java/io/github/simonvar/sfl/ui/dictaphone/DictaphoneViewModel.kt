package io.github.simonvar.sfl.ui.dictaphone

import androidx.lifecycle.viewModelScope
import io.github.simonvar.sfl.dictaphone.DictaphoneFeature
import io.github.simonvar.sfl.dictaphone.SamplingUtils
import io.github.simonvar.sfl.ui.base.BaseViewModel
import io.github.simonvar.sfl.widget.WaveView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DictaphoneViewModel : BaseViewModel() {

    private val dictaphone = DictaphoneFeature()

    val state = MutableStateFlow<DictaphoneState>(DictaphoneState.Idle)
    val data = MutableStateFlow(WaveView.Data())

    private var levelsCount = 0
    private val levelsHistory = mutableListOf<Int>()

    fun onChangeLevelsCount(value: Int) = launch {
        levelsCount = value
        for (i in 0 until levelsCount) {
            levelsHistory.add(0)
        }

        data.emit(
            WaveView.Data(levelsHistory.toList())
        )
    }

    fun onPermissionGranted() {
        dictaphone.init()
    }

    fun onRecordClick() = launch {
        state.emit(DictaphoneState.Recording)
        dictaphone.record().collect { proceedRecordedData(it) }
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
        levelsHistory.clear()
        for (i in 0 until levelsCount) {
            levelsHistory.add(0)
        }
        data.emit(
            WaveView.Data(
                levels = levelsHistory.toList(),
                withAnimation = true
            )
        )
    }

    private fun proceedRecordedData(values: ShortArray) {
        launch {
            SamplingUtils
                .getExtremes(values, values.size / 80)
                .map { it[0] - it[1] }
                .map {
                    (it.toFloat() / (Short.MAX_VALUE * 2) * 100).toInt()
                }
                .forEach {
                    levelsHistory.add(levelsHistory.size, it)
                    levelsHistory.removeFirst()
                }

            data.emit(
                WaveView.Data(levelsHistory.toList())
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        dictaphone.release()
    }
}
