package com.trixsearch.hasikit.telegram.service

import android.content.Context
import android.util.Log
import com.trixsearch.hasikit.BuildConfig
import com.trixsearch.hasikit.BuildConfig.TELEGRAM_API_HASH
import com.trixsearch.hasikit.BuildConfig.TELEGRAM_API_ID
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig
import com.trixsearch.hasikit.telegram.domain.model.AuthResult
import com.trixsearch.hasikit.telegram.domain.model.TelegramUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "TelegramClientService"

@Singleton
class TelegramClientService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: TelegramSourceConfig
) {
    @Volatile private var client: Client? = null

    @Volatile private var currentAuthState: TdApi.AuthorizationState? = null

    // Single pending slot — only one coroutine waits at a time
    @Volatile private var pendingAuthHandler: ((TdApi.AuthorizationState) -> Unit)? = null

    private val tdlibDbDir: String
        get() = File(context.filesDir, "tdlib").absolutePath

    // ── Init ──────────────────────────────────────────────────────────────────

    fun initClient() {
        try {
            Client.execute(TdApi.SetLogVerbosityLevel(1))
        } catch (e: Client.ExecutionException) {
            Log.w(TAG, "SetLogVerbosityLevel failed: ${e.message}")
        }
        createClient()
    }

    private fun createClient() {
        Log.d(TAG, "createClient — previous=${client != null}")
        client = Client.create(
            { update -> handleUpdate(update) },
            { e -> Log.e(TAG, "TDLib update exception", e) },
            { e -> Log.e(TAG, "TDLib default exception", e) }
        )
    }

    private fun handleUpdate(update: TdApi.Object) {
        when (update) {
            is TdApi.UpdateAuthorizationState -> {
                val state = update.authorizationState
                currentAuthState = state
                Log.d(TAG, "AUTH_STATE=${state::class.java.simpleName}")

                when (state) {
                    is TdApi.AuthorizationStateWaitTdlibParameters -> sendTdlibParameters()
                    is TdApi.AuthorizationStateWaitPhoneNumber     -> Log.i(TAG, "AUTH_WAIT_PHONE")
                    is TdApi.AuthorizationStateWaitCode            -> Log.i(TAG, "AUTH_WAIT_CODE")
                    is TdApi.AuthorizationStateWaitPassword        -> Log.i(TAG, "AUTH_WAIT_PASSWORD")
                    is TdApi.AuthorizationStateReady               -> Log.i(TAG, "AUTH_READY")
                    is TdApi.AuthorizationStateLoggingOut          -> Log.i(TAG, "AUTH_LOGGING_OUT")
                    is TdApi.AuthorizationStateClosed -> {
                        Log.i(TAG, "AUTH_CLOSED")
                        // Deliver CLOSED to any pending handler BEFORE creating a new client.
                        // This lets logout() resolve cleanly without racing against the new client.
                        pendingAuthHandler?.invoke(state)
                        pendingAuthHandler = null
                        createClient()
                        return // handler already invoked above — skip the invoke at the bottom
                    }
                    else -> Unit
                }

                pendingAuthHandler?.invoke(state)
            }
            is TdApi.UpdateOption -> {
                if (update.name == "version" && update.value is TdApi.OptionValueString) {
                    Log.i(TAG, "TDLIB_VERSION=${(update.value as TdApi.OptionValueString).value}")
                }
            }
        }
    }

    private fun sendTdlibParameters() {
        val dbDir = tdlibDbDir
        val filesDir = File(dbDir, "files").absolutePath
        File(dbDir).mkdirs()
        File(filesDir).mkdirs()

        Log.i(TAG, "SetTdlibParameters — " +
            "API_ID=$TELEGRAM_API_ID " +
            "API_HASH_PRESENT=${TELEGRAM_API_HASH.isNotBlank()} " +
            "databaseDirectory=$dbDir " +
            "filesDirectory=$filesDir " +
            "useMessageDatabase=true " +
            "useSecretChats=false"
        )

        client?.send(
            TdApi.SetTdlibParameters().apply {
                databaseDirectory = dbDir
                this.filesDirectory = filesDir
                useMessageDatabase = true
                useSecretChats = false
                apiId = TELEGRAM_API_ID
                apiHash = TELEGRAM_API_HASH
                systemLanguageCode = "en"
                deviceModel = android.os.Build.MODEL
                applicationVersion = BuildConfig.VERSION_NAME
            }
        ) { result ->
            if (result is TdApi.Error) {
                Log.e(TAG, "SetTdlibParameters error ${result.code}: ${result.message}")
            } else {
                Log.d(TAG, "SetTdlibParameters OK")
            }
        }
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun sendCode(phoneNumber: String): AuthResult {
        Log.d(TAG, "sendCode phone=$phoneNumber currentState=${currentAuthState?.javaClass?.simpleName}")
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        if (cleaned.length < 7) return AuthResult.Failure("Invalid phone number format")

        if (currentAuthState !is TdApi.AuthorizationStateWaitPhoneNumber) {
            Log.d(TAG, "sendCode — waiting for WaitPhoneNumber")
            val reached = waitForState { it is TdApi.AuthorizationStateWaitPhoneNumber }
            if (!reached) return AuthResult.Failure("TDLib did not reach WaitPhoneNumber state")
        }

        return suspendCancellableCoroutine { cont ->
            pendingAuthHandler = handler@{ state ->
                when (state) {
                    is TdApi.AuthorizationStateWaitCode -> {
                        pendingAuthHandler = null
                        Log.d(TAG, "sendCode — WaitCode received")
                        cont.resume(AuthResult.CodeSent(phoneCodeHash = ""))
                    }
                    is TdApi.AuthorizationStateWaitPassword -> {
                        pendingAuthHandler = null
                        cont.resume(AuthResult.Failure("2FA password required — not supported yet"))
                    }
                    is TdApi.AuthorizationStateClosed -> {
                        // handler already cleared in handleUpdate before createClient()
                        if (cont.isActive) cont.resume(AuthResult.Failure("TDLib client closed unexpectedly"))
                    }
                    else -> return@handler
                }
            }
            Log.d(TAG, "sendCode — sending SetAuthenticationPhoneNumber")
            client?.send(TdApi.SetAuthenticationPhoneNumber(cleaned, null)) { result ->
                if (result is TdApi.Error) {
                    pendingAuthHandler = null
                    Log.e(TAG, "SetAuthenticationPhoneNumber error ${result.code}: ${result.message}")
                    if (cont.isActive) cont.resume(AuthResult.Failure(result.message))
                }
            }
            cont.invokeOnCancellation { pendingAuthHandler = null }
        }
    }

    suspend fun verifyCode(phoneNumber: String, phoneCodeHash: String, code: String): AuthResult {
        Log.d(TAG, "verifyCode phone=$phoneNumber")
        return suspendCancellableCoroutine { cont ->
            pendingAuthHandler = handler@{ state ->
                when (state) {
                    is TdApi.AuthorizationStateReady -> {
                        pendingAuthHandler = null
                        client?.send(TdApi.GetMe()) { meResult ->
                            if (meResult is TdApi.User) {
                                cont.resume(AuthResult.Success(meResult.toTelegramUser()))
                            } else {
                                cont.resume(AuthResult.Failure("Login succeeded but GetMe failed"))
                            }
                        }
                    }
                    is TdApi.AuthorizationStateWaitPassword -> {
                        pendingAuthHandler = null
                        cont.resume(AuthResult.Failure("2FA password required — not supported yet"))
                    }
                    is TdApi.AuthorizationStateClosed -> {
                        if (cont.isActive) cont.resume(AuthResult.Failure("TDLib client closed unexpectedly"))
                    }
                    else -> return@handler
                }
            }
            client?.send(TdApi.CheckAuthenticationCode(code)) { result ->
                if (result is TdApi.Error) {
                    pendingAuthHandler = null
                    Log.e(TAG, "CheckAuthenticationCode error ${result.code}: ${result.message}")
                    if (cont.isActive) cont.resume(AuthResult.Failure(result.message))
                }
            }
            cont.invokeOnCancellation { pendingAuthHandler = null }
        }
    }

    suspend fun exportSession(): String = tdlibDbDir

    /**
     * Called during session restore. TDLib may still be cycling through
     * WaitTdlibParameters when this is called — wait for a stable state first.
     */
    suspend fun importSession(sessionString: String): TelegramUser? {
        Log.d(TAG, "importSession dbDir=$sessionString")
        if (!File(sessionString).exists()) {
            Log.w(TAG, "importSession — db dir missing")
            return null
        }
        // Wait until TDLib is no longer in the parameters/initialising states
        val stableState = waitForStableAuthState()
        Log.d(TAG, "importSession — stable state=${stableState?.javaClass?.simpleName}")

        return when (stableState) {
            is TdApi.AuthorizationStateReady -> {
                suspendCancellableCoroutine { cont ->
                    client?.send(TdApi.GetMe()) { meResult ->
                        cont.resume(if (meResult is TdApi.User) meResult.toTelegramUser() else null)
                    }
                    cont.invokeOnCancellation {}
                }
            }
            else -> null
        }
    }

    suspend fun logout() {
        Log.d(TAG, "logout — currentState=${currentAuthState?.javaClass?.simpleName}")
        suspendCancellableCoroutine { cont ->
            pendingAuthHandler = handler@{ state ->
                // Resolve as soon as TDLib confirms the session is closed.
                // AUTH_CLOSED handler in handleUpdate clears pendingAuthHandler and
                // calls createClient() — so this lambda fires, then the slot is cleared.
                if (state is TdApi.AuthorizationStateClosed) {
                    if (cont.isActive) cont.resume(Unit)
                }
                // Also resolve if TDLib skips straight to WaitPhoneNumber (rare)
                if (state is TdApi.AuthorizationStateWaitPhoneNumber) {
                    pendingAuthHandler = null
                    if (cont.isActive) cont.resume(Unit)
                }
            }
            client?.send(TdApi.LogOut()) { result ->
                if (result is TdApi.Error) {
                    Log.e(TAG, "LogOut error ${result.code}: ${result.message}")
                    pendingAuthHandler = null
                    if (cont.isActive) cont.resume(Unit)
                }
            }
            cont.invokeOnCancellation { pendingAuthHandler = null }
        }
        Log.d(TAG, "logout — complete, new state=${currentAuthState?.javaClass?.simpleName}")
    }

    // ── Media access ──────────────────────────────────────────────────────────

    /**
     * Exposes the live TDLib client for use by media repositories.
     * Only valid when [currentAuthState] is [TdApi.AuthorizationStateReady].
     */
    fun getClient(): Client? = client

    fun isReady(): Boolean = currentAuthState is TdApi.AuthorizationStateReady

    /**
     * Send a TDLib request and deliver the result to [handler].
     * Logs an error if the client is null or not ready.
     */
    fun send(query: TdApi.Function<*>, handler: Client.ResultHandler) {
        val c = client
        if (c == null) {
            Log.e(TAG, "send() called but client is null — query=${query::class.java.simpleName}")
            return
        }
        @Suppress("UNCHECKED_CAST")
        c.send(query as TdApi.Function<TdApi.Object>, handler)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private suspend fun waitForState(predicate: (TdApi.AuthorizationState) -> Boolean): Boolean {
        currentAuthState?.let { if (predicate(it)) return true }
        return suspendCancellableCoroutine { cont ->
            pendingAuthHandler = handler@{ state ->
                when {
                    predicate(state) -> {
                        pendingAuthHandler = null
                        if (cont.isActive) cont.resume(true)
                    }
                    // AUTH_CLOSED triggers createClient() → new cycle → keep waiting
                    state is TdApi.AuthorizationStateClosed -> return@handler
                    else -> return@handler
                }
            }
            cont.invokeOnCancellation { pendingAuthHandler = null }
        }
    }

    /**
     * Waits until TDLib is past the initialisation phase.
     * Returns the first state that is not WaitTdlibParameters.
     */
    private suspend fun waitForStableAuthState(): TdApi.AuthorizationState? {
        val current = currentAuthState
        if (current != null && current !is TdApi.AuthorizationStateWaitTdlibParameters) {
            return current
        }
        return suspendCancellableCoroutine { cont ->
            pendingAuthHandler = handler@{ state ->
                if (state !is TdApi.AuthorizationStateWaitTdlibParameters) {
                    pendingAuthHandler = null
                    if (cont.isActive) cont.resume(state)
                }
            }
            cont.invokeOnCancellation { pendingAuthHandler = null }
        }
    }
}

// ── Extension ─────────────────────────────────────────────────────────────────

private fun TdApi.User.toTelegramUser() = TelegramUser(
    id = id.toLong(),
    firstName = firstName,
    lastName = lastName,
    username = usernames?.activeUsernames?.firstOrNull(),
    phoneNumber = phoneNumber,
    profilePhotoUrl = null
)
