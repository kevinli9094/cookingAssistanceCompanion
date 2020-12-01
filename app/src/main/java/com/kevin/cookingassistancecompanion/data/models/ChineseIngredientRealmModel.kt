package com.kevin.cookingassistancecompanion.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ChineseIngredientRealmModel(
    @Required
    @PrimaryKey
    var ingredientName: String? = ""
) : RealmObject()