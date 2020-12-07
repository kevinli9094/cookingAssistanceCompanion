package com.kevin.cookingassistancecompanion.services

import com.kevin.cookingassistancecompanion.models.User
import retrofit2.Call
import retrofit2.http.GET

interface CookingAssistanceService {
    @GET("users")
    fun getUsers(): Call<List<User>>
}