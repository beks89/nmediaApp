package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: AppAuth, private val apiService: PostsApiService) : ViewModel() {
    val data: LiveData<AuthState> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L

    val state = SingleLiveEvent<Boolean>()

    fun authenticate(login: String, pass: String) = viewModelScope.launch {
        try {
            val authState =
                apiService.updateUser(login, pass).body() ?: throw ApiError(-1, "No user")
            auth.setAuth(authState.id, authState.token ?: "")
            state.value = true
        } catch (e: Exception) {
            state.value = false
        }
    }
}