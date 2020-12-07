package com.kevin.cookingassistancecompanion.models.gson

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("_id")
    var id: String = ""

    @SerializedName("name")
    var name: String = ""
}