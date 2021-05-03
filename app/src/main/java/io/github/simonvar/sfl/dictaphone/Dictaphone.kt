package io.github.simonvar.sfl.dictaphone

import android.util.Log
import io.github.simonvar.sfl.dictaphone.audioplayback.AudioPlaybackFeatureImpl
import io.github.simonvar.sfl.dictaphone.audiorecord.AudioRecordFeatureImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Dictaphone {

    companion object {
        private const val SAMPLE_RATE = 8000
    }

    private val playbackFeature = AudioPlaybackFeatureImpl()
    private val recordFeature = AudioRecordFeatureImpl()

    private val recordedBuffer = mutableListOf<Short>()

    fun init() {
        recordFeature.init(SAMPLE_RATE)
        playbackFeature.init(SAMPLE_RATE)
    }

    fun record(levelsCount: Int): Flow<LevelsData> {
        return recordFeature.record(levelsCount)
            .map { values ->
                recordedBuffer.addAll(values.toList())

                val levels = SamplingUtils
                    .getExtremes(values, values.size / 80)
                    .map { it[0] - it[1] }
                    .map { (it.toFloat() / (Short.MAX_VALUE * 2) * 100).toInt() }

                LevelsData(levels)
            }
    }

    fun stop() {
        recordFeature.stop()
    }

    fun setupPlay(levelsCount: Int): LevelsData {
        val data = playbackFeature.setup(recordedBuffer.toShortArray())

        val levels = SamplingUtils
            .getExtremes(data.buffer, levelsCount)
            .map { it[0] - it[1] }
            .map { (it.toFloat() / (Short.MAX_VALUE * 2) * 100).toInt() }

        val playedBuffer = data.buffer.copyOfRange(0, data.offset)

        val played = levels.size - SamplingUtils
            .getExtremes(playedBuffer, levelsCount)
            .map { it[0] - it[1] }
            .count()

        return LevelsData(levels, played)
    }

    fun play(levelsCount: Int): Flow<LevelsData> {
        return playbackFeature.play()
            .map { data ->
                val levels = SamplingUtils
                    .getExtremes(data.buffer, levelsCount)
                    .map { it[0] - it[1] }
                    .map { (it.toFloat() / (Short.MAX_VALUE * 2) * 100).toInt() }

                val played = (levels.count() * (data.offset.toFloat() / data.buffer.count())).toInt()

                Log.d("Dict", "${levels.count()} * ${data.offset} / ${data.buffer.count()} = $played")
                LevelsData(levels, played)
            }
    }

    fun pause() {
        playbackFeature.pause()
    }

    fun resetPlayback() {
        playbackFeature.reset()
    }

    fun reset() {
        recordedBuffer.clear()
        recordFeature.reset()
        playbackFeature.reset()
    }

    fun release() {
        recordFeature.release()
        playbackFeature.release()
    }
}