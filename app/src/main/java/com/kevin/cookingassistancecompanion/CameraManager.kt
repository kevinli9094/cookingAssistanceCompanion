package com.kevin.cookingassistancecompanion

import android.util.Log
import android.util.Size
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.kevin.cookingassistancecompanion.textAnalyzer.TextAnalyzerFactory
import com.kevin.cookingassistancecompanion.utility.afterMeasured
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Class that handles the setup of the camera, including which analyzer to user and auto focus
 */
class CameraManager(
    private val activity: AppCompatActivity,
    private val cameraView: PreviewView,
    private val overlayView: CameraOverlay,
    private val autoFocusIntervalSec: Long = 1
) {

    companion object {
        const val TAG = "CameraManager"
    }

    private val executor = Executors.newSingleThreadExecutor()

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

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
                .setTargetResolution(Size(720, 1280))
                .build()
                .also {
                    it.setAnalyzer(
                        executor,
                        ImageAnalyzer(TextAnalyzerFactory().getTextAnalyzer(overlayView))
                    )
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    activity, cameraSelector, preview, imageAnalyzer
                )

                focusOnTouch(camera)
                autoFocus(camera)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    private fun autoFocus(camera: Camera) {
        cameraView.afterMeasured {
            val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                cameraView.width.toFloat(), cameraView.height.toFloat()
            )
            val centerWidth = cameraView.width.toFloat() / 2
            val centerHeight = cameraView.height.toFloat() / 2
            //create a point on the center of the view
            val autoFocusPoint = factory.createPoint(centerWidth, centerHeight)
            try {
                camera.cameraControl.startFocusAndMetering(
                    FocusMeteringAction.Builder(
                        autoFocusPoint,
                        FocusMeteringAction.FLAG_AF
                    ).apply {
                        //auto-focus every 1 seconds
                        setAutoCancelDuration(autoFocusIntervalSec, TimeUnit.SECONDS)
                    }.build()
                )
            } catch (e: CameraInfoUnavailableException) {
                Log.d("ERROR", "cannot access camera", e)
            }
        }
    }


    private fun focusOnTouch(camera: Camera) {
        cameraView.afterMeasured {
            cameraView.setOnTouchListener { _, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            cameraView.width.toFloat(), cameraView.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        try {
                            camera.cameraControl.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()
                            )
                        } catch (e: CameraInfoUnavailableException) {
                            Log.d(TAG, "cannot access camera", e)
                        }
                        true
                    }
                    else -> false // Unhandled event.
                }
            }
        }
    }
}