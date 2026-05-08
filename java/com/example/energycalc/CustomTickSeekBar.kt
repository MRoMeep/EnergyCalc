package com.example.energycalc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar

class CustomTickSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#555555")
    }
    private val tickRadius = 2 * resources.displayMetrics.density

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (max > 0) {
            val w = width - paddingLeft - paddingRight
            val h = height / 2f
            val spacing = w.toFloat() / max

            canvas.save()
            if (thumb != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    canvas.clipOutRect(thumb.bounds)
                } else {
                    @Suppress("DEPRECATION")
                    canvas.clipRect(thumb.bounds, android.graphics.Region.Op.DIFFERENCE)
                }
            }

            for (i in 0..max) {
                if (i > progress) {
                    val cx = paddingLeft + (i * spacing)
                    canvas.drawCircle(cx, h, tickRadius, tickPaint)
                }
            }
            canvas.restore()
        }
    }
}