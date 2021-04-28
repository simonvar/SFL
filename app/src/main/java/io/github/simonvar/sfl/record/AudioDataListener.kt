package io.github.simonvar.sfl.record

interface AudioDataListener {
    fun onAudioDataReceived(data: ShortArray)
}