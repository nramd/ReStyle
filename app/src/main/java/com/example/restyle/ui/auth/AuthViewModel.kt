package com.example.restyle.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restyle.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: FirebaseUser? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authRepository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val user = authRepository.getCurrentUser()
        _isLoggedIn.value = user != null
        _authState.value = AuthState(user = user)
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            authRepository.register(email, password, displayName).fold(
                onSuccess = { user ->
                    Log.d(TAG, "Register success: ${user.uid}")
                    _authState.value = AuthState(
                        isLoading = false,
                        isSuccess = true,
                        user = user
                    )
                    _isLoggedIn.value = true
                },
                onFailure = { exception ->
                    Log.e(TAG, "Register failed", exception)
                    _authState.value = AuthState(
                        isLoading = false,
                        error = exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            authRepository.login(email, password).fold(
                onSuccess = { user ->
                    Log.d(TAG, "Login success: ${user.uid}")
                    _authState.value = AuthState(
                        isLoading = false,
                        isSuccess = true,
                        user = user
                    )
                    _isLoggedIn.value = true
                },
                onFailure = { exception ->
                    Log.e(TAG, "Login failed", exception)
                    _authState.value = AuthState(
                        isLoading = false,
                        error = exception.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _isLoggedIn.value = false
        _authState.value = AuthState()
        Log.d(TAG, "User logged out")
    }

    fun resetAuthState() {
        _authState.value = AuthState(user = authRepository.getCurrentUser())
    }

    fun getCurrentUserId(): String {
        return authRepository.getCurrentUserId()
    }
}