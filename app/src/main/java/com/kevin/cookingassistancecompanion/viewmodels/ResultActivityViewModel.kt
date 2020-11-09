package com.kevin.cookingassistancecompanion.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kevin.cookingassistancecompanion.ScanningResult

class ResultActivityViewModel : ViewModel() {

    val data: MutableLiveData<List<ResultItemViewModel>> by lazy {
        MutableLiveData(toViewModels())
    }

    /**
     * map a string in [fromIndex] to [to] string and update result list
     */
    fun mapTo(fromIndex: Int, to: String) {
        //todo
    }

    /**
     * remove item from result list
     */
    fun remove(index: Int) {
        // todo
    }

    fun discardRest() {

    }

    fun saveRest() {

    }

    fun exitApp() {

    }

    fun scanAgain() {

    }


    /**
     * converts result list to viewmodels
     */
    private fun toViewModels(): List<ResultItemViewModel> {
        val result = ScanningResult.getSortedResult()
        return result.map {
            ResultItemViewModel(it)
        }
    }
}