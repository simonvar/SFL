package io.github.simonvar.sfl.dictophone

interface AudioDataListener {
    fun onAudioDataReceived(data: ShortArray)
}