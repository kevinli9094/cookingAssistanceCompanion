package com.kevin.cookingassistancecompanion.textAnalyzer

import com.kevin.cookingassistancecompanion.CameraOverlay
import com.kevin.cookingassistancecompanion.ScanningResult

/**
 * Factory class for [BaseTextAnalyzer]
 */
class TextAnalyzerFactory {
    fun getTextAnalyzer(overlay: CameraOverlay): BaseTextAnalyzer {
        return when (ScanningResult.resultType) {
            ScanningResult.ResultType.TANDT_CHINESE,
            ScanningResult.ResultType.TANDT_ENGLISH -> {
                TAndTTextAnalyzer(overlay)
            }
            else -> throw IllegalStateException("Unrecognized Scan type")
        }
    }
}