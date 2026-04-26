package com.tcs.games.score4.ui.selectimage

import android.graphics.PointF
import android.graphics.RectF

data class CropParams(
    val cropMode: CropMode = CropMode.CIRCLE,
    val cropCircleCenter: PointF = PointF(400f, 400f),
    val cropCircleRadius: Float = 250f,
    val cropRect: RectF = RectF(100f, 100f, 400f, 500f)
) {
    enum class CropMode {
        CIRCLE, RECTANGLE
    }
}
