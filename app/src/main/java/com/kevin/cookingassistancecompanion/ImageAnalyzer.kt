package com.kevin.cookingassistancecompanion

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import com.kevin.cookingassistancecompanion.textAnalyzer.BaseTextAnalyzer
import com.kevin.cookingassistancecompanion.textAnalyzer.ImageBaseInfo

/**
 * ImageAnalyzer that takes the result from Mlkit and pass it to [textAnalyzer] and [CameraOverlay]
 */
class ImageAnalyzer constructor(
    private val textAnalyzer: BaseTextAnalyzer
) : ImageAnalysis.Analyzer {

    companion object {
        const val TAG = "ImageTextAnalyzer"
    }

    override fun analyze(imageProxy: ImageProxy) {
        @androidx.camera.core.ExperimentalGetImage
        val mediaImage = imageProxy.image

        @androidx.camera.core.ExperimentalGetImage
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            // Pass image to an ML Kit Vision API
            processImage(image, imageProxy, imageProxy.imageInfo.rotationDegrees)
        } else {
            Log.w(TAG, "image is null")
        }
    }

    private fun processImage(image: InputImage, closeable: AutoCloseable, rotationDegree: Int) {
        val recognizer = TextRecognition.getClient()


        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                textAnalyzer.processText(
                    visionText,
                    ImageBaseInfo(
                        width = image.width,
                        height = image.height,
                        rotationDegree = rotationDegree
                    )
                )
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "error while processing image", e)
            }
            .addOnCompleteListener {
                closeable.close()
            }
    }
}