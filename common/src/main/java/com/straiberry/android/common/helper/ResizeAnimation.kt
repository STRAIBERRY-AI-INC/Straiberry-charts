package com.straiberry.android.common.helper

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(
    var view: View,
    private val targetHeight: Int,
    var startHeight: Int,
    private val targetWith: Int,
    var startWith: Int,
    var isScaleDown:Boolean
) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        var newHeight=0
        var newWith=0
        if (isScaleDown){
             newHeight = (startHeight - targetHeight * interpolatedTime).toInt()
             newWith = (startWith  - targetWith * interpolatedTime).toInt()
        }else {
            newHeight = (startHeight + targetHeight * interpolatedTime).toInt()
            newWith = (startWith + targetWith * interpolatedTime).toInt()
        }
        if (targetHeight != 0)
            view.layoutParams.height = newHeight
        if (targetWith != 0)
            view.layoutParams.width = newWith
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}