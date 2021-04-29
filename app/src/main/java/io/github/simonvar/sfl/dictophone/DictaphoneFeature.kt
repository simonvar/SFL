package io.github.simonvar.sfl.dictophone

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log


class DictaphoneFeature(private val listener: AudioDataListener) {

    companion object {
        private const val LOG_TAG = "RecordFeature"
        private const val SAMPLE_RATE = 8000
    }

    private var shouldContinue = true

    private lateinit var record: AudioRecord
    private lateinit var track: AudioTrack

    suspend fun record() {
        shouldContinue = true
        // buffer size in bytes
        var bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }

        val audioBuffer = ShortArray(bufferSize)

        val record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!")
            return
        }

        record.startRecording()
        var shortsRead: Long = 0
        while (shouldContinue) {
            val numberOfShort = record.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort.toLong()

            // Notify waveform
            listener.onAudioDataReceived(audioBuffer)
        }

        record.stop()
        record.release()

        Log.v(LOG_TAG, "Recording stopped. Samples read: $shortsRead")
    }

    suspend fun stop() {
        shouldContinue = false
    }

    suspend fun play() {

    }

    suspend fun reset() {

    }
}
