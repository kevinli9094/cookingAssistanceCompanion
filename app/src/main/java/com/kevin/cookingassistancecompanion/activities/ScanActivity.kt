package com.kevin.cookingassistancecompanion.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kevin.cookingassistancecompanion.CameraManager
import com.kevin.cookingassistancecompanion.CameraOverlay
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.databinding.ActivityScanBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class ScanActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ScanActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    lateinit var cameraView: PreviewView
    lateinit var overlayView: CameraOverlay
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityScanBinding.inflate(layoutInflater)

        cameraView = binding.cameraView
        overlayView = binding.overlay

        setContentView(binding.root)

        setupDoneButton(binding)
        cameraManager = CameraManager(this, cameraView, overlayView)

        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        loadAssetToDatabase()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
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

    private fun setupDoneButton(binding: ActivityScanBinding) {
        binding.doneButton.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Use this to load asset file to database
     */
    private fun loadAssetToDatabase() {
        val datastore = RealmItemNamesDatastore()

        GlobalScope.launch(Dispatchers.IO) {
            if (datastore.getTAndTItemNames().isEmpty()) {
                return@launch
            }
            var reader: BufferedReader? = null
            val mutableSet = mutableSetOf<String>()
            try {
                reader = BufferedReader(
                    InputStreamReader(assets.open("items.txt"))
                )

                // do reading, usually loop until end of file reading
                var mLine = reader.readLine()
                while (mLine != null) {
                    Log.i(TAG, mLine)
                    mutableSet.add(mLine)
                    mLine = reader.readLine()
                }
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (e: IOException) {
                        Log.e(TAG, e.stackTraceToString())
                    }
                }
            }

            datastore.insertTAndTItemNames(mutableSet.toList())
        }
    }
}