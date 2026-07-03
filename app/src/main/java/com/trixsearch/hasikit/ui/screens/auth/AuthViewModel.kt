package com.trixsearch.hasikit.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trixsearch.hasikit.BuildConfig
import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.AuthState
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

val DEMO_USER = TelegramUser(
    id = -1L,
    firstName = "Demo",
    lastName = "User",
    username = null,
    phoneNumber = "Demo Mode",
    profilePhotoUrl = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: TelegramAuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
    val isDemoMode: Boolean = BuildConfig.TELEGRAM_DEMO_MODE

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loginAsDemo() {
        viewModelScope.launch {
            Log.d(TAG, "loginAsDemo")
            authRepository.loginAsDemo(DEMO_USER)
        }
    }

    fun sendCode(phoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Log.d(TAG, "sendCode phone=$phoneNumber")
            when (val result = authRepository.sendCode(phoneNumber)) {
                is AuthResult.Failure -> {
                    _errorMessage.value = result.message
                    Log.e(TAG, "sendCode failed: ${result.message}")
                }
                else -> { /* authState updated by repository */ }
            }
            _isLoading.value = false
        }
    }

    fun verifyCode(phoneNumber: String, phoneCodeHash: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Log.d(TAG, "verifyCode phone=$phoneNumber")
            when (val result = authRepository.verifyCode(phoneNumber, phoneCodeHash, code)) {
                is AuthResult.Failure -> {
                    _errorMessage.value = result.message
                    Log.e(TAG, "verifyCode failed: ${result.message}")
                }
                else -> { /* authState updated by repository */ }
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            Log.d(TAG, "logout")
            authRepository.logout()
        }
    }
}
