package com.kevin.cookingassistancecompanion.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannedResultItemViewModel(
    text: String,
    editable: Boolean = false
) : ResultItemViewModel(text = text, itemType = ITEM_TYPE_RESULT){

    val editableObservable = MutableLiveData(editable)

    fun done(){
        Log.i("KL:", "${this}this.done is called. has active observer = ${editableObservable.hasActiveObservers()}. has observer = ${editableObservable.hasObservers()}")
        editableObservable.value = false
        // todo: need to add to database
    }
}