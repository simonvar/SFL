package io.github.simonvar.sfl.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.github.simonvar.sfl.databinding.ScreenRecordBinding

fun ScreenRecordBinding.colorStateAnimator(
    state: RecordContent,
    resources: Resources
): Animator {
    val currentColor = (root.background as? ColorDrawable)?.color ?: 0
    return ValueAnimator
        .ofArgb(currentColor, state.color(resources))
        .apply {
            addUpdateListener {
                val color = it.animatedValue as Int
                root.setBackgroundColor(color)
                recordStopButton.tint = color
                playPauseButton.tint = color
                resetButton.tint = color
            }
        }
}

fun View.inAnimator(): Animator {
    return ObjectAnimator
        .ofFloat(this, View.TRANSLATION_Y, 0F)
        .apply { interpolator = AccelerateDecelerateInterpolator() }
}

fun View.outAnimator(translation: Float): Animator {
    return ObjectAnimator
        .ofFloat(this, View.TRANSLATION_Y, translation)
        .apply { interpolator = AccelerateDecelerateInterpolator() }
}
