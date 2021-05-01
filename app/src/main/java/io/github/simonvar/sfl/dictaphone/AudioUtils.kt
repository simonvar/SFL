package io.github.simonvar.sfl.dictaphone

fun calculateAudioLength(samplesCount: Int, sampleRate: Int, channelCount: Int): Int {
    return samplesCount / channelCount * 1000 / sampleRate
}

fun extremes(data: ShortArray, sampleSize: Int): List<Extreme> {
    return TODO()
}