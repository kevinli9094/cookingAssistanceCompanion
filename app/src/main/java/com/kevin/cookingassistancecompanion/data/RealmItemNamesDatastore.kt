package com.kevin.cookingassistancecompanion.data

import com.kevin.cookingassistancecompanion.data.models.TAndTItemNameRealmModel
import com.kevin.cookingassistancecompanion.models.TAndTItemPair
import io.realm.kotlin.where

/**
 * Datastore to store item name
 */
class RealmItemNamesDatastore {

    private val realmConnection = RealmConnection()

    fun getTAndTItemPairList(): List<TAndTItemPair> {
        return realmConnection.fetch({ realm ->
            realm.where<TAndTItemNameRealmModel>()
                .findAll()
                .map {
                    TAndTItemPair(it)
                }
        }, defaultValue = emptyList())
    }

    fun getTAndTItemMap(): Map<String, String> {
        return realmConnection.fetch({ realm ->
            val mutableMap = mutableMapOf<String, String>()
            realm.where<TAndTItemNameRealmModel>()
                .findAll()
                .forEach {
                    mutableMap[it.name] = it.chineseName
                }
            return@fetch mutableMap
        }, defaultValue = emptyMap())
    }

    fun insertTAndTItemNames(map: Map<String, String>) {
        realmConnection.execute {
            val models = map.map { item ->
                TAndTItemNameRealmModel(name = item.key, chineseName = item.value)
            }

            it.copyToRealmOrUpdate(models)
        }
    }

    fun insertSingleTAndTItemName(correctTerm: String, mapping: String){
        realmConnection.execute {
            it.copyToRealmOrUpdate(TAndTItemNameRealmModel(name = correctTerm, chineseName = mapping))
        }
    }
}