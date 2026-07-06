package com.trixsearch.hasikit.telegram.domain.repository

import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.AuthState
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import kotlinx.coroutines.flow.StateFlow

interface TelegramAuthRepository {

    val authState: StateFlow<AuthState>

    suspend fun sendCode(phoneNumber: String): AuthResult

    suspend fun verifyCode(phoneNumber: String, phoneCodeHash: String, code: String): AuthResult

    suspend fun getCurrentUser(): TelegramUser?

    suspend fun restoreSession()

    suspend fun logout()

    suspend fun forceDeleteSession()

    suspend fun loginAsDemo(user: TelegramUser)
}
