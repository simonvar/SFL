package io.github.simonvar.sfl.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import io.github.simonvar.sfl.R

class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    val round = 100.dpAsPx()

    val minLevelPx = 4.dpAsPx()
    var maxLevelPx = 250.dpAsPx()

    var sizePx = 4.dpAsPx()
    var gapPx = 2.dpAsPx()

    var levelsCount = 1

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

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
        values.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val mid = height / 2
        var offsetX = 0
        for (level in 0..levelsCount) {

            offsetX += gapPx

            canvas.drawRoundRect(
                offsetX.toFloat(),
                (mid + sizePx / 2).toFloat(),
                (offsetX + sizePx).toFloat(),
                (mid - sizePx / 2).toFloat(),
                round.toFloat(),
                round.toFloat(),
                defaultPaint
            )

            offsetX += (sizePx + gapPx)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val oneLevelSize = gapPx + sizePx + gapPx
        val specSize = MeasureSpec.getSize(widthMeasureSpec)
        levelsCount = specSize / oneLevelSize
    }

    private fun Float.percentAsPx(): Int {
        return (maxLevelPx * this).toInt() + minLevelPx
    }

    private fun Int.dpAsPx(): Int {
        return this * resources.displayMetrics.density.toInt()
    }

    interface Adapter {
        fun append(level: Int)
    }

    interface OnLevelsCountChanged : (Int) -> Unit

}
