package io.github.simonvar.sfl.dictaphone.audiorecord

import kotlinx.coroutines.flow.Flow

interface AudioRecordFeature {

    fun init(sampleRate: Int)

    fun record(levelsCount: Int): Flow<ShortArray>

    fun stop()

    fun reset()

    fun release()
}
