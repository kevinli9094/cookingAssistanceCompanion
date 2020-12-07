package com.kevin.cookingassistancecompanion.data

import com.kevin.cookingassistancecompanion.data.models.ChineseIngredientRealmModel
import io.realm.kotlin.where

/**
 * Datastore to get ingredients
 */
class RealmIngredientsDatastore {
    private val realmConnection = RealmConnection()

    fun getChineseIngredients(): List<String> {
        return realmConnection.fetch({ realm ->
            realm.where<ChineseIngredientRealmModel>()
                .findAll()
                .mapNotNull {
                    it.ingredientName
                }
        }, defaultValue = emptyList())
    }

    fun insertChineseIngredients(ingredients: List<String>) {
        realmConnection.execute { realm ->
            val models = ingredients.map {
                ChineseIngredientRealmModel(it)
            }

            realm.copyToRealmOrUpdate(models)
        }
    }

    fun insertSingleChineseIngredient(ingredient: String){
        realmConnection.execute { realm ->
            realm.copyToRealmOrUpdate(ChineseIngredientRealmModel(ingredient))
        }
    }
}