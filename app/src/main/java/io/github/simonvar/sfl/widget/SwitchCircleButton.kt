package io.github.simonvar.sfl.widget

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import io.github.simonvar.sfl.R

class SwitchCircleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val FIRST = 0
        const val SECOND = 0
    }

    var state by this::currentState
        private set

    var text: CharSequence = ""
        set(value) {
            field = value
            innerTextView.text = value
        }

    @ColorRes
    var tint: Int = 0
        set(value) {
            field = value
            innerFirstImage.imageTintList = ColorStateList.valueOf(value)
            innerSecondImage.imageTintList = ColorStateList.valueOf(value)
        }

    var duration: Long = 0L

    @DrawableRes
    var firstIconId: Int = 0
        set(value) {
            field = value
            innerFirstImage.setImageResource(value)
        }

    @DrawableRes
    var secondIconId: Int = 0
        set(value) {
            field = value
            innerSecondImage.setImageResource(value)
        }

    private lateinit var innerButton: FrameLayout
    private lateinit var innerFirstImage: ImageView
    private lateinit var innerSecondImage: ImageView
    private lateinit var innerTextView: TextView

    private var currentState = FIRST

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet) {
        orientation = VERTICAL

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.layout_circle_button, this, true)

        innerButton = findViewById(R.id.icons_container)
        innerFirstImage = findViewById(R.id.icon_first)
        innerSecondImage = findViewById(R.id.icon_second)
        innerTextView = findViewById(R.id.text)

        initAttributes(context, attrs)
        initIcons()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.SwitchCircleButton)

        text = values.getString(R.styleable.SwitchCircleButton_android_text).orEmpty()
        tint = values.getColor(R.styleable.SwitchCircleButton_android_tint, 0)
        duration = values.getInteger(R.styleable.SwitchCircleButton_android_duration, 0).toLong()
        firstIconId = values.getResourceId(R.styleable.SwitchCircleButton_drawable_first, 0)
        secondIconId = values.getResourceId(R.styleable.SwitchCircleButton_drawable_second, 0)

        values.recycle()
    }

    private fun initIcons() {
        with(innerFirstImage) {
            isVisible = true
            scaleX = 1F
            scaleY = 1F
            rotation = 0F
        }

        with(innerSecondImage) {
            isVisible = true
            scaleX = 0F
            scaleY = 0F
            rotation = -90F
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        with(innerFirstImage) {
            pivotX = width / 2F
            pivotY = height / 2F
        }
        with(innerSecondImage) {
            pivotX = width / 2F
            pivotY = height / 2F
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        innerButton.setOnClickListener {
            listener?.onClick(it)
            moveToNextState()
        }
    }

    private fun moveToNextState() {
        if (secondIconId == 0) return
        val nextState = (currentState + 1) % 2
        when (nextState) {
            0 -> startIconAnimation(innerSecondImage, innerFirstImage)
            1 -> startIconAnimation(innerFirstImage, innerSecondImage)
            else -> throw IllegalStateException("Can't be more than two states!")
        }
        currentState = nextState
    }

    private fun startIconAnimation(targetOut: View, targetIn: View) {
        val set = AnimatorSet()
        set.playTogether(iconOutAnimator(targetOut), iconInAnimator(targetIn))
        set.start()
    }

    private fun iconOutAnimator(target: View): Animator {
        return AnimatorInflater
            .loadAnimator(context, R.animator.anim_icon_out)
            .apply {
                duration = this@SwitchCircleButton.duration
                setTarget(target)
            }
    }

    private fun iconInAnimator(target: View): Animator {
        return AnimatorInflater
            .loadAnimator(context, R.animator.anim_icon_in)
            .apply {
                duration = this@SwitchCircleButton.duration
                setTarget(target)
            }
    }
}