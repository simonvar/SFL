package io.github.simonvar.sfl.ui

import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat
import io.github.simonvar.sfl.R
import io.github.simonvar.sfl.databinding.ScreenMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CHANGE_STATE_ANIM_MS = 250L
    }

    var bgState = 0

    lateinit var binding: ScreenMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setWindowTransparency()
        initView()
    }

    private fun initView() {
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
            animateBackgroundColor()
        }
        playPauseButton.setOnClickListener {  }
        resetButton.setOnClickListener {  }
    }

    private fun animateBackgroundColor() {
        val colorFrom = when (bgState) {
            0 -> resources.getColor(R.color.blue)
            1 -> resources.getColor(R.color.red)
            2 -> resources.getColor(R.color.teal)
            else -> resources.getColor(R.color.blue)
        }

        val colorTo = when (bgState) {
            0 -> resources.getColor(R.color.red)
            1 -> resources.getColor(R.color.teal)
            2 -> resources.getColor(R.color.blue)
            else -> resources.getColor(R.color.red)
        }

        val animator = ValueAnimator.ofArgb(colorFrom, colorTo)
        animator.duration = CHANGE_STATE_ANIM_MS

        animator.addUpdateListener {
            val color = it.animatedValue as Int
            binding.root.setBackgroundColor(color)
            binding.recordStopButton.tint = color
            binding.playPauseButton.tint = color
            binding.resetButton.tint = color
        }

        bgState = (bgState + 1) % 3
        animator.start()
    }

    private fun setWindowTransparency() {
        removeSystemInsets(window.decorView)
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun removeSystemInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }
}