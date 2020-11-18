package com.kevin.cookingassistancecompanion.viewmodels

import androidx.lifecycle.MutableLiveData

class ScannedResultItemViewModel(
    text: String,
    editable: Boolean = false
) : ResultItemViewModel(text = text, itemType = ITEM_TYPE_RESULT) {

    val editableObservable = MutableLiveData(editable)

    fun done() {
        editableObservable.value = false
        // todo: need to add to database
    }
}