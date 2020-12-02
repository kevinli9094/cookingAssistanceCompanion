package com.kevin.cookingassistancecompanion.viewmodels.result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.lifecycleScope
import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemIngredientMapDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.utility.ItemIngredientConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScannedResultItemViewModel constructor(
    text: String,
    converter: ItemIngredientConverter,
    private val ingredientsDatastore: RealmIngredientsDatastore,
    private val itemNamesDatastore: RealmItemNamesDatastore,
    private val itemIngredientMapDatastore: RealmItemIngredientMapDatastore,
    editable: Boolean = false
) : ResultItemViewModel(text = text, itemType = ITEM_TYPE_RESULT) {

    val itemNameEditableObservable = MutableLiveData(editable)
    val ingredientEditableObservable = MutableLiveData(editable)
    val convertedTextObservable = MutableLiveData("")
    val spinnerEntriesObservable = MutableLiveData<List<String>>()
    val convertedObservable = MutableLiveData(false)
    val foregroundAlphaObservable = MutableLiveData(1f)
    val foregroundTranslationXObservable = MutableLiveData(0f)

    init {
        textObservable.observe(this){
            lifecycleScope.launch(Dispatchers.Default) {
                val convertedResult = converter.convert(it).sortedIngredient
                if(convertedResult != null && convertedResult.isNotEmpty()){
                    convertedTextObservable.postValue(convertedResult.firstOrNull()?.ingredient)
                    Log.i(TAG, "KL: entries size = ${convertedResult.size}")
                    spinnerEntriesObservable.postValue(convertedResult.map { it.ingredient })
                    convertedObservable.postValue(true)
                }
            }
        }

        Transformations.distinctUntilChanged(convertedTextObservable).observe(this){
            setIngredient(it)
        }
    }

    fun done() {
        val itemText = textObservable.value
        if(itemText == null){
            // show user some message
            return
        }
        itemNameEditableObservable.postValue(false)
        ingredientEditableObservable.postValue(false)
        lifecycleScope.launch(Dispatchers.Default){
            itemNamesDatastore.insertSingleTAndTItemName(itemText, itemText)
        }

        val selectedValue = convertedTextObservable.value
        if(selectedValue != null && selectedValue.isNotBlank()){
            addMissingIngredient()
        }
    }

    fun editMissingIngredient(){
        ingredientEditableObservable.postValue(true)
    }

    fun addMissingIngredient(){
        val ingredientText = convertedTextObservable.value
        val itemText = textObservable.value
        if(ingredientText != null && itemText != null){
            lifecycleScope.launch(Dispatchers.Default) {
                itemIngredientMapDatastore.insertTAndTMapping(itemText, ingredientText)
                ingredientsDatastore.insertSingleChineseIngredient(ingredientText)
                ingredientEditableObservable.postValue(false)
                convertedObservable.postValue(true)
            }
        } else {
            // show user some message
            Log.w(TAG, "ingredient field is missing while trying to add new ingredient")
        }
    }

    private fun setIngredient(ingredient: String){
        if(ingredient.isBlank()){
            return
        }
        val currentEntries = spinnerEntriesObservable.value
        if(currentEntries == null || !currentEntries.contains(ingredient)){
            val newList = mutableListOf<String>()
            newList.add(ingredient)
            if(currentEntries != null && currentEntries.isNotEmpty()){
                newList.addAll(currentEntries)
            }
            Log.i(TAG, "KL: new list size = ${newList.size}")
            spinnerEntriesObservable.postValue(newList)
            convertedTextObservable.postValue(ingredient)
        }

    }
}