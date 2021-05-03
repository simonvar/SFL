package io.github.simonvar.sfl.ui.dictaphone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import io.github.simonvar.sfl.R
import io.github.simonvar.sfl.databinding.ScreenDictaphoneBinding
import io.github.simonvar.sfl.ui.base.BaseScreen
import io.github.simonvar.sfl.ui.dictaphone.DictaphoneAnimatorFactory.Companion.ANIM_DURATION
import io.github.simonvar.sfl.widget.CircleButton
import io.github.simonvar.sfl.widget.WaveView

class DictaphoneScreen : BaseScreen(R.layout.screen_dictaphone) {

    private lateinit var binding: ScreenDictaphoneBinding
    private lateinit var animatorFactory: DictaphoneAnimatorFactory

    private val vm: DictaphoneViewModel by viewModels()
    private var buttonTranslationY = 0F

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            vm.onPermissionGranted()
            vm.onRecordClick()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (isRecordPermissionGranted()) vm.onPermissionGranted()

        binding = ScreenDictaphoneBinding.bind(view)
        with(binding) {
            root.doOnLayout {
                buttonTranslationY = it.height - recordStopButton.y
                animatorFactory = DictaphoneAnimatorFactory(binding, resources, buttonTranslationY)

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
                CircleButton.FIRST -> {
                    if (isRecordPermissionGranted()) {
                        vm.onRecordClick()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
                CircleButton.SECOND -> {
                    vm.onStopClick()
                }
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
        vm.state bind this::handleRecordState
        vm.data bind binding.waveform::setData
    }

    private fun handleRecordState(state: DictaphoneState) {
        when (state) {
            DictaphoneState.Idle -> moveToIdleState()
            DictaphoneState.Recording -> moveToRecordState()
            DictaphoneState.Paused -> moveToPauseState()
            DictaphoneState.Playing -> moveToPlayState()
        }
    }

    private fun moveToIdleState() = with(binding) {
        recordStopButton.jumpToState(CircleButton.FIRST)
        animatorFactory.moveToIdleStateAnimator().start()
    }

    private fun moveToRecordState() = with(binding) {
        recordStopButton.moveToState(CircleButton.SECOND)
        animatorFactory.moveToRecordStateAnimator().start()
    }

    private fun moveToPauseState() = with(binding) {
        playPauseButton.moveToState(CircleButton.FIRST)
        animatorFactory.moveToPauseStateAnimator().start()
    }

    private fun moveToPlayState() = with(binding) {
        playPauseButton.moveToState(CircleButton.SECOND)
    }

    private fun isRecordPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        requestPermissionLauncher.unregister()
    }
}
