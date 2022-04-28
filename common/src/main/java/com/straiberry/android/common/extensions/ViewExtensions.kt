package com.straiberry.android.common.extensions

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun showLoading(progressBar: ProgressBar) = progressBar.visible()
fun hideLoading(progressBar: ProgressBar) = progressBar.gone()
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Context.getFromClipBoard(): String? {
    val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    return clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

inline fun View.onClick(crossinline onClick: () -> Unit) {
    setOnClickListener { onClick() }
}

/**
 * Increase the click area of this view
 */
fun View.increaseHitArea(dp: Float) {
    // increase the hit area
    val increasedArea = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        Resources.getSystem().displayMetrics
    ).toInt()
    val parent = parent as View
    parent.post {
        val rect = Rect()
        getHitRect(rect)
        rect.top -= increasedArea
        rect.left -= increasedArea
        rect.bottom += increasedArea
        rect.right += increasedArea
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}

fun View.setMargins(
    leftMarginDp: Int? = null,
    topMarginDp: Int? = null,
    rightMarginDp: Int? = null,
    bottomMarginDp: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        leftMarginDp?.run { params.leftMargin = this.dp }
        topMarginDp?.run { params.topMargin = this.dp }
        rightMarginDp?.run { params.rightMargin = this.dp }
        bottomMarginDp?.run { params.bottomMargin = this.dp }
        requestLayout()
    }
}

fun View.clearMargins() {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = 0.dp
        params.topMargin = 0.dp
         params.rightMargin = 0.dp
         params.bottomMargin = 0.dp
        requestLayout()
    }
}

fun View.visibleWithAnimation() {
    this.animate().alpha(1f)
    visible()
}

fun View.hideWithAnimation() {
    this.animate().alpha(0f)
    hide()
}

fun View.hideWithoutAnimation() {
    this.alpha = 0f
    hide()
}

fun TextView.changeTextWithAnimation() {
    alpha = 0f
    animate().alpha(1f).duration = 500
}

fun View.goneWithAnimation() {
    this.animate().alpha(0f)
    gone()
}

fun View.goneWithDelay(delay: Long,endOfAnimation: EndOfAnimation) {
    this.animate()
        .setStartDelay(delay)
        .withEndAction {
            gone()
            endOfAnimation()
        }
        .start()
}

fun View.takeScreenshot(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgDrawable = this.background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    this.draw(canvas)
    return bitmap
}

fun TextView.mirroring(){
    scaleX = -1f
    scaleY = 1f
    translationX = 1f
}

fun TextView.clearMirroring(){
    scaleX = 1f
    scaleY = 1f
    translationX = 0f
}

fun String.launchUrl(activity: Activity){
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(activity, Uri.parse(this))
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

typealias EndOfAnimation = () -> Unit