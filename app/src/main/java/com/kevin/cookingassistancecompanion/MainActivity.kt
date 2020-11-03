package com.kevin.cookingassistancecompanion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.kevin.cookingassistancecompanion.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    lateinit var cameraView: PreviewView
    lateinit var overlayView: CameraOverlay
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        cameraView = binding.cameraView
        overlayView = binding.overlay

        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)



        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageTextAnalyzer(overlayView))
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private class ImageTextAnalyzer constructor(private val overlay: CameraOverlay) : ImageAnalysis.Analyzer {

        companion object{
            const val TAG = "ImageTextAnalyzer"
        }

        override fun analyze(imageProxy: ImageProxy) {
            Log.d(TAG, "analyzing image")
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
                Log.d(TAG, "image is null")
            }
        }

        private fun processImage(image: InputImage, closeable: AutoCloseable, rotationDegree: Int){
            val recognizer = TextRecognition.getClient()

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (rotationDegree == 0 || rotationDegree == 180) {
                        overlay.drawBlocks(
                            visionText.textBlocks,
                            imageWidth = image.width,
                            imageHeight = image.height
                        )
                    } else {
                        overlay.drawBlocks(
                            visionText.textBlocks,
                            imageHeight = image.width,
                            imageWidth = image.height
                        )
                    }
                    for (block in visionText.textBlocks) {

                        for (line in block.lines) {
                            Log.i(TAG, "line: " + line.text)
                        }
                    }
                    closeable.close()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "error while processing image", e)
                }
        }
    }
}