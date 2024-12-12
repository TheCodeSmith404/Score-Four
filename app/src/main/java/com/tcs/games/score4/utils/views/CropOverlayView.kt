package com.tcs.games.score4.utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.min

class CropOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class CropMode {
        CIRCLE, RECTANGLE
    }

    var cropMode = CropMode.CIRCLE
        set(value) {
            field = value
            invalidate()
        }

    var cropCircleCenter = PointF(400f, 400f)
    var cropCircleRadius = dpToPx(96f) // Minimum circle radius (96dp)
    var cropRect = RectF(
        dpToPx(270f), dpToPx(200f), dpToPx(270f) + dpToPx(270f), dpToPx(200f) + dpToPx(360f)
    ) // Minimum rectangle size (270dp x 360dp)

    private var isResizing = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#80000000") // Semi-transparent overlay
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Create a temporary bitmap and canvas to apply transparency
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(bitmap)

        // Draw the semi-transparent overlay on the temporary canvas
        tempCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        if (cropMode == CropMode.CIRCLE) {
            // Clear the circle area
            tempCanvas.drawCircle(cropCircleCenter.x, cropCircleCenter.y, cropCircleRadius, clearPaint)
            // Draw circle border
            tempCanvas.drawCircle(cropCircleCenter.x, cropCircleCenter.y, cropCircleRadius, borderPaint)
        } else {
            // Clear the rectangle area
            tempCanvas.drawRect(cropRect, clearPaint)
            // Draw rectangle border
            tempCanvas.drawRect(cropRect, borderPaint)
        }

        // Draw the temporary bitmap onto the actual canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        bitmap.recycle() // Clean up the bitmap to avoid memory leaks
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isResizing = when (cropMode) {
                    CropMode.CIRCLE -> isNearCircleEdge(event.x, event.y)
                    CropMode.RECTANGLE -> isNearEdge(event.x, event.y, cropRect)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY

                if (isResizing) {
                    when (cropMode) {
                        CropMode.CIRCLE -> {
                            cropCircleRadius = hypot(
                                (event.x - cropCircleCenter.x).toDouble(),
                                (event.y - cropCircleCenter.y).toDouble()
                            ).toFloat().coerceIn(minCircleRadius(), maxRadius())
                        }
                        CropMode.RECTANGLE -> {
                            val newWidth = cropRect.width() + dx
                            val newHeight = newWidth * 4 / 3 // Maintain 3:4 aspect ratio

                            // Apply minimum size constraint for the rectangle
                            cropRect.right = (cropRect.left + newWidth).coerceAtLeast(minRectWidth())
                            cropRect.bottom = (cropRect.top + newHeight).coerceAtLeast(minRectHeight())
                            cropRect.right = cropRect.right.coerceAtMost(width.toFloat())
                            cropRect.bottom = cropRect.bottom.coerceAtMost(height.toFloat())
                        }
                    }
                } else {
                    when (cropMode) {
                        CropMode.CIRCLE -> moveCircle(dx, dy)
                        CropMode.RECTANGLE -> moveRect(dx, dy)
                    }
                }

                lastTouchX = event.x
                lastTouchY = event.y
                invalidate()
            }
        }
        return true
    }

    private fun moveCircle(dx: Float, dy: Float) {
        cropCircleCenter.x = (cropCircleCenter.x + dx).coerceIn(cropCircleRadius, width - cropCircleRadius)
        cropCircleCenter.y = (cropCircleCenter.y + dy).coerceIn(cropCircleRadius, height - cropCircleRadius)
    }

    private fun moveRect(dx: Float, dy: Float) {
        cropRect.offset(dx, dy)
        cropRect.offsetTo(
            cropRect.left.coerceIn(0f, width - cropRect.width()),
            cropRect.top.coerceIn(0f, height - cropRect.height())
        )
    }

    private fun maxRadius(): Float {
        return min(
            min(cropCircleCenter.x, cropCircleCenter.y),
            min(width - cropCircleCenter.x, height - cropCircleCenter.y)
        )
    }

    private fun minCircleRadius(): Float {
        return dpToPx(96f) // 96dp minimum circle radius
    }

    private fun minRectWidth(): Float {
        return dpToPx(270f) // 270dp minimum width
    }

    private fun minRectHeight(): Float {
        return dpToPx(360f) // 360dp minimum height
    }

    private fun isNearCircleEdge(x: Float, y: Float): Boolean {
        val distance = hypot((x - cropCircleCenter.x).toDouble(), (y - cropCircleCenter.y).toDouble())
        return distance > cropCircleRadius - 30 && distance < cropCircleRadius + 30
    }

    private fun isNearEdge(x: Float, y: Float, rect: RectF): Boolean {
        val edgeTolerance = 30f
        return (x in rect.left - edgeTolerance..rect.left + edgeTolerance
                || x in rect.right - edgeTolerance..rect.right + edgeTolerance
                || y in rect.top - edgeTolerance..rect.top + edgeTolerance
                || y in rect.bottom - edgeTolerance..rect.bottom + edgeTolerance)
    }

    // Convert DP to PX
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
