package com.straiberry.android.charts.extenstions

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat

fun View.getDrawable(drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(this.context, drawableRes)
