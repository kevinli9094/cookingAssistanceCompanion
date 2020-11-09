package com.kevin.cookingassistancecompanion.textAnalyzer

import androidx.annotation.VisibleForTesting
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.text.Text
import com.kevin.cookingassistancecompanion.CameraOverlay
import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.models.DrawInfo

class TAndTTextAnalyzer(overlay: CameraOverlay): BaseTextAnalyzer(overlay) {

    companion object {
        const val TAG = "TAndTTextAnalyzer"
    }

    private val filterRegex = Regex("^([0-9$]|[wWuU] ?[\$sS]).*")

    override fun processText(visionText: Text, imageInfo: ImageBaseInfo) {
        val drawInfos = mutableListOf<DrawInfo>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text
                if(!filteredString(text)){
                    val boundingBox = line.boundingBox
                    if(boundingBox != null){
                        drawInfos.add(DrawInfo(boundingBox.toRectF(), text))
                        ScanningResult.incrementKey(text)
                    }
                }
            }
        }

        updateOverlay(drawInfos, imageInfo)
    }

    /**
     * Remove price by ignoring string that only contains
     * - start with one of ['w$', '$', 'w $']
     * - or start with a number
     */
    @VisibleForTesting
    fun filteredString(text: String):Boolean{
        return filterRegex.matches(text)
    }
}