package com.kevin.cookingassistancecompanion.data

import com.kevin.cookingassistancecompanion.data.models.TAndTItemIngredientRealmModel
import io.realm.kotlin.where

class RealmItemIngredientMapDatastore {
    private val realmConnection = RealmConnection()

    fun insertTAndTMapping(name: String, ingredient: String) {
        realmConnection.execute {
            it.copyToRealmOrUpdate(TAndTItemIngredientRealmModel(name, ingredient))
        }
    }

    fun getTAndTMapping(): Map<String, String> {
        return realmConnection.fetch({ realm ->
            realm.where<TAndTItemIngredientRealmModel>()
                .findAll()
                .associate {
                    it.name to it.ingredient
                }
        }, defaultValue = emptyMap())
    }
}