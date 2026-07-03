package com.trixsearch.hasikit.telegram.service

import android.content.Context
import android.util.Log
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "TelegramClientService"
private const val TIMEOUT_MS = 30_000L

@Singleton
class TelegramClientService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: TelegramSourceConfig
) {
    @Volatile
    private var client: Client? = null

    // ── Client lifecycle ──────────────────────────────────────────────────────

    fun initClient() {
        if (client != null) return
        Client.setLogVerbosityLevel(0)
        client = Client.create(
            { update -> Log.v(TAG, "update: ${update.javaClass.simpleName}") },
            null,
            null
        )
        Log.d(TAG, "TDLib client created")
        sendTdlibParameters()
    }

    private fun sendTdlibParameters() {
        val dbDir = File(context.filesDir, "tdlib").also { it.mkdirs() }
        val filesDir = File(context.filesDir, "tdlib_files").also { it.mkdirs() }
        send(
            TdApi.SetTdlibParameters().apply {
                databaseDirectory = dbDir.absolutePath
                filesDirectory = filesDir.absolutePath
                useMessageDatabase = true
                useSecretChats = false
                apiId = config.apiId
                apiHash = config.apiHash
                systemLanguageCode = "en"
                deviceModel = android.os.Build.MODEL
                applicationVersion = "1.0"
            }
        )
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun sendCode(phoneNumber: String): AuthResult {
        Log.d(TAG, "sendCode phone=$phoneNumber")
        initClient()
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        if (cleaned.length < 7) return AuthResult.Failure("Invalid phone number format")

        return withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client!!.send(TdApi.SetAuthenticationPhoneNumber(cleaned, null)) { obj ->
                    when (obj) {
                        is TdApi.Ok -> {
                            // TDLib moves to AuthorizationStateWaitCode; hash is embedded in state
                            // We query the state to get the phoneCodeHash
                            client!!.send(TdApi.GetAuthorizationState()) { state ->
                                when (state) {
                                    is TdApi.AuthorizationStateWaitCode -> {
                                        val hash = state.codeInfo?.phoneCodeHash ?: ""
                                        Log.d(TAG, "sendCode success hash=$hash")
                                        cont.resume(AuthResult.CodeSent(hash))
                                    }
                                    is TdApi.Error -> cont.resume(AuthResult.Failure(state.message))
                                    else -> cont.resume(AuthResult.CodeSent(""))
                                }
                            }
                        }
                        is TdApi.Error -> {
                            Log.e(TAG, "sendCode error: ${obj.message}")
                            cont.resume(AuthResult.Failure(obj.message))
                        }
                        else -> cont.resume(AuthResult.Failure("Unexpected response"))
                    }
                }
            }
        }
    }

    suspend fun verifyCode(phoneNumber: String, phoneCodeHash: String, code: String): AuthResult {
        Log.d(TAG, "verifyCode phone=$phoneNumber")
        return withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client!!.send(TdApi.CheckAuthenticationCode(code)) { obj ->
                    when (obj) {
                        is TdApi.Ok -> {
                            client!!.send(TdApi.GetMe()) { me ->
                                when (me) {
                                    is TdApi.User -> {
                                        val user = me.toTelegramUser()
                                        Log.d(TAG, "verifyCode success userId=${user.id}")
                                        cont.resume(AuthResult.Success(user))
                                    }
                                    is TdApi.Error -> cont.resume(AuthResult.Failure(me.message))
                                    else -> cont.resume(AuthResult.Failure("Failed to fetch user"))
                                }
                            }
                        }
                        is TdApi.Error -> {
                            Log.e(TAG, "verifyCode error: ${obj.message}")
                            cont.resume(AuthResult.Failure(obj.message))
                        }
                        else -> cont.resume(AuthResult.Failure("Unexpected response"))
                    }
                }
            }
        }
    }

    suspend fun exportSession(): String {
        // TDLib persists session in the database directory automatically.
        // We store the db path as the session token so we can re-init on restore.
        val dbDir = File(context.filesDir, "tdlib")
        return dbDir.absolutePath
    }

    suspend fun importSession(sessionString: String): TelegramUser? {
        Log.d(TAG, "importSession path=$sessionString")
        val dbDir = File(sessionString)
        if (!dbDir.exists()) {
            Log.w(TAG, "importSession — session directory missing")
            return null
        }
        initClient()
        return withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client!!.send(TdApi.GetAuthorizationState()) { state ->
                    when (state) {
                        is TdApi.AuthorizationStateReady -> {
                            client!!.send(TdApi.GetMe()) { me ->
                                when (me) {
                                    is TdApi.User -> {
                                        Log.d(TAG, "importSession success userId=${me.id}")
                                        cont.resume(me.toTelegramUser())
                                    }
                                    else -> cont.resume(null)
                                }
                            }
                        }
                        else -> {
                            Log.w(TAG, "importSession — auth state not ready: ${state.javaClass.simpleName}")
                            cont.resume(null)
                        }
                    }
                }
            }
        }
    }

    suspend fun logout() {
        Log.d(TAG, "logout")
        withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                client!!.send(TdApi.LogOut()) { cont.resume(Unit) }
            }
        }
        client?.close()
        client = null
    }

    // ── Media ─────────────────────────────────────────────────────────────────

    fun getClient(): Client? = client

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun send(request: TdApi.Function<*>) {
        client?.send(request) { obj ->
            if (obj is TdApi.Error) Log.e(TAG, "send error for ${request.javaClass.simpleName}: ${obj.message}")
        }
    }
}

private fun TdApi.User.toTelegramUser() = TelegramUser(
    id = id,
    firstName = firstName,
    lastName = lastName,
    username = usernames?.activeUsernames?.firstOrNull(),
    phoneNumber = phoneNumber,
    profilePhotoUrl = null
)
