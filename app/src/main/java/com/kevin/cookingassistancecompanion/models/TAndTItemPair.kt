package com.kevin.cookingassistancecompanion.models

import com.kevin.cookingassistancecompanion.data.models.TAndTItemNameRealmModel

data class TAndTItemPair(val name: String, val chineseName: String){
    constructor(realmModel: TAndTItemNameRealmModel):this(realmModel.name, realmModel.chineseName)
}