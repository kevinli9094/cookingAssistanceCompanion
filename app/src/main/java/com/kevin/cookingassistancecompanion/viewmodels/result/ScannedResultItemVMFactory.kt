package com.kevin.cookingassistancecompanion.viewmodels.result

import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.utility.MessageManager
import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemIngredientMapDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore

/**
 * Factory class to create ResultItemViewModel class base of setting
 */
class ScannedResultItemVMFactory(
    private val messageManager: MessageManager,
    private val ingredientsDatastore: RealmIngredientsDatastore,
    private val itemNamesDatastore: RealmItemNamesDatastore,
    private val itemIngredientMapDatastore: RealmItemIngredientMapDatastore
) {

    fun createExistingVM(text: String): ScannedResultItemViewModel {
        return when (ScanningResult.resultType) {
            ScanningResult.ResultType.TANDT_CHINESE -> TAndTScannedResultItemViewModel(
                text,
                messageManager,
                ingredientsDatastore,
                itemNamesDatastore,
                itemIngredientMapDatastore,
                editable = false
            )
            else -> {
                throw IllegalStateException("not implemented")
            }
        }
    }

    fun createNewVM(): ScannedResultItemViewModel {
        return when (ScanningResult.resultType) {
            ScanningResult.ResultType.TANDT_CHINESE -> TAndTScannedResultItemViewModel(
                "",
                messageManager,
                ingredientsDatastore,
                itemNamesDatastore,
                itemIngredientMapDatastore,
                editable = true
            )
            else -> {
                throw IllegalStateException("not implemented")
            }
        }
    }
}