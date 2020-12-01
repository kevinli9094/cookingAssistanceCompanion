package com.kevin.cookingassistancecompanion.viewmodels.storeselection

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.StoreSelectionCoordinator

class StoreSelectionViewModel(private val coordinator: StoreSelectionCoordinator) : ViewModel() {
    @Suppress("UNCHECKED_CAST")
    private class CustomViewModelFactory(private val coordinator: StoreSelectionCoordinator) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StoreSelectionViewModel(coordinator) as T
        }
    }
    companion object{
        fun getViewModel(activity: AppCompatActivity): StoreSelectionViewModel{
            val coordinator = StoreSelectionCoordinator(activity)
            return ViewModelProvider(activity, CustomViewModelFactory(coordinator))
                .get(StoreSelectionViewModel::class.java)
        }
    }


    val selectedType = MutableLiveData(ScanningResult.resultType.str)

    val scanType: List<String> = ScanningResult.ResultType.values().map { it.str }

    fun onScanClicked() {
        ScanningResult.resultType = ScanningResult.ResultType.strToResultType(selectedType.value!!)
        coordinator.openScanActivity()
    }
}