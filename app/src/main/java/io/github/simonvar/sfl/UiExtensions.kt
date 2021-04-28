package io.github.simonvar.sfl

import android.content.res.Resources

fun Int.dpAsPx(): Int {
    return this * Resources.getSystem().displayMetrics.density.toInt()
}