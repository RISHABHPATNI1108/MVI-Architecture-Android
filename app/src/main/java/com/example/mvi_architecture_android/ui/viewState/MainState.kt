package com.example.mvi_architecture_android.ui.viewState

import com.example.mvi_architecture_android.data.model.User

sealed class MainState: BaseViewState {

    object Idle : MainState()
    object Loading : MainState()
    data class Users(val user: List<User>) : MainState()
    data class Error(val error: String?) : MainState()

}