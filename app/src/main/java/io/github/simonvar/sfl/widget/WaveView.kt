package io.github.simonvar.sfl.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
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

    class Params(
        val count: Int,
        val margin: Int
    )

    private var levels: List<Int> = emptyList()

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

    init {
        initAttributes(context, attrs)
    }

    fun setLevels(levels: List<Int>, withAnimation: Boolean = true) {
        this.levels = levels
        invalidate()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
        values.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val mid = height / 2
        var offsetX = 0
        for (level in levels) {
            offsetX += levelMarginPx

            canvas.drawRoundRect(
                offsetX.toFloat(),
                (mid + level.percentAsPx()).toFloat(),
                (offsetX + LEVEL_WIDTH_PX).toFloat(),
                (mid - level.percentAsPx()).toFloat(),
                CORNER_RADIUS_PX.toFloat(),
                CORNER_RADIUS_PX.toFloat(),
                defaultPaint
            )

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
