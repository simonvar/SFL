package io.github.simonvar.sfl.ui.dictaphone

import io.github.simonvar.sfl.dictaphone.Dictaphone
import io.github.simonvar.sfl.ui.base.BaseViewModel
import io.github.simonvar.sfl.widget.WaveView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

class DictaphoneViewModel : BaseViewModel() {

    private val dictaphone = Dictaphone()

    val dictaphoneState = MutableStateFlow<DictaphoneState>(DictaphoneState.Idle)
    val waveformState = MutableStateFlow(WaveView.Data())

    private var levelsCount = 0
    private val levelsHistory = mutableListOf<Int>()

    fun onChangeLevelsCount(value: Int) = launch {
        updateLevelsCount(value)
        updateWaveform()
    }

    fun onPermissionGranted() {
        dictaphone.init()
    }

    fun onRecordClick() = launch {
        dictaphoneState.emit(DictaphoneState.Recording)
        dictaphone.record(levelsCount).collect { levels ->
            levels.values.forEach {
                levelsHistory.add(levelsHistory.size, it)
                levelsHistory.removeFirst()
            }

            updateWaveform()
        }
    }

    fun onStopClick() = launch {
        dictaphoneState.emit(DictaphoneState.Paused)
        dictaphone.stop()

        val levels = dictaphone.setupPlay(levelsCount)
        levelsHistory.clear()
        levelsHistory.addAll(levels.values)

        updateWaveform(withAnimation = true)
    }

    fun onPlayClick() = launch {
        dictaphoneState.emit(DictaphoneState.Playing)
        dictaphone.play(levelsCount).collect { levels ->
            if (levels.values.count() == levels.played) {
                dictaphoneState.emit(DictaphoneState.Paused)
                dictaphone.resetPlayback()
            } else {
                levelsHistory.clear()
                levelsHistory.addAll(levels.values)

                updateWaveform(
                    coloredTo = levels.played,
                    withAnimation = false
                )
            }
        }
    }

    fun onPauseClick() = launch {
        dictaphoneState.emit(DictaphoneState.Paused)
        dictaphone.pause()
    }

    fun onResetClick() = launch {
        dictaphoneState.emit(DictaphoneState.Idle)
        dictaphone.reset()
        levelsHistory.clear()
        resetWaveform(withAnimation = true)
    }

    private suspend fun updateLevelsCount(value: Int) {
        levelsCount = value
        resetWaveform(withAnimation = false)
    }

    private suspend fun resetWaveform(withAnimation: Boolean) {
        for (i in 0 until levelsCount) {
            levelsHistory.add(0)
        }
        updateWaveform(withAnimation = withAnimation)
    }

    private suspend fun updateWaveform(
        coloredTo: Int = levelsCount,
        withAnimation: Boolean = false
    ) {
        waveformState.emit(
            WaveView.Data(
                levels = levelsHistory.toList(),
                coloredToIndex = coloredTo,
                withAnimation = withAnimation
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        dictaphone.release()
    }
}
