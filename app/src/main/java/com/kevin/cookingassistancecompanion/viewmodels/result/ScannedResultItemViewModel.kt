package com.kevin.cookingassistancecompanion.viewmodels.result

import androidx.lifecycle.MutableLiveData
import com.kevin.cookingassistancecompanion.utility.ItemIngredientConverter

class ScannedResultItemViewModel constructor(
    text: String,
    converter: ItemIngredientConverter,
    editable: Boolean = false
) : ResultItemViewModel(text = text, itemType = ITEM_TYPE_RESULT) {

    val editableObservable = MutableLiveData(editable)
    val convertedTextObservable = MutableLiveData("")
    val spinnerEntriesObservable = MutableLiveData<List<String>>()
    val convertedObservable = MutableLiveData(false)
    val foregroundAlphaObservable = MutableLiveData(1f)
    val foregroundTranslationXObservable = MutableLiveData(0f)

    init {
        textObservable.observe(this){
            val convertedResult = converter.covert(it).sortedIngredient
            if(convertedResult != null && convertedResult.isNotEmpty()){
                convertedTextObservable.postValue(convertedResult.firstOrNull()?.ingredient)
                spinnerEntriesObservable.postValue(convertedResult.map { it.ingredient })
                convertedObservable.postValue(true)
            }
        }
    }

    fun done() {
        editableObservable.value = false
        // todo: need to add to database
    }
}