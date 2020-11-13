package com.kevin.cookingassistancecompanion

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.kevin.cookingassistancecompanion.models.DrawInfo

class CameraOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "CameraOverlayCustomView"

        private const val TEXT_COLOR = Color.BLACK
        private const val MARKER_COLOR = Color.WHITE
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }

    private val rectPaint = Paint()
    private val labelPaint = Paint()
    private val textPaint = Paint()
    private val drawInfos = mutableListOf<DrawInfo>()
    private var scaleFactor = 1.0f
    private var postScaleWidthOffset = 0f
    private var postScaleHeightOffset = 0f

    init {
        rectPaint.color = MARKER_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH

        labelPaint.color = MARKER_COLOR
        labelPaint.style = Paint.Style.FILL

        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (drawInfos.isNotEmpty()) {
            drawInfos.forEach { drawInfo ->
                val rect = drawInfo.rectF
                val text = drawInfo.text
                canvas?.drawRect(rect, rectPaint)

                val lineHeight =
                    TEXT_SIZE + 2 * STROKE_WIDTH
                val textWidth = textPaint.measureText(text)

                canvas?.drawRect(
                    rect.left - STROKE_WIDTH,
                    rect.top - lineHeight,
                    rect.left + textWidth + 2 * STROKE_WIDTH,
                    rect.top,
                    labelPaint
                )
                // Renders the text at the bottom of the box.
                canvas?.drawText(
                    text,
                    rect.left,
                    rect.top - STROKE_WIDTH,
                    textPaint
                )
            }
        }

    }

    fun drawBlocks(textBlocks: List<DrawInfo>, imageWidth: Int, imageHeight: Int) {
        Log.i(TAG, "overlay left: ${left}. top: ${top}. right : ${right}, bottom: ${bottom}")
        Log.i(TAG, "image width = $imageWidth and image height = ${imageHeight}")
        updateScaleAndOffsets(imageWidth = imageWidth, imageHeight = imageHeight)
        drawInfos.clear()
        textBlocks.forEach { drawInfo ->
            val rect = drawInfo.rectF

            rect.left = translateX(rect.left)
            rect.right = translateX(rect.right)
            rect.top = translateY(rect.top)
            rect.bottom = translateY(rect.bottom)


            drawInfos.add(DrawInfo(rect, drawInfo.text))
        }
        invalidate()
    }

    private fun updateScaleAndOffsets(imageWidth: Int, imageHeight: Int) {
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

        Log.i(
            TAG,
            "updated scale and offsets. scaleFactor = $scaleFactor, postScaleHeightOffset = $postScaleHeightOffset, and postScaleWidthOffset = $postScaleWidthOffset"
        )
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