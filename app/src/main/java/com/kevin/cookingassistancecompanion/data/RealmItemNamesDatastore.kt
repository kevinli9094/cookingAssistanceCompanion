package com.kevin.cookingassistancecompanion.data

import com.kevin.cookingassistancecompanion.data.models.TAndTItemNameRealmModel
import io.realm.kotlin.where

class RealmItemNamesDatastore {

    private val realmConnection = RealmConnection()

    fun getTAndTItemNames(): List<String> {
        return realmConnection.fetch({ realm ->
            realm.where<TAndTItemNameRealmModel>()
                .findAll()
                .map {
                    it.name
                }
        }, defaultValue = emptyList())

    }

    fun insertTAndTItemNames(items: List<String>) {
        realmConnection.execute {
            val models = items.map { itemName ->
                TAndTItemNameRealmModel(name = itemName)
            }

            it.copyToRealmOrUpdate(models)
        }
    }
}