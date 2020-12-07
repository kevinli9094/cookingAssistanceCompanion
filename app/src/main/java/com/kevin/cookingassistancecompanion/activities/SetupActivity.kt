package com.kevin.cookingassistancecompanion.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.kevin.cookingassistancecompanion.databinding.ActivitySetupBinding
import com.kevin.cookingassistancecompanion.viewmodels.ViewModelFactory
import com.kevin.cookingassistancecompanion.viewmodels.setup.SetupActivityViewModel

/**
 * Activity for the setup page
 */
class SetupActivity : AppCompatActivity() {

    /**
     * this method is being used in [SetupActivityViewModel] to hide soft keyboard
     */
    private val hideKeyboard: () -> Unit = {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySetupBinding.inflate(layoutInflater)
        val model = ViewModelFactory(this).create(SetupActivityViewModel::class.java)
        model.hideKeyBoardMethod = hideKeyboard
        binding.lifecycleOwner = this
        binding.model = model
        setContentView(binding.root)
    }
}