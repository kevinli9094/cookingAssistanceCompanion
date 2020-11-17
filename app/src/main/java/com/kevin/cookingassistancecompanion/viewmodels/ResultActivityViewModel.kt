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
import kotlin.math.max

class ResultActivityViewModel : ViewModel() {
    companion object{
        const val TAG = "ResultActivityViewModel"
    }

    private val mutableData: MutableLiveData<List<ResultItemViewModel>> by lazy {
        MutableLiveData(emptyList())
    }

    private val mutableDataList = mutableListOf<ResultItemViewModel>()

    private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    init {
        processData()
    }
    private fun processData(){
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

        val viewModelScanneds: List<ScannedResultItemViewModel> = result.map {
            ScannedResultItemViewModel(it.key.elements.first().value as String)
        }

        mutableDataList.addAll(viewModelScanneds)
        mutableDataList.add(ButtonResultItemViewModel("Add"))
        mutableData.value = mutableDataList
        mutableIsLoading.value = false
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
        mutableDataList[index].destroyLifecycle()
        mutableDataList.removeAt(index)
        mutableData.value = mutableDataList
    }

    fun addEditableItem(){
        mutableDataList.add(max(mutableDataList.size -1, 0), ScannedResultItemViewModel("", editable = true))
        mutableData.value = mutableDataList
    }

    fun save() {
        ScanningResult.setResult(mutableDataList.map { it.textObservable.value!! })
    }

    fun exitApp() {

    }

    fun scanAgain() {

    }
}