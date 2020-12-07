package com.kevin.cookingassistancecompanion.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Realm model for store mapping from t&t english item name to t&t chinese item name
 */
open class TAndTItemNameRealmModel(
    @Required
    @PrimaryKey
    var name: String = "",
    @Required
    var chineseName: String = ""
) : RealmObject()