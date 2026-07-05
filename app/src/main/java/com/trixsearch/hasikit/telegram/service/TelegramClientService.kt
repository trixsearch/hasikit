package com.trixsearch.hasikit.telegram.service

import android.content.Context
import android.util.Log
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TelegramClientService"

/**
 * Stub implementation — compiles without TDLib.
 *
 * When TDLib AAR is added to app/libs/:
 *   1. Add imports: org.drinkless.tdlib.Client, org.drinkless.tdlib.TdApi
 *   2. Replace each method body with the TDLib call documented in the comment above it.
 *   3. Restore getClient() return type to Client?
 */
@Singleton
class TelegramClientService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: TelegramSourceConfig
) {
    // TDLib integration point — replace Any? with Client? when TDLib is present
    private var client: Any? = null

    fun initClient() {
        // TDLib: Client.setLogVerbosityLevel(0)
        // TDLib: client = Client.create({ update -> ... }, null, null)
        // TDLib: sendTdlibParameters()
        Log.d(TAG, "initClient — TDLib not yet integrated")
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    /**
     * TDLib wiring:
     *   client.send(TdApi.SetAuthenticationPhoneNumber(phone, null))
     *   → on AuthorizationStateWaitCode → return AuthResult.CodeSent(phoneCodeHash)
     */
    suspend fun sendCode(phoneNumber: String): AuthResult {
        Log.d(TAG, "sendCode stub phone=$phoneNumber")
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        if (cleaned.length < 7) return AuthResult.Failure("Invalid phone number format")
        return AuthResult.Failure("Telegram login not available yet. Use Demo mode.")
    }

    /**
     * TDLib wiring:
     *   client.send(TdApi.CheckAuthenticationCode(code))
     *   → on AuthorizationStateReady → client.send(TdApi.GetMe()) → AuthResult.Success(user)
     */
    suspend fun verifyCode(phoneNumber: String, phoneCodeHash: String, code: String): AuthResult {
        Log.d(TAG, "verifyCode stub phone=$phoneNumber")
        return AuthResult.Failure("Telegram login not available yet. Use Demo mode.")
    }

    /**
     * TDLib wiring: return TDLib database directory path.
     * TDLib auto-persists session there; path is stored as session token.
     */
    suspend fun exportSession(): String {
        return "stub_session_${System.currentTimeMillis()}"
    }

    /**
     * TDLib wiring:
     *   Re-init client with existing db dir → check AuthorizationStateReady
     *   → client.send(TdApi.GetMe()) → return TelegramUser
     */
    suspend fun importSession(sessionString: String): TelegramUser? {
        Log.d(TAG, "importSession stub — TDLib not yet integrated")
        return null
    }

    /**
     * TDLib wiring: client.send(TdApi.LogOut())
     */
    suspend fun logout() {
        Log.d(TAG, "logout stub — TDLib not yet integrated")
        client = null
    }

    // ── Media — wired by TelegramMediaRepositoryImpl when TDLib is present ────

    fun getClient(): Any? = client
}
