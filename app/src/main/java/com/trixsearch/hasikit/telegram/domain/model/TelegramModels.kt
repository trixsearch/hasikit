package com.trixsearch.hasikit.telegram.domain.model

data class TelegramUser(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String?,
    val phoneNumber: String,
    val profilePhotoUrl: String?
) {
    val displayName: String get() = buildString {
        append(firstName)
        if (lastName.isNotBlank()) append(" $lastName")
    }
}

data class TelegramChannel(
    val id: Long,
    val title: String,
    val username: String?,
    val description: String?,
    val memberCount: Int,
    val photoUrl: String?
)

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class CodeSent(val phone: String, val phoneCodeHash: String) : AuthState()
    data class Authenticated(val user: TelegramUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class AuthResult {
    data class CodeSent(val phoneCodeHash: String) : AuthResult()
    data class Success(val user: TelegramUser) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}
