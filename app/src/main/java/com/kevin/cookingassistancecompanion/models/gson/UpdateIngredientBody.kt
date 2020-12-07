package com.kevin.cookingassistancecompanion.models.gson

import com.google.gson.annotations.SerializedName

/**
 * GSON model for updateing user's ingredient
 */
data class UpdateIngredientBody(
    @SerializedName("userId")
    var userId: String = "",
    @SerializedName("ingredients")
    var ingredients: List<String> = emptyList()
)