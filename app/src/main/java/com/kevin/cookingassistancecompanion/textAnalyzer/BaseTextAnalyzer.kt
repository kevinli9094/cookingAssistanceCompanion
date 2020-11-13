package com.kevin.cookingassistancecompanion.textAnalyzer

import com.google.mlkit.vision.text.Text
import com.kevin.cookingassistancecompanion.CameraOverlay
import com.kevin.cookingassistancecompanion.models.DrawInfo

data class ImageBaseInfo(val width: Int, val height: Int, val rotationDegree: Int)

abstract class BaseTextAnalyzer(private val overlay: CameraOverlay) {

    abstract fun processText(visionText: Text, imageInfo: ImageBaseInfo)

    protected fun updateOverlay(drawInfos: List<DrawInfo>, imageInfo: ImageBaseInfo) {
        val rotationDegree = imageInfo.rotationDegree

        if (rotationDegree == 0 || rotationDegree == 180) {
            overlay.drawBlocks(
                drawInfos,
                imageWidth = imageInfo.width,
                imageHeight = imageInfo.height
            )
        } else {
            overlay.drawBlocks(
                drawInfos,
                imageHeight = imageInfo.width,
                imageWidth = imageInfo.height
            )
        }
    }
}