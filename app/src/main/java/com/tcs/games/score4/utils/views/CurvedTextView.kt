package com.tcs.games.score4.utils.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.tcs.games.score4.R

class CurvedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.purple_500) // Default color
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    private val path = Path()

    private var curvedText: String = "Default Curved Text" // Default text
    private var arcHeight = 100f // Default curvature

    // Center values, defaulting to NaN for calculation fallback
    private var centerX: Float = Float.NaN
    private var centerY: Float = Float.NaN

    init {
        // Load attributes from XML
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CurvedTextView, 0, 0)

            curvedText = typedArray.getString(R.styleable.CurvedTextView_text) ?: curvedText

            paint.color = typedArray.getColor(
                R.styleable.CurvedTextView_textColor,
                ContextCompat.getColor(context, R.color.purple_500)
            )

            paint.textSize = typedArray.getDimension(R.styleable.CurvedTextView_textSize, 60f)
            paint.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)

            arcHeight = typedArray.getDimension(R.styleable.CurvedTextView_arcHeight, 100f)

            // Read centerX and centerY attributes
            centerX = typedArray.getFloat(R.styleable.CurvedTextView_centerX, Float.NaN)
            centerY = typedArray.getFloat(R.styleable.CurvedTextView_centerY, Float.NaN)

            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Determine the center values
        val calculatedCenterX = if (centerX.isNaN()) width / 2f else centerX
        val calculatedCenterY = if (centerY.isNaN()) height / 2f else centerY
        Log.d("Curved Text View","$calculatedCenterY and $calculatedCenterX")

        // Create a horizontal arc path
        path.reset()
        path.addArc(
            calculatedCenterX - width / 2f, // Left
            calculatedCenterY - arcHeight / 2f, // Top
            calculatedCenterX + width / 2f, // Right
            calculatedCenterY + arcHeight / 2f, // Bottom
            180f, // Start angle
            180f  // Sweep angle (horizontal arc)
        )

        // Draw text along the horizontal arc
        canvas.drawTextOnPath(curvedText, path, 0f, 0f, paint)
    }

    /**
     * Sets the text to be displayed and redraws the view.
     */
    fun setText(newText: String) {
        curvedText = newText
        invalidate() // Redraw the view
    }

    /**
     * Sets the arc height to adjust curvature and redraws the view.
     */
    fun setArcHeight(newArcHeight: Float) {
        arcHeight = newArcHeight
        invalidate() // Redraw the view
    }

    /**
     * Sets the arc's center X coordinate and redraws the view.
     */
    fun setCenterX(newCenterX: Float) {
        centerX = newCenterX
        invalidate() // Redraw the view
    }

    /**
     * Sets the arc's center Y coordinate and redraws the view.
     */
    fun setCenterY(newCenterY: Float) {
        centerY = newCenterY
        invalidate() // Redraw the view
    }
}
