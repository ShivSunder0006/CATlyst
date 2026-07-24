package com.example.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.TypedValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetHelpers {
    fun createProgressBitmap(
        context: Context,
        progress: Float,
        sizePx: Int,
        strokeWidthPx: Float
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Resolve primary color (fallback to a deep purple)
        val typedValue = TypedValue()
        val theme = context.theme
        val primaryColor = if (theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)) {
            typedValue.data
        } else {
            0xFF6200EE.toInt()
        }
        
        val trackColor = 0x33888888 // semi-transparent gray
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            strokeCap = Paint.Cap.ROUND
        }
        
        val padding = strokeWidthPx / 2f + 4f
        val rect = RectF(
            padding,
            padding,
            sizePx - padding,
            sizePx - padding
        )
        
        paint.color = trackColor
        canvas.drawArc(rect, 0f, 360f, false, paint)
        
        paint.color = primaryColor
        canvas.drawArc(rect, -90f, 360f * progress, false, paint)
        
        return bitmap
    }
    
    fun updateAllWidgets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            DailyProgressWidget().updateAll(context)
            QuickSessionWidget().updateAll(context)
            DashboardWidget().updateAll(context)
        }
    }
}
