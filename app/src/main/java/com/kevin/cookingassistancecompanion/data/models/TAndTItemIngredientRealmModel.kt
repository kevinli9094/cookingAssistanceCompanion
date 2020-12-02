package com.kevin.cookingassistancecompanion.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class TAndTItemIngredientRealmModel(
    @Required
    @PrimaryKey
    var name: String = "",
    @Required
    var ingredient: String = ""
) : RealmObject()