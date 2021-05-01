package io.github.simonvar.sfl.dictaphone

import android.media.*
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.AudioTrack.PLAYSTATE_PLAYING
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.min

class DictaphoneFeature {

    companion object {
        private const val LOG_TAG = "DictaphoneFeature"
        private const val SAMPLE_RATE = 8000
    }

    private lateinit var record: AudioRecord
    private lateinit var track: AudioTrack

    private var bufferSize = 0
    private var playedOffset = 0
    private var recorded = mutableListOf<Short>()

    fun init() {
        bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }

        record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!")
            throw IllegalStateException("Audio Record can't initialize!")
        }

        track = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        if (record.state != AudioTrack.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Track can't initialize!")
            throw IllegalStateException("Audio Track can't initialize!")
        }

    }

    fun record(): Flow<ShortArray> {
        val buffer = ShortArray(bufferSize)
        record.startRecording()

        return flow {
            while (record.recordingState == RECORDSTATE_RECORDING) {
                record.read(buffer, 0, buffer.size)
                recorded.addAll(buffer.asList())
                emit(buffer)
            }
        }
    }

    fun stop() {
        record.stop()
    }

    fun play() {
        track.play()
        while (track.playState == PLAYSTATE_PLAYING) {
            val buffer = recorded.subList(playedOffset, min(playedOffset + bufferSize, recorded.size)).toShortArray()
            val wrote = track.write(buffer, 0, buffer.size)
            playedOffset += wrote
        }
    }

    fun pause() {
        track.pause()
    }

    fun reset() {
        track.stop()
        playedOffset = 0
        recorded.clear()
    }

    fun release() {
        record.release()
        track.release()
    }
}
