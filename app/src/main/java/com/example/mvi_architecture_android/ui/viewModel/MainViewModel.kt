package com.example.mvi_architecture_android.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi_architecture_android.data.repository.MainRepository
import com.example.mvi_architecture_android.ui.intent.MainIntent
import com.example.mvi_architecture_android.ui.viewState.MainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)

    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.FetchUser -> {
                        withContext(Dispatchers.Default) {
                            fetchUser()
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchUser() {
        _state.value = MainState.Loading
        _state.value = try {
            MainState.Users(repository.getUsers())
        } catch (ex: Exception) {
            MainState.Error(ex.message)
        }
    }

}