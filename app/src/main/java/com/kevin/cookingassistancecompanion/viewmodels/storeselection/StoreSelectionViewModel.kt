package com.kevin.cookingassistancecompanion.viewmodels.storeselection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.coordinators.StoreSelectionCoordinator

/**
 * ViewModel for store selection screen
 */
class StoreSelectionViewModel(private val coordinator: StoreSelectionCoordinator) : ViewModel() {

    val selectedType = MutableLiveData(ScanningResult.resultType.str)

    val scanType: List<String> = ScanningResult.ResultType.values().map { it.str }

    fun onScanClicked() {
        ScanningResult.resultType = ScanningResult.ResultType.strToResultType(selectedType.value!!)
        coordinator.openScanActivity()
    }
}