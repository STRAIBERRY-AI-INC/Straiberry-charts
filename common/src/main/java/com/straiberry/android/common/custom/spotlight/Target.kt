package com.straiberry.android.common.custom.spotlight

import android.graphics.PointF
import android.view.View
import com.straiberry.android.common.custom.spotlight.effect.Effect
import com.straiberry.android.common.custom.spotlight.effect.EmptyEffect
import com.straiberry.android.common.custom.spotlight.shape.Circle
import com.straiberry.android.common.custom.spotlight.shape.Shape

/**
 * Target represents the spot that Spotlight will cast.
 */
class Target(
    val showCasePosition: ShowCasePosition,
    val description: String,
    val isClickable : Boolean,
    val anchor: PointF,
    val shape: Shape,
    val effect: Effect,
    val overlay: View?,
    val listener: OnTargetListener?
) {

    /**
     * [Builder] to build a [Target].
     * All parameters should be set in this [Builder].
     */
    class Builder {

        private var anchor: PointF = DEFAULT_ANCHOR
        private var showCasePosition: ShowCasePosition = ShowCasePosition.TopCenter
        private var description: String = ""
        private var shape: Shape = DEFAULT_SHAPE
        private var effect: Effect = DEFAULT_EFFECT
        private var overlay: View? = null
        private var isClickable = true
        private var listener: OnTargetListener? = null

        /**
         * Set if the target view is not clickable
         */
        fun notClickable(isClickable: Boolean):Builder = apply {
            this.isClickable = isClickable
        }
        /**
         * Set the position of show case based on target [ShowCasePosition]
         */
        fun showCasePosition(showCasePosition: ShowCasePosition): Builder = apply {
            this.showCasePosition = showCasePosition
        }

        /**
         * Set the description of current target
         */
        fun setDescription(description: String): Builder = apply {
            this.description = description
        }

        /**
         * Sets a pointer to start a [Target].
         */
        fun setAnchor(view: View): Builder = apply {
            val location = IntArray(2)
            view.getLocationInWindow(location)
            val x = location[0] + view.width / 2f
            val y = location[1] + view.height / 2f
            setAnchor(x, y)
        }

        /**
         * Sets an anchor point to start [Target].
         */
        fun setAnchor(x: Float, y: Float): Builder = apply {
            setAnchor(PointF(x, y))
        }

        /**
         * Sets an anchor point to start [Target].
         */
        fun setAnchor(anchor: PointF): Builder = apply {
            this.anchor = anchor
        }

        /**
         * Sets [shape] of the spot of [Target].
         */
        fun setShape(shape: Shape): Builder = apply {
            this.shape = shape
        }

        /**
         * Sets [effect] of the spot of [Target].
         */
        fun setEffect(effect: Effect): Builder = apply {
            this.effect = effect
        }

        /**
         * Sets [overlay] to be laid out to describe [Target].
         */
        fun setOverlay(overlay: View): Builder = apply {
            this.overlay = overlay
        }

        /**
         * Sets [OnTargetListener] to notify the state of [Target].
         */
        fun setOnTargetListener(listener: OnTargetListener): Builder = apply {
            this.listener = listener
        }

        fun build() = Target(
            showCasePosition = showCasePosition,
            description = description,
            isClickable = isClickable,
            anchor = anchor,
            shape = shape,
            effect = effect,
            overlay = overlay,
            listener = listener
        )

        companion object {

            private val DEFAULT_ANCHOR = PointF(0f, 0f)

            private val DEFAULT_SHAPE = Circle(100f)

            private val DEFAULT_EFFECT = EmptyEffect()
        }
    }
}

