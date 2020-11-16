package com.kevin.cookingassistancecompanion.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intuit.fuzzymatcher.component.MatchService
import com.intuit.fuzzymatcher.domain.Document
import com.intuit.fuzzymatcher.domain.Document.Builder
import com.intuit.fuzzymatcher.domain.Element
import com.intuit.fuzzymatcher.domain.ElementType
import com.intuit.fuzzymatcher.domain.MatchType
import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore

class ResultActivityViewModel : ViewModel() {
    companion object{
        const val TAG = "ResultActivityViewModel"
    }

    private val mutableData: MutableLiveData<List<ResultItemViewModel>> by lazy {
        MutableLiveData(emptyList())
    }

    private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    init {
        processData()
    }
    private fun processData(){
        Log.i(TAG, "start time")
        val outputResult = ScanningResult.getSortedResult()
        val documentList: List<Document> =
            outputResult.mapIndexed { index, value ->
                Builder(index.toString())
                    .addElement(
                        Element.Builder<String>()
                            .setValue(value)
                            .setType(ElementType.TEXT)
                            .setMatchType(MatchType.EQUALITY)
                            .setPreProcessingFunction {
                                return@setPreProcessingFunction it
                            }
                            .setThreshold(0.9)
                            .createElement()
                    )
                    .createDocument()
            }

        val correctItemList = RealmItemNamesDatastore().getTAndTItemNames()
        val existingDoc = correctItemList.mapIndexed { index, value ->
            Builder(index.toString())
                .addElement(
                    Element.Builder<String>()
                        .setValue(value)
                        .setType(ElementType.TEXT)
                        .setMatchType(MatchType.EQUALITY)
                        .setPreProcessingFunction {
                            return@setPreProcessingFunction it
                        }
                        .setThreshold(0.9)
                        .createElement()
                )
                .createDocument()
        }

        val matchService = MatchService()
        val result = matchService.applyMatch(existingDoc, documentList)

        val viewModels: List<ResultItemViewModel> = result.map {
            ResultItemViewModel(it.key.elements.first().value as String)
        }

        mutableData.value = viewModels
        mutableIsLoading.value = false
        Log.i(TAG, "end time")
    }

    fun getData(): LiveData<List<ResultItemViewModel>> {
        return mutableData
    }

    fun getIsLoading(): LiveData<Boolean>{
        return mutableIsLoading
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

    }

    fun discardRest() {

    }

    fun saveRest() {

    }

    fun exitApp() {

    }

    fun scanAgain() {

    }
}