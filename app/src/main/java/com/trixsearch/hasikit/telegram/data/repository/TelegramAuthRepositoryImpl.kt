package com.trixsearch.hasikit.telegram.data.repository

import android.util.Log
import com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager
import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.AuthState
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TelegramAuthRepo"

@Singleton
class TelegramAuthRepositoryImpl @Inject constructor(
    private val clientService: TelegramClientService,
    private val sessionManager: TelegramSessionManager
) : TelegramAuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState

    override suspend fun sendCode(phoneNumber: String): AuthResult {
        Log.d(TAG, "sendCode phone=$phoneNumber")
        return try {
            val result = clientService.sendCode(phoneNumber)
            when (result) {
                is AuthResult.CodeSent -> {
                    _authState.value = AuthState.CodeSent(phoneNumber, result.phoneCodeHash)
                    Log.d(TAG, "sendCode success — hash=${result.phoneCodeHash}")
                    result
                }
                is AuthResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                    Log.e(TAG, "sendCode failed: ${result.message}")
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

    override suspend fun verifyCode(
        phoneNumber: String,
        phoneCodeHash: String,
        code: String
    ): AuthResult {
        Log.d(TAG, "verifyCode phone=$phoneNumber code=$code")
        return try {
            val result = clientService.verifyCode(phoneNumber, phoneCodeHash, code)
            when (result) {
                is AuthResult.Success -> {
                    val sessionString = clientService.exportSession()
                    sessionManager.saveSession(result.user, sessionString)
                    _authState.value = AuthState.Authenticated(result.user)
                    Log.d(TAG, "verifyCode success userId=${result.user.id}")
                    result
                }
                is AuthResult.Failure -> {
                    _authState.value = AuthState.Error(result.message)
                    Log.e(TAG, "verifyCode failed: ${result.message}")
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

    override suspend fun getCurrentUser(): TelegramUser? {
        return when (val state = _authState.value) {
            is AuthState.Authenticated -> state.user
            else -> null
        }
    }

    override suspend fun restoreSession() {
        Log.d(TAG, "restoreSession")
        if (!sessionManager.hasValidSession()) {
            Log.d(TAG, "restoreSession — no saved session")
            _authState.value = AuthState.Unauthenticated
            return
        }
        try {
            val sessionString = sessionManager.getSessionString()
            if (sessionString.isNullOrBlank()) {
                Log.w(TAG, "restoreSession — session string missing")
                _authState.value = AuthState.Unauthenticated
                return
            }
            // Demo session — restore directly from saved user without TDLib
            if (sessionString == "demo_session") {
                val user = sessionManager.savedUser.first()
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                    Log.d(TAG, "restoreSession demo userId=${user.id}")
                } else {
                    sessionManager.clearSession()
                    _authState.value = AuthState.Unauthenticated
                }
                return
            }
            val user = clientService.importSession(sessionString)
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
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
        try {
            clientService.logout()
        } catch (e: Exception) {
            Log.e(TAG, "logout exception: ${e.message}", e)
        } finally {
            sessionManager.clearSession()
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun loginAsDemo(user: TelegramUser) {
        Log.d(TAG, "loginAsDemo userId=${user.id}")
        sessionManager.saveSession(user, "demo_session")
        _authState.value = AuthState.Authenticated(user)
    }
}
