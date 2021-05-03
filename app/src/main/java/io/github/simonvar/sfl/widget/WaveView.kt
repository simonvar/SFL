package io.github.simonvar.sfl.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import io.github.simonvar.sfl.R
import io.github.simonvar.sfl.dpAsPx

class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private val CORNER_RADIUS_PX = 100.dpAsPx()
        private val LEVEL_WIDTH_PX = 4.dpAsPx()
        private var MIN_LEVEL_MARGIN_PX = 2.dpAsPx()

        fun calculateParamsForWidth(width: Int): Params {
            val levelWidth = MIN_LEVEL_MARGIN_PX + LEVEL_WIDTH_PX + MIN_LEVEL_MARGIN_PX
            val count = width / levelWidth
            return Params(count, MIN_LEVEL_MARGIN_PX)
        }
    }

    data class Data(
        val levels: List<Int> = emptyList(),
        val coloredToIndex: Int = levels.lastIndex,
        val withAnimation: Boolean = false
    )

    data class Params(
        val count: Int,
        val margin: Int
    )

    private var data = Data(emptyList())

    var levelMarginPx = 0
        set(value) {
            field = value
            invalidate()
        }

    private var levelMaxHeightPx = 0

    private val defaultPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    private val alphaPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        alpha = 52
    }

    private val animator = ValueAnimator.ofFloat(0F, 1F)
        .apply {
            duration = 150L
            interpolator = AccelerateInterpolator()
        }

    init {
        initAttributes(context, attrs)
    }

    fun setData(data: Data) {
        animator.pause()

        if (data.withAnimation) {

            val prevLevels = this.data.levels
            val newLevels = data.levels

            animator.addUpdateListener {
                val ratio = it.animatedValue as Float
                val tempLevels = prevLevels.mapIndexed { index, value ->
                    val diff = value - (newLevels.getOrNull(index) ?: 0)
                    (value - (diff * ratio)).toInt()
                }
                this.data = Data(tempLevels)
                invalidate()
            }
            animator.start()
        } else {
            this.data = data
            invalidate()
        }
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
        values.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val mid = height / 2
        var offsetX = 0
        data.levels.forEachIndexed { index, level ->
            offsetX += levelMarginPx

            val paint = if (index <= data.coloredToIndex) defaultPaint else alphaPaint
            val left = offsetX.toFloat()
            val top = (mid + level.percentAsPx()).toFloat()
            val right = (offsetX + LEVEL_WIDTH_PX).toFloat()
            val bottom = (mid - level.percentAsPx()).toFloat()
            val rx = CORNER_RADIUS_PX.toFloat()
            val ry = CORNER_RADIUS_PX.toFloat()

            canvas.drawRoundRect(left, top, right, bottom, rx, ry, paint)

            offsetX += (LEVEL_WIDTH_PX + levelMarginPx)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specSizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specSizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        levelMaxHeightPx = specSizeHeight / 2
    }

    private fun Int.percentAsPx(): Int {
        return levelMaxHeightPx / 100 * this + LEVEL_WIDTH_PX / 2
    }
}
