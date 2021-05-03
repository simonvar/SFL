package io.github.simonvar.sfl.dictaphone.audiorecord

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioRecordFeatureImpl : AudioRecordFeature {

    companion object {
        private const val LOG_TAG = "AudioRecordFeature"
    }

    private var record: AudioRecord? = null

    private var sampleRate = 8000
    private var bufferSize = 0

    private var recorded = mutableListOf<Short>()

    override fun init(sampleRate: Int) {
        this.sampleRate = sampleRate

        bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = sampleRate * 2
        }

        record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (record?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!")
            throw IllegalStateException("Audio Record can't initialize!")
        }
    }

    override fun record(levelsCount: Int): Flow<ShortArray> {
        val record = this.record ?: throw IllegalStateException("Audio Record need initialize!")
        val buffer = ShortArray(bufferSize)
        record.startRecording()

        return flow {
            while (record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                record.read(buffer, 0, buffer.size)
                recorded.addAll(buffer.asList())
                emit(buffer)
            }
        }
    }

    override fun stop() {
        record?.stop()
    }

    override fun reset() {
        recorded.clear()
    }

    override fun release() {
        record?.stop()
        record?.release()
        record = null
    }
}
