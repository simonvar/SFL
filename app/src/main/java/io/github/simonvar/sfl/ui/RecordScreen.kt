package io.github.simonvar.sfl.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import io.github.simonvar.sfl.R
import io.github.simonvar.sfl.databinding.ScreenRecordBinding
import io.github.simonvar.sfl.widget.SwitchCircleButton

class RecordScreen : Fragment(R.layout.screen_record) {

    private lateinit var binding: ScreenRecordBinding

    private val animDuration: Long by lazy {
        resources.getInteger(R.integer.state_changes_anim_duration).toLong()
    }

    private var content: RecordContent = RecordContent.Idle

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        with(binding) {
            playPauseButton.translationY = controlTranslationY(root, playPauseButton)
            resetButton.translationY = controlTranslationY(root, resetButton)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = ScreenRecordBinding.bind(view)
        initView()
    }

    private fun initView() = with(binding) {
        root.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
        initControls()
        initBackground()
    }

    private fun initBackground() = with(binding) {
        val initColor = resources.getColor(R.color.blue)
        binding.root.setBackgroundColor(initColor)
        binding.recordStopButton.tint = initColor
        binding.playPauseButton.tint = initColor
        binding.resetButton.tint = initColor
    }

    private fun initControls() = with(binding) {
        recordStopButton.setOnClickListener {
            content = RecordContent.Record
            animateBackgroundColor()
            if (recordStopButton.state == SwitchCircleButton.FIRST) {
                animatorToPlayState(binding).start()
            }
        }

        playPauseButton.setOnClickListener {
            content = RecordContent.Play
            animateBackgroundColor()
        }

        resetButton.setOnClickListener {
            content = RecordContent.Idle
            animateBackgroundColor()
            animatorToRecordState(binding).start()
        }
    }

    private fun animateBackgroundColor() {
        val colorFrom = (binding.root.background as ColorDrawable).color

        val colorTo = when (content) {
            RecordContent.Idle -> resources.getColor(R.color.blue)
            RecordContent.Record -> resources.getColor(R.color.red)
            RecordContent.Play -> resources.getColor(R.color.teal)
        }

        ValueAnimator
            .ofArgb(colorFrom, colorTo)
            .apply {
                duration = animDuration
                addUpdateListener {
                    val color = it.animatedValue as Int
                    binding.root.setBackgroundColor(color)
                    binding.recordStopButton.tint = color
                    binding.playPauseButton.tint = color
                    binding.resetButton.tint = color
                }
                start()
            }
    }

    private fun animatorToPlayState(binding: ScreenRecordBinding): Animator {
        with(binding) {
            val set = AnimatorSet()
            set.playTogether(
                controlOutAnimator(root, recordStopButton),
                controlInAnimator(playPauseButton),
                controlInAnimator(resetButton),
            )
            set.duration = animDuration
            return set
        }
    }

    private fun animatorToRecordState(binding: ScreenRecordBinding): Animator {
        with(binding) {
            val set = AnimatorSet()
            set.playTogether(
                controlInAnimator(recordStopButton),
                controlOutAnimator(root, playPauseButton),
                controlOutAnimator(root, resetButton)
            )
            set.duration = animDuration
            return set
        }
    }

    private fun controlInAnimator(target: View): Animator {
        return ObjectAnimator
            .ofFloat(target, View.TRANSLATION_Y, 0F)
            .apply { interpolator = AccelerateDecelerateInterpolator() }
    }

    private fun controlOutAnimator(container: View, target: View): Animator {
        val translation = controlTranslationY(container, target)
        return ObjectAnimator
            .ofFloat(target, View.TRANSLATION_Y, translation)
            .apply { interpolator = AccelerateDecelerateInterpolator() }
    }

    private fun controlTranslationY(container: View, target: View): Float {
        return container.height - target.y
    }
}
