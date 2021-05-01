package io.github.simonvar.sfl.ui.dictaphone

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.github.simonvar.sfl.databinding.ScreenDictaphoneBinding

class DictaphoneAnimatorFactory(
    private val binding: ScreenDictaphoneBinding,
    private val resources: Resources,
    private val buttonTranslationY: Float
) {

    companion object {
        const val ANIM_DURATION = 300L
    }

    fun moveToRecordStateAnimator(): Animator = with(binding) {
        return colorStateAnimator(DictaphoneState.Recording, resources)
    }

    fun moveToPauseStateAnimator(): Animator = with(binding) {
        val set = AnimatorSet()
        set.playTogether(
            colorStateAnimator(DictaphoneState.Paused, resources),
            recordStopButton.outAnimator(buttonTranslationY),
            playPauseButton.inAnimator(),
            resetButton.inAnimator(),
        )
        set.duration = ANIM_DURATION
        return set
    }

    fun moveToIdleStateAnimator(): Animator = with(binding) {
        val set = AnimatorSet()
        set.playTogether(
            colorStateAnimator(DictaphoneState.Idle, resources),
            recordStopButton.inAnimator(),
            playPauseButton.outAnimator(buttonTranslationY),
            resetButton.outAnimator(buttonTranslationY)
        )
        set.duration = ANIM_DURATION
        return set
    }

    private fun ScreenDictaphoneBinding.colorStateAnimator(
        state: DictaphoneState,
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

    private fun View.inAnimator(): Animator {
        return ObjectAnimator
            .ofFloat(this, View.TRANSLATION_Y, 0F)
            .apply { interpolator = AccelerateDecelerateInterpolator() }
    }

    private fun View.outAnimator(translation: Float): Animator {
        return ObjectAnimator
            .ofFloat(this, View.TRANSLATION_Y, translation)
            .apply { interpolator = AccelerateDecelerateInterpolator() }
    }
}
