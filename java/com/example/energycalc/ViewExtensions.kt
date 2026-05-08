package com.example.energycalc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator

fun View.animateVisibility(isVisible: Boolean, duration: Long = 300) {
    if (isVisible == (visibility == View.VISIBLE)) return

    animate().cancel()

    if (isVisible) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measure(widthSpec, heightSpec)
        val targetHeight = measuredHeight

        layoutParams.height = 0
        visibility = View.VISIBLE
        alpha = 0f

        ValueAnimator.ofInt(0, targetHeight).apply {
            this.duration = duration
            interpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
            addUpdateListener { animator ->
                layoutParams.height = animator.animatedValue as Int
                requestLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            })
            start()
        }

        animate().alpha(1f).setDuration(duration).start()

    } else {
        val initialHeight = height

        ValueAnimator.ofInt(initialHeight, 0).apply {
            this.duration = duration
            interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
            addUpdateListener { animator ->
                val valAnim = animator.animatedValue as Int
                if (valAnim > 0) {
                    layoutParams.height = valAnim
                }
                if (valAnim < 10) {
                    alpha = 0f
                }
                requestLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
            start()
        }

        animate().alpha(0f).setDuration((duration * 0.5).toLong()).start()
    }
}