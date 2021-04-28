package io.github.simonvar.sfl.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import io.github.simonvar.sfl.R
import io.github.simonvar.sfl.databinding.ScreenRecordBinding
import io.github.simonvar.sfl.widget.CircleButton
import io.github.simonvar.sfl.widget.WaveView
import kotlinx.coroutines.flow.collect

class RecordScreen : Fragment(R.layout.screen_record) {

    companion object {
        private const val ANIM_DURATION = 300L
    }

    private lateinit var binding: ScreenRecordBinding
    private val vm: RecordViewModel by viewModels()
    private var buttonTranslationY = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = ScreenRecordBinding.bind(view)
        with(binding) {
            root.doOnLayout {
                buttonTranslationY = it.height - recordStopButton.y
                playPauseButton.translationY = buttonTranslationY
                resetButton.translationY = buttonTranslationY

                val params = WaveView.calculateParamsForWidth(it.width)
                waveform.levelMarginPx = params.margin

                initVM(params)
            }

        }


        initButtons()
        initColors()
    }

    private fun initButtons() = with(binding) {
        recordStopButton.duration = ANIM_DURATION
        recordStopButton.setOnClickListener {
            when (recordStopButton.state) {
                CircleButton.FIRST -> vm.onRecordClick()
                CircleButton.SECOND -> vm.onStopClick()
            }
        }

        playPauseButton.duration = ANIM_DURATION
        playPauseButton.setOnClickListener {
            when (playPauseButton.state) {
                CircleButton.FIRST -> vm.onPlayClick()
                CircleButton.SECOND -> vm.onPauseClick()
            }
        }

        resetButton.setOnClickListener {
            vm.onResetClick()
        }
    }

    private fun initColors() = with(binding) {
        val initColor = resources.getColor(R.color.blue)
        binding.root.setBackgroundColor(initColor)
        binding.recordStopButton.tint = initColor
        binding.playPauseButton.tint = initColor
        binding.resetButton.tint = initColor
    }

    private fun initVM(params: WaveView.Params) {
        vm.onChangeLevelsCount(params.count)

        lifecycleScope.launchWhenCreated {
            vm.state.collect {
                handleRecordState(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            vm.levels.collect {
                binding.waveform.setLevels(it)
            }
        }
    }

    private fun handleRecordState(state: RecordState) {
        when (state) {
            RecordState.Idle -> moveToIdleState()
            RecordState.Recording -> moveToRecordState()
            RecordState.Paused -> moveToPauseState()
            RecordState.Playing -> moveToPlayState()
        }
    }

    private fun moveToIdleState() = with(binding) {
        recordStopButton.jumpToState(CircleButton.FIRST)
        binding.toIdleStateAnimator().start()
    }

    private fun moveToRecordState() = with(binding) {
        recordStopButton.moveToState(CircleButton.SECOND)
        binding.toRecordAnimator().start()
    }

    private fun moveToPauseState() = with(binding) {
        playPauseButton.moveToState(CircleButton.FIRST)
        binding.toPauseStateAnimator().start()
    }

    private fun moveToPlayState() = with(binding) {
        playPauseButton.moveToState(CircleButton.SECOND)
    }

    private fun ScreenRecordBinding.toRecordAnimator(): Animator {
        return colorStateAnimator(RecordState.Recording, resources)
    }

    private fun ScreenRecordBinding.toPauseStateAnimator(): Animator {
        val set = AnimatorSet()
        set.playTogether(
            colorStateAnimator(RecordState.Paused, resources),
            recordStopButton.outAnimator(buttonTranslationY),
            playPauseButton.inAnimator(),
            resetButton.inAnimator(),
        )
        set.duration = ANIM_DURATION
        return set
    }

    private fun ScreenRecordBinding.toIdleStateAnimator(): Animator {
        val set = AnimatorSet()
        set.playTogether(
            colorStateAnimator(RecordState.Idle, resources),
            recordStopButton.inAnimator(),
            playPauseButton.outAnimator(buttonTranslationY),
            resetButton.outAnimator(buttonTranslationY)
        )
        set.duration = ANIM_DURATION
        return set
    }
}
