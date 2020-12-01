package com.kevin.cookingassistancecompanion.utility

import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.models.IngredientMatch
import com.kevin.cookingassistancecompanion.models.ItemConvertedResult

/**
 * Helper class to convert English Item to ingredient depends on the setting
 */
class ItemIngredientConverter(
    private val ingredientsDatastore: RealmIngredientsDatastore,
    private val itemNamesDatastore: RealmItemNamesDatastore
) {

    fun covert(item: String): ItemConvertedResult {
        return when (ScanningResult.resultType) {
            ScanningResult.ResultType.TANDT_CHINESE -> convertTANDTChineseItems(item)
            ScanningResult.ResultType.TANDT_ENGLISH -> convertTANDTEnglishItems(item)
        }
    }

    private fun convertTANDTChineseItems(item: String): ItemConvertedResult {
        val map = itemNamesDatastore.getTAndTItemMap()
        val ingredientList = ingredientsDatastore.getChineseIngredients()
        return convertTANDTChinese(item, map, ingredientList)
    }

    private fun convertTANDTEnglishItems(item: String): ItemConvertedResult {
        // TODO: implement this later
        return convertTANDTEnglish(item)
    }

    /**
     * Takes a correct english T&T item and convert it to Chinese ingredient item
     */
    private fun convertTANDTChinese(
        itemName: String,
        itemMap: Map<String, String>,
        chineseIngredients: List<String>
    ): ItemConvertedResult {
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

    /**
     * Takes a correct english T&T item and convert it to English ingredient item
     */
    private fun convertTANDTEnglish(itemName: String): ItemConvertedResult {
        // TODO: implement this
        return ItemConvertedResult(itemName, null)
    }
}