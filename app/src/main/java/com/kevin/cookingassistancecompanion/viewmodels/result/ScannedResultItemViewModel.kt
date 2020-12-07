package com.kevin.cookingassistancecompanion.viewmodels.result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.lifecycleScope
import com.kevin.cookingassistancecompanion.models.ItemConvertedResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Abstract class for ScannedResutlItemViewModels
 */
abstract class ScannedResultItemViewModel constructor(
    text: String,
    editable: Boolean = false
) : ResultItemViewModel(text = text, itemType = ITEM_TYPE_RESULT) {

    val itemNameEditableObservable = MutableLiveData(editable)
    val ingredientEditableObservable = MutableLiveData(editable)
    val spinnerEntriesObservable = MutableLiveData<List<String>>()
    val convertedObservable = MutableLiveData(false)
    val foregroundAlphaObservable = MutableLiveData(1f)
    val foregroundTranslationXObservable = MutableLiveData(0f)

    /**
     * This needs to be called by child class since [convert] will use object that will only be
     * initialized after init{} is called
     */
    protected fun init(){
        textObservable.observe(this) {
            lifecycleScope.launch(Dispatchers.Default) {
                val convertedResult = convert(it).sortedIngredient
                if (convertedResult != null && convertedResult.isNotEmpty()) {
                    convertedTextObservable.postValue(convertedResult.firstOrNull()?.ingredient)
                    spinnerEntriesObservable.postValue(convertedResult.map { it.ingredient })
                    convertedObservable.postValue(true)
                }
            }
        }

        Transformations.distinctUntilChanged(convertedTextObservable).observe(this) {
            setIngredient(it)
        }
    }

    fun editIngredient(){
        ingredientEditableObservable.postValue(true)
        convertedObservable.postValue(false)
    }

    fun doneAddNew() {
        val itemText = textObservable.value
        if (itemText == null) {
            // todo: show user some message
            return
        }
        itemNameEditableObservable.postValue(false)
        ingredientEditableObservable.postValue(false)
        lifecycleScope.launch(Dispatchers.Default) {
            insertSingleItem(itemText)
        }

        val selectedValue = convertedTextObservable.value
        if (selectedValue != null && selectedValue.isNotBlank()) {
            addMissingIngredient()
        }
    }

    fun editMissingIngredient() {
        ingredientEditableObservable.postValue(true)
    }

    fun addMissingIngredient() {
        val ingredientText = convertedTextObservable.value
        val itemText = textObservable.value
        if (ingredientText != null && itemText != null) {
            lifecycleScope.launch(Dispatchers.Default) {
                insertNewIngredientMapping(itemText, ingredientText)
                ingredientEditableObservable.postValue(false)
                convertedObservable.postValue(true)
            }
        } else {
            // todo: show user some message
            Log.w(TAG, "ingredient field is missing while trying to add new ingredient")
        }
    }

    private fun setIngredient(ingredient: String) {
        if (ingredient.isBlank()) {
            return
        }
        val currentEntries = spinnerEntriesObservable.value
        if (currentEntries == null || !currentEntries.contains(ingredient)) {
            val newList = mutableListOf<String>()
            newList.add(ingredient)
            if (currentEntries != null && currentEntries.isNotEmpty()) {
                newList.addAll(currentEntries)
            }
            spinnerEntriesObservable.postValue(newList)
            convertedTextObservable.postValue(ingredient)
        }

    }

    /**
     * Insert a new mapping between an item and a ingredient. Should also add the ingredient if it's
     * not already present.
     */
    protected abstract fun insertNewIngredientMapping(itemText: String, ingredient: String)

    /**
     * Insert an single item to correct database
     */
    protected abstract fun insertSingleItem(itemText: String)

    /**
     * Should return a [ItemConvertedResult] that contains data of a list of ingredients matches itemText
     */
    protected abstract fun convert(itemText: String): ItemConvertedResult
}