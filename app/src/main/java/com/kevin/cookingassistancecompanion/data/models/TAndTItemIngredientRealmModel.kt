package com.kevin.cookingassistancecompanion.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Realm model to store mapping from t&t english item name to chinese ingredient
 */
open class TAndTItemIngredientRealmModel(
    @Required
    @PrimaryKey
    var name: String = "",
    @Required
    var ingredient: String = ""
) : RealmObject()