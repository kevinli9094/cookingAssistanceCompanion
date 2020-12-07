package com.kevin.cookingassistancecompanion.textAnalyzer

import androidx.annotation.VisibleForTesting
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.text.Text
import com.kevin.cookingassistancecompanion.CameraOverlay
import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.models.DrawInfo

/**
 * TextAnalyzer for T&T item
 */
class TAndTTextAnalyzer(overlay: CameraOverlay): BaseTextAnalyzer(overlay) {

    companion object {
        const val TAG = "TAndTTextAnalyzer"
    }

    private val filterPriceRegex = Regex("^(\$|[0-9$]|[wWvVuU] ?[\$sS]| ?[\$sS] ?[0-9]).*")
    private val filterTitleRegex = Regex("^(pr[0o]duce|[mn]eat|gr[0o]cery|deli)$", RegexOption.IGNORE_CASE)
    private val filterErrorRegex = Regex("^([a-z]|zh|\\(a|w\\\\)$", RegexOption.IGNORE_CASE)

    override fun processText(visionText: Text, imageInfo: ImageBaseInfo) {
        val drawInfos = mutableListOf<DrawInfo>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text
                if(!filteredString(text)){
                    val boundingBox = line.boundingBox
                    if(boundingBox != null){
                        drawInfos.add(DrawInfo(boundingBox.toRectF(), text))
                        ScanningResult.addString(convertString(text))
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
        return filterPriceRegex.matches(text)
                || filterTitleRegex.matches(text)
                || filterErrorRegex.matches(text)
    }

    private val onSaleRegex = Regex("\\(.*\\)")
    private val zeroRegex = Regex("0")
    @VisibleForTesting
    fun convertString(text: String):String{

        var temp = onSaleRegex.replace(text,"")
            .trim()
        temp = zeroRegex.replace(temp, "O")
        return temp
    }
}