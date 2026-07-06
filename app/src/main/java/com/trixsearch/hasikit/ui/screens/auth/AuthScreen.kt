package com.trixsearch.hasikit.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trixsearch.hasikit.telegram.domain.model.AuthState

private val BgGradient = Brush.verticalGradient(listOf(Color(0xFF0A0A0F), Color(0xFF12122A)))
private val PrimaryBlue = Color(0xFF2AABEE)

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onAuthenticated()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGradient)
            .imePadding()
    ) {
        AnimatedContent(
            targetState = authState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "auth_transition"
        ) { state ->
            when (state) {
                is AuthState.CodeSent -> OtpScreen(
                    phone = state.phone,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onVerify = { code -> viewModel.verifyCode(state.phone, state.phoneCodeHash, code) },
                    onBack = { viewModel.clearError() },
                    onClearError = viewModel::clearError
                )
                else -> PhoneScreen(
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onSendCode = viewModel::sendCode,
                    onClearError = viewModel::clearError
                )
            }
        }
    }
}

@Composable
private fun HasikitLogo(size: Int = 80) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF1A237E), Color(0xFF0D0D2B)))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Hasikit",
            tint = PrimaryBlue,
            modifier = Modifier.size((size * 0.52f).dp)
        )
    }
}

@Composable
private fun PhoneScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onSendCode: (String) -> Unit,
    onClearError: () -> Unit
) {
    var phone by remember { mutableStateOf("+") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        HasikitLogo(72)
        Spacer(Modifier.height(16.dp))
        Text("HASIKIT", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, letterSpacing = 3.sp, color = Color.White)
        Text("Aapki Muskurahat ki Chavi", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
        Spacer(Modifier.height(48.dp))
        Text("Sign in with Telegram", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(6.dp))
        Text(
            "Enter your phone number to receive a\nverification code via Telegram.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { v -> onClearError(); phone = if (v.startsWith("+")) v else "+$v" },
            label = { Text("Phone Number") },
            placeholder = { Text("+91 98765 43210") },
            leadingIcon = { Icon(Icons.Default.Phone, null, tint = PrimaryBlue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (phone.length > 4 && !isLoading) onSendCode(phone) }),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = authFieldColors()
        )
        if (errorMessage != null) { Spacer(Modifier.height(10.dp)); ErrorBanner(errorMessage) }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onSendCode(phone) },
            enabled = phone.length > 4 && !isLoading,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
            } else {
                Text("Send Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(32.dp))
        Text(
            "By continuing you agree to Telegram's Terms of Service.\nHasikit does not store your credentials.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.3f),
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun OtpScreen(
    phone: String,
    isLoading: Boolean,
    errorMessage: String?,
    onVerify: (String) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape).background(PrimaryBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Message, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Check Telegram", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("We sent a verification code to", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.55f))
        Spacer(Modifier.height(4.dp))
        Text(phone, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { v ->
                onClearError()
                if (v.length <= 6 && v.all { it.isDigit() }) {
                    code = v
                    if (v.length == 5) onVerify(v)
                }
            },
            label = { Text("Verification Code") },
            placeholder = { Text("12345") },
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (code.length >= 4 && !isLoading) onVerify(code) }),
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = authFieldColors()
        )
        if (errorMessage != null) { Spacer(Modifier.height(10.dp)); ErrorBanner(errorMessage) }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onVerify(code) },
            enabled = code.length >= 4 && !isLoading,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
            } else {
                Text("Verify Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp), tint = Color.White.copy(alpha = 0.5f))
            Spacer(Modifier.width(6.dp))
            Text("Wrong number? Go back", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = PrimaryBlue,
    unfocusedLabelColor = Color.White.copy(alpha = 0.45f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = PrimaryBlue,
    focusedContainerColor = Color.White.copy(alpha = 0.04f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
)
