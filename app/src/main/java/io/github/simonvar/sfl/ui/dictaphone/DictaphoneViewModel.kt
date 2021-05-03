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
    private var audioDuration = 0L

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
        dictaphone.stop()
        val data = dictaphone.setupPlay(levelsCount)

        audioDuration = data.duration.toLong() * 1000
        dictaphoneState.emit(DictaphoneState.ReadyForPlay(audioDuration))

        levelsHistory.clear()
        levelsHistory.addAll(data.values)

        updateWaveform(withAnimation = true)
    }

    fun onPlayClick() = launch {
        dictaphoneState.emit(DictaphoneState.Playing)
        dictaphone.play(levelsCount).collect { data ->
            levelsHistory.clear()
            levelsHistory.addAll(data.values)
            updateWaveform(withAnimation = false)
        }
    }

    fun onPlayingEnd() = launch {
        dictaphoneState.emit(DictaphoneState.ReadyForPlay(audioDuration))
        dictaphone.resetPlayback()
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

    private suspend fun updateWaveform(withAnimation: Boolean = false) {
        waveformState.emit(
            WaveView.Data(
                levels = levelsHistory.toList(),
                withAnimation = withAnimation
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        dictaphone.release()
    }
}
