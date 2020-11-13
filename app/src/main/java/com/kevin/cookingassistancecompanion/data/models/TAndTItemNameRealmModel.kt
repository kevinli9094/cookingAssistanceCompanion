package com.kevin.cookingassistancecompanion.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class TAndTItemNameRealmModel(
    @Required
    @PrimaryKey
    var name: String = ""
): RealmObject()