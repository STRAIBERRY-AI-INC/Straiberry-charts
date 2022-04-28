package com.straiberry.android.common.custom.spotlight

import android.animation.*
import android.animation.ValueAnimator.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.straiberry.android.common.R
import com.straiberry.android.common.extensions.*


enum class ShowCasePosition { TopCenter, TopLeft, TopRight, BottomCenter, BottomLeft, BottomRight }

/**
 * [SpotlightView] starts/finishes [Spotlight], and starts/finishes a current [Target].
 */
internal class SpotlightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @ColorInt backgroundColor: Int
) : FrameLayout(context, attrs, defStyleAttr) {

    private val backgroundPaint by lazy {
        Paint().apply { color = backgroundColor }
    }

    private val shapePaint by lazy {
        Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    }

    private val effectPaint by lazy { Paint() }
    private lateinit var listener: OnSpotlightListener
    private val invalidator = AnimatorUpdateListener { invalidate() }
    private val textViewSkip = TextView(context)
    private var shapeAnimator: ValueAnimator? = null
    private var effectAnimator: ValueAnimator? = null
    private var target: Target? = null
    private var descriptionView: View? = null
    private var descriptionRootView: ConstraintLayout? = null
    private var textViewDescription: TextView? = null

    init {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        descriptionView = LayoutInflater.from(context)
            .inflate(R.layout.layout_spot_light_description, null, false).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
        descriptionRootView = descriptionView?.findViewById(R.id.rootLayoutHelpDescription)
        textViewDescription =
            descriptionView?.findViewById(R.id.textViewHelpDescription)
        addView(descriptionView)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        val currentTarget = target
        val currentShapeAnimator = shapeAnimator
        val currentEffectAnimator = effectAnimator
        if (currentTarget != null && currentEffectAnimator != null && currentShapeAnimator != null && !currentShapeAnimator.isRunning) {
            currentTarget.effect.draw(
                canvas = canvas,
                point = currentTarget.anchor,
                value = currentEffectAnimator.animatedValue as Float,
                paint = effectPaint
            )
        }
        if (currentTarget != null && currentShapeAnimator != null) {
            // Draw shape
            currentTarget.shape.draw(
                canvas = canvas,
                point = currentTarget.anchor,
                value = currentShapeAnimator.animatedValue as Float,
                paint = shapePaint,
                context = context
            )
        }
    }

    private fun setupSkipButton() {
        addView(textViewSkip)
        textViewSkip.apply {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    SKIP_BUTTON_SIZE.dp(context)
                ).apply {
                    this.gravity = Gravity.TOP or Gravity.END
                }
            gravity = Gravity.END
            onClick { listener.onSkip() }
            text = context.getString(R.string.skip)
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setMargins(
                (SKIP_MARGIN_LEFT_AND_RIGHT).dp(context),
                (SKIP_MARGIN_TOP).dp(context),
                (SKIP_MARGIN_LEFT_AND_RIGHT).dp(context),
                (SKIP_MARGIN_LEFT_AND_RIGHT).dp(context)
            )

            this.increaseHitArea((SKIP_MARGIN_LEFT_AND_RIGHT).dp(context).toFloat())
        }
    }

    fun setListener(listener: OnSpotlightListener) {
        this.listener = listener
    }

    /**
     * Starts [Spotlight].
     */
    fun startSpotlight(
        duration: Long,
        interpolator: TimeInterpolator,
        listener: Animator.AnimatorListener
    ) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
            setDuration(duration)
            setInterpolator(interpolator)
            addListener(listener)
        }
        objectAnimator.start()
    }

    /**
     * Finishes [Spotlight].
     */
    fun finishSpotlight(
        duration: Long,
        interpolator: TimeInterpolator,
        listener: Animator.AnimatorListener
    ) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
            setDuration(duration)
            setInterpolator(interpolator)
            addListener(listener)
        }
        objectAnimator.start()
    }

    /**
     * Starts the provided [Target].
     */
    fun startTarget(target: Target) {
        removeAllViews()
        this.target = target.apply {
            setupSkipButton()
            // Make spotlight clickable when target view
            // is not clickable
            if (!isClickable) {
                this@SpotlightView.isClickable = true
                onClick { listener!!.onClick() }
            } else
                this@SpotlightView.isClickable = false

            // adjust anchor in case where custom container is set.
            val location = IntArray(2)
            getLocationInWindow(location)
            val offset = PointF(location[0].toFloat(), location[1].toFloat())
            anchor.offset(-offset.x, -offset.y)
            descriptionView?.visible()
            textViewDescription?.text = description

            if (showCasePosition == ShowCasePosition.TopCenter)
                if (layoutDirection == LayoutDirection.LTR)
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! / 2)
                        ?.translationY(anchor.y - shape.radius - descriptionView?.height!! * 2)
                        ?.start()
                else
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! - descriptionView?.width!! / 3)
                        ?.translationY(anchor.y - shape.radius - descriptionView?.height!! * 2)
                        ?.start()
            else if (showCasePosition == ShowCasePosition.BottomCenter)
                if (layoutDirection == LayoutDirection.LTR)
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! / 2)
                        ?.translationY(anchor.y + shape.radius + descriptionView?.height!! / 2)
                        ?.start()
                else
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! - descriptionView?.width!! / 3)
                        ?.translationY(anchor.y + shape.radius + descriptionView?.height!! / 2)
                        ?.start()
            else if (showCasePosition == ShowCasePosition.TopLeft)
                if (layoutDirection == LayoutDirection.LTR)
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!!)
                        ?.translationY(anchor.y - shape.radius - descriptionView?.height!! * 2)
                        ?.start()
                else
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! / 1.5f)
                        ?.translationY(anchor.y - shape.radius - descriptionView?.height!! * 2)
                        ?.start()
            else if (showCasePosition == ShowCasePosition.TopRight)
                if (layoutDirection == LayoutDirection.LTR)
                    descriptionView?.animate()
                        ?.translationX(anchor.x)
                        ?.translationY(anchor.y - shape.radius - descriptionView?.height!! * 2)
                        ?.start()
                else
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! * 2)
                        ?.translationY(anchor.y - shape.radius - descriptionView?.height!! * 2)
                        ?.start()
            else if (showCasePosition == ShowCasePosition.BottomLeft)
                if (layoutDirection == LayoutDirection.LTR)
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!!)
                        ?.translationY(anchor.y + shape.radius + descriptionView?.height!! / 2)
                        ?.start()
                else
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! / 1.5f)
                        ?.translationY(anchor.y + shape.radius + descriptionView?.height!! / 2)
                        ?.start()
            else if (showCasePosition == ShowCasePosition.BottomRight)
                if (layoutDirection == LayoutDirection.LTR)
                    descriptionView?.animate()
                        ?.translationX(anchor.x)
                        ?.translationY(anchor.y + shape.radius + descriptionView?.height!! / 2)
                        ?.start()
                else
                    descriptionView?.animate()
                        ?.translationX(anchor.x - descriptionView?.width!! * 2)
                        ?.translationY(anchor.y + shape.radius + descriptionView?.height!! / 2)
                        ?.start()

            addView(descriptionView)
        }
        this.shapeAnimator?.removeAllListeners()
        this.shapeAnimator?.removeAllUpdateListeners()
        this.shapeAnimator?.cancel()
        this.shapeAnimator = ValueAnimator.ofPropertyValuesHolder(
            PropertyValuesHolder.ofFloat("scaleX", 1.5f),
            PropertyValuesHolder.ofFloat("scaleY", 1.5f)
        ).apply {
            duration = target.shape.duration
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            addUpdateListener(invalidator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    removeAllListeners()
                    removeAllUpdateListeners()
                }

                override fun onAnimationCancel(animation: Animator) {
                    removeAllListeners()
                    removeAllUpdateListeners()
                }
            })
        }
        this.effectAnimator?.removeAllListeners()
        this.effectAnimator?.removeAllUpdateListeners()
        this.effectAnimator?.cancel()
        this.effectAnimator = ofFloat(0f, 1f).apply {
            startDelay = target.shape.duration
            duration = target.effect.duration
            interpolator = target.effect.interpolator
            repeatMode = target.effect.repeatMode
            repeatCount = INFINITE
            addUpdateListener(invalidator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    removeAllListeners()
                    removeAllUpdateListeners()
                }

                override fun onAnimationCancel(animation: Animator) {
                    removeAllListeners()
                    removeAllUpdateListeners()
                }
            })
        }
        shapeAnimator?.start()
        effectAnimator?.start()
    }

    /**
     * Finishes the current [Target].
     */
    fun finishTarget(listener: Animator.AnimatorListener) {
        val currentTarget = target ?: return
        val currentAnimatedValue = shapeAnimator?.animatedValue ?: return
        shapeAnimator?.removeAllListeners()
        shapeAnimator?.removeAllUpdateListeners()
        shapeAnimator?.cancel()
        shapeAnimator = ofFloat(currentAnimatedValue as Float, 0f).apply {
            duration = currentTarget.shape.duration
            interpolator = currentTarget.shape.interpolator
            addUpdateListener(invalidator)
            addListener(listener)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    removeAllListeners()
                    removeAllUpdateListeners()
                }

                override fun onAnimationCancel(animation: Animator) {
                    removeAllListeners()
                    removeAllUpdateListeners()
                }
            })
        }
        effectAnimator?.removeAllListeners()
        effectAnimator?.removeAllUpdateListeners()
        effectAnimator?.cancel()
        effectAnimator = null
        shapeAnimator?.start()
    }

    fun cleanup() {
        effectAnimator?.removeAllListeners()
        effectAnimator?.removeAllUpdateListeners()
        effectAnimator?.cancel()
        effectAnimator = null
        shapeAnimator?.removeAllListeners()
        shapeAnimator?.removeAllUpdateListeners()
        shapeAnimator?.cancel()
        shapeAnimator = null
        removeAllViews()
    }

    companion object {
        private const val SKIP_MARGIN_LEFT_AND_RIGHT = 15
        private const val SKIP_MARGIN_TOP = 20
        private const val SKIP_BUTTON_SIZE = 70
    }
}
typealias OnTargetClick = () -> Unit