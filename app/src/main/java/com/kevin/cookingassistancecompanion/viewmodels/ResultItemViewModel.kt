package com.kevin.cookingassistancecompanion.viewmodels

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class ResultItemViewModel(
    text: String,
    val itemType : Int
):ViewModel(), LifecycleOwner {

    companion object{
        const val ITEM_TYPE_RESULT = 1
        const val ITEM_TYPE_BUTTON = 2
    }
    val textObservable = MutableLiveData(text)
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun destroyLifecycle(){
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun onCleared() {
        super.onCleared()
        destroyLifecycle()
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}