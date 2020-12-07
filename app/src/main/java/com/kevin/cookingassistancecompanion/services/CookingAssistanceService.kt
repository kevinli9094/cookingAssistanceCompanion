package com.kevin.cookingassistancecompanion.services

import com.kevin.cookingassistancecompanion.models.gson.UpdateIngredientBody
import com.kevin.cookingassistancecompanion.models.gson.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface CookingAssistanceService {
    @GET("/users")
    fun getUsers(): Call<List<User>>

    @PUT("/users/ingredients/add")
    fun updateIngredient(
        @Body body: UpdateIngredientBody
    ): Call<Void>
}