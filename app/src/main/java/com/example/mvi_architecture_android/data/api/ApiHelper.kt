package com.example.mvi_architecture_android.data.api

import com.example.mvi_architecture_android.data.model.User

interface ApiHelper {

    suspend fun getUsers(): List<User>

}