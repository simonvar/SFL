package io.github.simonvar.sfl.dictaphone.audioplayback

import kotlinx.coroutines.flow.Flow

interface AudioPlaybackFeature {

    fun init(sampleRate: Int)

    fun setup(buffer: ShortArray): PlaybackData

    fun play(): Flow<PlaybackData>

    fun pause()

    fun reset()

    fun release()
}