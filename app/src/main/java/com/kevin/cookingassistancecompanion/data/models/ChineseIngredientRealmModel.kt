package com.kevin.cookingassistancecompanion.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Realm model to store chinese ingredients
 */
open class ChineseIngredientRealmModel(
    @Required
    @PrimaryKey
    var ingredientName: String? = ""
) : RealmObject()