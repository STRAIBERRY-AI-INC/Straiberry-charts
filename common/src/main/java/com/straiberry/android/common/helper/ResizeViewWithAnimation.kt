package com.straiberry.android.common.helper

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeViewWithAnimation(
    private val view: View,
    private val toHeight: Float,
    private val fromHeight: Float,
    private val toWidth: Float,
    private val fromWidth: Float,
    duration: Long
) : Animation() {

    init {
        this.duration = duration
    }

    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation?
    ) {
        val height = (toHeight - fromHeight) * interpolatedTime + fromHeight
        val width = (toWidth - fromWidth) * interpolatedTime + fromWidth
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        layoutParams.width = width.toInt()
        view.requestLayout()
    }
}