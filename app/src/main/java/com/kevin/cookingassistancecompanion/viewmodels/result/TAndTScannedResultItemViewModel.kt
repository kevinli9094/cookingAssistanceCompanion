package com.kevin.cookingassistancecompanion.viewmodels.result

import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemIngredientMapDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.models.IngredientMatch
import com.kevin.cookingassistancecompanion.models.ItemConvertedResult

/**
 * ScannedResultItemViewModel that target T&T items
 */
class TAndTScannedResultItemViewModel constructor(
    text: String,
    private val ingredientsDatastore: RealmIngredientsDatastore,
    private val itemNamesDatastore: RealmItemNamesDatastore,
    private val itemIngredientMapDatastore: RealmItemIngredientMapDatastore,
    editable: Boolean = false
): ScannedResultItemViewModel(
    text,
    editable
) {

    init {
        init()
    }

    override fun insertSingleItem(itemText: String) {
        itemNamesDatastore.insertSingleTAndTItemName(itemText, itemText)
    }

    override fun insertNewIngredientMapping(itemText: String, ingredient: String) {
        itemIngredientMapDatastore.insertTAndTMapping(itemText, ingredient)
        ingredientsDatastore.insertSingleChineseIngredient(ingredient)
    }

    override fun convert(itemText: String): ItemConvertedResult {
        val map = itemNamesDatastore.getTAndTItemMap()
        val ingredientList = ingredientsDatastore.getChineseIngredients()
        val itemIngredientMap = itemIngredientMapDatastore.getTAndTMapping()
        return convertTANDTChinese(itemText, map, ingredientList, itemIngredientMap)
    }

    /**
     * Takes a correct english T&T item and convert it to Chinese ingredient item
     */
    private fun convertTANDTChinese(
        itemName: String,
        itemMap: Map<String, String>,
        chineseIngredients: List<String>,
        itemIngredientMap: Map<String, String>
    ): ItemConvertedResult {

        val directMapping = itemIngredientMap[itemName]
        if (directMapping != null){
            return ItemConvertedResult(itemName, listOf(IngredientMatch(directMapping, 1)))
        }
        // convert it to T&T Chinese item
        val chineseItem = itemMap[itemName]

        if (chineseItem == null) {
            return ItemConvertedResult(itemName, null)
        }

        // convert it to Chinese ingredient
        val mutableList = mutableListOf<IngredientMatch>()

        chineseIngredients.forEach { ingredientName ->
            if (chineseItem.contains(ingredientName)) {
                mutableList.add(IngredientMatch(ingredientName, ingredientName.length))
            }
        }

        val sortedMatches = mutableList.sortedByDescending { it.score }
        return ItemConvertedResult(itemName, sortedMatches)
    }
}