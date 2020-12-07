package com.kevin.cookingassistancecompanion.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kevin.cookingassistancecompanion.coordinators.ResultActivityCoordinator
import com.kevin.cookingassistancecompanion.coordinators.SetupCoordinator
import com.kevin.cookingassistancecompanion.coordinators.StoreSelectionCoordinator
import com.kevin.cookingassistancecompanion.data.SharePreferenceDatastore
import com.kevin.cookingassistancecompanion.viewmodels.result.ResultActivityViewModel
import com.kevin.cookingassistancecompanion.viewmodels.setup.SetupActivityViewModel
import com.kevin.cookingassistancecompanion.viewmodels.storeselection.StoreSelectionViewModel
import java.lang.IllegalStateException

/**
 * Custom view model factory class to help activity create view models
 */
class ViewModelFactory (private val activity: Activity) : ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){
            StoreSelectionViewModel::class.java ->{
                StoreSelectionViewModel(StoreSelectionCoordinator((activity)))
            }
            ResultActivityViewModel::class.java -> {
                ResultActivityViewModel(
                    ResultActivityCoordinator(activity),
                    SharePreferenceDatastore(activity)
                )
            }
            SetupActivityViewModel::class.java -> {
                SetupActivityViewModel(
                    SetupCoordinator(activity),
                    SharePreferenceDatastore(activity)
                )
            }
            else -> {throw IllegalStateException("unrecognized view model")}
        } as T
    }
}