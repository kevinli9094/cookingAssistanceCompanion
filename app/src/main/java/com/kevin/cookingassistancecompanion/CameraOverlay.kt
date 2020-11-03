package com.kevin.cookingassistancecompanion

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.mlkit.vision.text.Text
import kotlin.math.min

class CameraOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object{
        const val TAG = "CameraOverlayCustomView"
    }

    private val rectPaint = Paint()
    private var rects = mutableListOf<RectF>()
    private var scaleFactor = 1.0f
    private var postScaleWidthOffset = 0f
    private var postScaleHeightOffset = 0f

    init {
        rectPaint.color = Color.BLACK
        rectPaint.strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(rects.isNotEmpty()){
            rects.forEach { rect ->
                canvas?.drawRect(rect, rectPaint)
            }
        }

    }

    fun drawBlocks(textBlocks: List<Text.TextBlock>, imageWidth: Int, imageHeight: Int){
        Log.i(TAG, "overlay left: ${left}. top: ${top}. right : ${right}, bottom: ${bottom}")
        Log.i(TAG, "image width = $imageWidth and image height = ${imageHeight}")
        updateScaleAndOffsets(imageWidth = imageWidth, imageHeight = imageHeight)
        rects.clear()
        for (textBlock in textBlocks) {
            for (line in textBlock.lines){
                val rect = RectF(line.boundingBox)

                rect.left = translateX(rect.left)
                rect.right = translateX(rect.right)
                rect.top = translateY(rect.top)
                rect.bottom = translateY(rect.bottom)
                rects.add(rect)
            }
        }
        invalidate()
    }

    private fun updateScaleAndOffsets(imageWidth: Int, imageHeight: Int){
        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio = imageWidth.toFloat() / imageHeight
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = width.toFloat() / imageWidth
            postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 2
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = height.toFloat() / imageHeight
            postScaleWidthOffset = (height.toFloat() * imageAspectRatio - width) / 2
        }

        Log.i(TAG, "updated scale and offsets. scaleFactor = $scaleFactor, postScaleHeightOffset = $postScaleHeightOffset, and postScaleWidthOffset = $postScaleWidthOffset")
    }

    private fun translateX(x: Float): Float {
        return scale(x) - postScaleWidthOffset
    }

    private fun translateY(y: Float): Float {
        return scale(y) - postScaleHeightOffset
    }

    private fun scale(imagePixel: Float): Float {
        return imagePixel * scaleFactor
    }
}