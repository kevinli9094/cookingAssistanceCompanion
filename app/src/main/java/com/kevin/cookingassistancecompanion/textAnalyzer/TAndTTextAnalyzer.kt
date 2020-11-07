package com.kevin.cookingassistancecompanion.textAnalyzer

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.text.Text
import com.kevin.cookingassistancecompanion.CameraOverlay
import com.kevin.cookingassistancecompanion.models.DrawInfo

class TAndTTextAnalyzer(overlay: CameraOverlay): BaseTextAnalyzer(overlay) {

    companion object {
        const val TAG = "TAndTTextAnalyzer"
    }

    private val mutableMap = mutableMapOf<String, Int>()
    private val filterRegex = Regex("^([0-9$]|[wWuU] ?[\$sS]).*")

    @Synchronized
    private fun incrementKey(key: String) {
        mutableMap.compute(key) { _, oldValue ->
            return@compute if (oldValue == null) {
                1
            } else {
                oldValue + 1
            }
        }
    }

    override fun processText(visionText: Text, imageInfo: ImageBaseInfo) {
        Log.i(TAG, "new image")
        val drawInfos = mutableListOf<DrawInfo>()
        for (block in visionText.textBlocks) {
            Log.i(TAG, "new block")
            for (line in block.lines) {
                val text = line.text
                if(!filteredString(text)){
                    Log.i(TAG, "line: " + line.text)
                    val boundingBox = line.boundingBox
                    if(boundingBox != null){
                        drawInfos.add(DrawInfo(boundingBox.toRectF(), text))
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