package com.straiberry.android.common.helper

import android.widget.ImageButton
import com.google.android.material.tabs.TabLayout
import com.straiberry.android.common.extensions.dp

/**
 * Animating the view pager indicator with image button close.
 * When user swipe to last page on view pager, indicator will hide and image button show's up.
 */
class AnimateViewPagerIndicator(
    private val lastPage: Int,
    private val tabLayout: TabLayout,
    private val imageButton: ImageButton
) {

    companion object {
        private var isImageButtonGoToHomeShowing = false
        private const val AnimationDurationForButtonGo = 800L
        private const val AnimationDurationForTranslation = 800L
        private const val AnimationDurationForAlpha = 500L
        private const val FullAlpha = 1F
        private const val ZeroAlpha = 0F
        private val ImageButtonGoToHomeSize = 58.dp
        private val TranslationXImageButtonGotToHome = (-75F).dp
        private const val TranslationXShow = 0f
        private val TranslationXHideTabIndicator = (20f).dp
        private const val StartHeightZero = 0
        private const val StartWithZero = 0
    }

    fun animateView(position: Int) {
        if (position == lastPage) {
            hideIndicator()
        } else {
            if (isImageButtonGoToHomeShowing)
                showIndicator()
        }
    }

    private fun hideIndicator() {
        isImageButtonGoToHomeShowing = true
        animationTabIndicatorHide()
        animationImageButtonGotToHomeScaleUp()
    }

    private fun showIndicator() {
        isImageButtonGoToHomeShowing = false
        animationTabIndicatorShow()
        animationImageButtonGotToHomeScaleDown()
    }

    /**
     * Hide tab indicator with animation
     */
    private fun animationTabIndicatorHide() {
        tabLayout.animate().translationX(TranslationXHideTabIndicator).duration =
            AnimationDurationForTranslation
        tabLayout.animate().alpha(ZeroAlpha).duration =
            AnimationDurationForAlpha
    }

    /**
     * Increase with and height of image button go, with animation
     */
    private fun animationImageButtonGotToHomeScaleUp() {
        imageButton.animate().translationX(TranslationXShow)
            .duration = AnimationDurationForTranslation
        val animation = ResizeAnimation(
            imageButton,
            ImageButtonGoToHomeSize, StartHeightZero, ImageButtonGoToHomeSize, StartWithZero, false
        )
        animation.duration = AnimationDurationForButtonGo
        imageButton.startAnimation(animation)
    }

    /**
     * Show tab indicator with animation
     */
    private fun animationTabIndicatorShow() {
        tabLayout.animate().translationX(TranslationXShow).duration =
            AnimationDurationForTranslation
        tabLayout.animate().alpha(FullAlpha).duration =
            AnimationDurationForAlpha
    }

    /**
     * Decrease with and height of image button go, with animation
     */
    private fun animationImageButtonGotToHomeScaleDown() {
        imageButton.animate()
            .translationX(TranslationXImageButtonGotToHome).duration =
            AnimationDurationForTranslation
        val animation = ResizeAnimation(
            imageButton,
            ImageButtonGoToHomeSize,
            ImageButtonGoToHomeSize,
            ImageButtonGoToHomeSize,
            ImageButtonGoToHomeSize,
            true
        )
        animation.duration = AnimationDurationForButtonGo
        imageButton.startAnimation(animation)
    }
}