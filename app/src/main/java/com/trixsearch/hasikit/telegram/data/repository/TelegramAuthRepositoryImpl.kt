package com.trixsearch.hasikit.telegram.data.repository

import android.content.Context
import android.util.Log
import com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager
import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.AuthState
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TelegramAuthRepo"

@Singleton
class TelegramAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clientService: TelegramClientService,
    private val sessionManager: TelegramSessionManager
) : TelegramAuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState

    // Background scope for profile photo loading — does not block auth flow
    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Load profile photo in background and update auth state with cached path
    private fun loadAndCacheProfilePhoto(user: TelegramUser) {
        repoScope.launch {
            val photoPath = runCatching { clientService.loadProfilePhoto() }.getOrNull()
            if (photoPath != null && photoPath != user.profilePhotoUrl) {
                _authState.value = AuthState.Authenticated(user.copy(profilePhotoUrl = photoPath))
                Log.d(TAG, "profilePhoto cached path=$photoPath")
            }
        }
    }

    override suspend fun sendCode(phoneNumber: String): AuthResult {
        Log.d(TAG, "sendCode phone=$phoneNumber")
        return try {
            val result = clientService.sendCode(phoneNumber)
            when (result) {
                is AuthResult.CodeSent -> {
                    _authState.value = AuthState.CodeSent(phoneNumber, result.phoneCodeHash)
                    result
                }
                is AuthResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                    result
                }
                else -> AuthResult.Failure("Unexpected result from sendCode")
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown error"
            Log.e(TAG, "sendCode exception: $msg", e)
            _authState.value = AuthState.Error(msg)
            AuthResult.Failure(msg)
        }
    }

    override suspend fun verifyCode(phoneNumber: String, phoneCodeHash: String, code: String): AuthResult {
        Log.d(TAG, "verifyCode phone=$phoneNumber")
        return try {
            val result = clientService.verifyCode(phoneNumber, phoneCodeHash, code)
            when (result) {
                is AuthResult.Success -> {
                    val sessionString = clientService.exportSession()
                    sessionManager.saveSession(result.user, sessionString)
                    _authState.value = AuthState.Authenticated(result.user)
                    // Load profile photo after successful login
                    loadAndCacheProfilePhoto(result.user)
                    result
                }
                is AuthResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                    result
                }
                else -> AuthResult.Failure("Unexpected result from verifyCode")
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown error"
            Log.e(TAG, "verifyCode exception: $msg", e)
            _authState.value = AuthState.Error(msg)
            AuthResult.Failure(msg)
        }
    }

    override suspend fun getCurrentUser(): TelegramUser? =
        (_authState.value as? AuthState.Authenticated)?.user

    override suspend fun restoreSession() {
        Log.d(TAG, "restoreSession")
        if (!sessionManager.hasValidSession()) {
            _authState.value = AuthState.Unauthenticated
            return
        }
        try {
            val sessionString = sessionManager.getSessionString()
            if (sessionString.isNullOrBlank()) {
                sessionManager.clearSession()
                _authState.value = AuthState.Unauthenticated
                return
            }
            val user = clientService.importSession(sessionString)
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                // Load profile photo in background after session restore
                loadAndCacheProfilePhoto(user)
                Log.d(TAG, "restoreSession success userId=${user.id}")
            } else {
                Log.w(TAG, "restoreSession — importSession returned null, clearing")
                sessionManager.clearSession()
                _authState.value = AuthState.Unauthenticated
            }
        } catch (e: Exception) {
            Log.e(TAG, "restoreSession exception: ${e.message}", e)
            sessionManager.clearSession()
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun logout() {
        Log.d(TAG, "logout")
        // Clear app state immediately so UI navigates away without waiting for TDLib
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
        try {
            clientService.logout()
        } catch (e: Exception) {
            Log.e(TAG, "logout TDLib exception (session already cleared): ${e.message}", e)
        }
    }

    override suspend fun forceDeleteSession() {
        Log.d(TAG, "forceDeleteSession")
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
        try {
            // Delete TDLib database directory entirely
            val tdlibDir = File(context.filesDir, "tdlib")
            if (tdlibDir.exists()) {
                tdlibDir.deleteRecursively()
                Log.d(TAG, "forceDeleteSession — deleted TDLib dir: ${tdlibDir.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "forceDeleteSession exception: ${e.message}", e)
        }
    }

    // Kept for interface compatibility — no-op since demo mode is removed
    override suspend fun loginAsDemo(user: TelegramUser) {
        Log.w(TAG, "loginAsDemo called but demo mode is disabled")
    }
}
