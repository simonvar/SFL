package io.github.simonvar.sfl.dictaphone

import kotlin.math.max
import kotlin.math.min

fun calculateAudioLength(samplesCount: Int, sampleRate: Int, channelCount: Int): Int {
    return samplesCount / channelCount * 1000 / sampleRate
}

fun extremes(data: ShortArray, sampleSize: Int): List<Extreme> {
    val groupSize = data.size / sampleSize
    return List(sampleSize) { index ->
        val fromIndex = index * groupSize
        val toIndex = min((index + 1) * groupSize, data.size)

        var min = Short.MAX_VALUE.toInt()
        var max = Short.MIN_VALUE.toInt()

        val group = data.copyOfRange(fromIndex, toIndex)
        group.forEach {
            min = min(min, it.toInt())
            max = max(max, it.toInt())
        }

        Extreme(min, max)
    }
}