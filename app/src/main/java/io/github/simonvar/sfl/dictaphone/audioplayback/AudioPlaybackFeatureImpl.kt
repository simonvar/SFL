package io.github.simonvar.sfl.dictaphone.audioplayback

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.min

class AudioPlaybackFeatureImpl : AudioPlaybackFeature {

    companion object {
        private const val LOG_TAG = "AudioPlaybackFeature"
    }

    private var track: AudioTrack? = null

    private var sampleRate = 8000
    private var bufferSize = 0
    private var bufferForPlay = ShortArray(0)
    private var playedOffset = 0

    override fun init(sampleRate: Int) {
        this.sampleRate = sampleRate

        bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = sampleRate * 2
        }

        track = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        if (track?.state != AudioTrack.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Track can't initialize!")
            throw IllegalStateException("Audio Track can't initialize!")
        }
    }

    override fun setup(buffer: ShortArray): PlaybackData {
        bufferForPlay = buffer
        return PlaybackData(bufferForPlay, 0)
    }

    override fun play(): Flow<PlaybackData> {
        val track = track ?: throw IllegalStateException("Audio Track need initialize!")

        track.play()
        return flow {
            while (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                val lastIndex = min(playedOffset + 1, bufferForPlay.size)
                val buffer = bufferForPlay.copyOfRange(playedOffset, lastIndex)
                val wrote = track.write(buffer, 0, buffer.size)
                playedOffset += wrote

                emit(PlaybackData(bufferForPlay, playedOffset))
            }
        }

    }

    override fun pause() {
        track?.pause()
    }

    override fun reset() {
        track?.pause()
        playedOffset = 0
    }

    override fun release() {
        track?.release()
    }
}