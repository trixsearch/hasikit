package com.trixsearch.hasikit.ui.screens.request

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.trixsearch.hasikit.telegram.service.TelegramClientService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import kotlin.coroutines.resume

private const val TAG = "RequestContent"
// Bot username to send requests to
private const val BOT_USERNAME = "hasikit_m_bot"

@HiltViewModel
class RequestContentViewModel @Inject constructor(
    private val telegramClientService: TelegramClientService
) : ViewModel() {

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState

    sealed class SendState {
        object Idle : SendState()
        object Sending : SendState()
        object Success : SendState()
        data class Error(val message: String) : SendState()
    }

    // Resolve bot username to chatId then send message directly via TDLib
    fun sendRequest(contentName: String, type: String?, year: String?) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            try {
                // Build the formatted request message
                val typePart = if (!type.isNullOrBlank()) " ($type)" else ""
                val yearPart = if (!year.isNullOrBlank()) "\nYear: $year" else ""
                val message = "I would like to request:\n\n$contentName$typePart$yearPart"

                // Search for the bot chat by username to get chatId
                val chat = searchPublicChat(BOT_USERNAME)
                if (chat == null) {
                    _sendState.value = SendState.Error("Could not find @$BOT_USERNAME. Make sure you are connected.")
                    return@launch
                }

                // Send the message directly inside the app via TDLib
                val result = sendMessage(chat.id, message)
                if (result != null) {
                    Log.d(TAG, "Request sent successfully to chatId=${chat.id}")
                    _sendState.value = SendState.Success
                } else {
                    _sendState.value = SendState.Error("Failed to send message. Please try again.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendRequest error", e)
                _sendState.value = SendState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() { _sendState.value = SendState.Idle }

    private suspend fun searchPublicChat(username: String): TdApi.Chat? =
        suspendCancellableCoroutine { cont ->
            telegramClientService.send(TdApi.SearchPublicChat(username)) { result ->
                cont.resume(if (result is TdApi.Chat) result else null)
            }
            cont.invokeOnCancellation {}
        }

    private suspend fun sendMessage(chatId: Long, text: String): TdApi.Message? =
        suspendCancellableCoroutine { cont ->
            val content = TdApi.InputMessageText(
                TdApi.FormattedText(text, emptyArray()),
                null,
                false
            )
            // Pass null for topicId (MessageTopic) — not a forum thread, plain chat message
            telegramClientService.send(TdApi.SendMessage(chatId, null, null, null, null, content)) { result ->
                cont.resume(if (result is TdApi.Message) result else null)
            }
            cont.invokeOnCancellation {}
        }
}

// Content type options for the dropdown
private val CONTENT_TYPES = listOf("Movie", "Anime", "Web Series", "TV Show", "Documentary", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestContentScreen(
    navController: NavController,
    viewModel: RequestContentViewModel = hiltViewModel()
) {
    val sendState by viewModel.sendState.collectAsState()

    var contentName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var yearInput by remember { mutableStateOf("") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // Chat-like message list — shows sent requests in session
    val sentMessages = remember { mutableStateListOf<String>() }

    // Reset to idle and add to chat list on success
    LaunchedEffect(sendState) {
        if (sendState is RequestContentViewModel.SendState.Success) {
            val typePart = if (!selectedType.isNullOrBlank()) " (${selectedType})" else ""
            val yearPart = if (yearInput.isNotBlank()) "\nYear: $yearInput" else ""
            sentMessages.add("I would like to request:\n\n$contentName$typePart$yearPart")
            contentName = ""
            selectedType = null
            yearInput = ""
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Content", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                // imePadding ensures keyboard does not overlap form fields
                .imePadding()
        ) {
            // Chat-like message area showing sent requests
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                // Bot info header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.SmartToy, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    "@$BOT_USERNAME",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                if (sentMessages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.MovieFilter, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                                Spacer(Modifier.height(8.dp))
                                Text("Send a request below", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Render sent messages as chat bubbles
                items(sentMessages.size) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                sentMessages[index],
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Error state bubble
                if (sendState is RequestContentViewModel.SendState.Error) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.errorContainer) {
                                Text(
                                    (sendState as RequestContentViewModel.SendState.Error).message,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Input form area — scrollable so fields stay visible when keyboard is open
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Required: Content Name
                OutlinedTextField(
                    value = contentName,
                    onValueChange = { contentName = it },
                    label = { Text("Content Name *") },
                    placeholder = { Text("e.g. Inception, Attack on Titan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Movie, null, modifier = Modifier.size(18.dp)) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Optional: Type dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedType ?: "",
                            onValueChange = {},
                            label = { Text("Type (optional)") },
                            placeholder = { Text("Movie, Anime…") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { typeDropdownExpanded = true }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = { selectedType = null; typeDropdownExpanded = false }
                            )
                            CONTENT_TYPES.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { selectedType = type; typeDropdownExpanded = false }
                                )
                            }
                        }
                    }

                    // Optional: Year
                    OutlinedTextField(
                        value = yearInput,
                        onValueChange = { if (it.length <= 4) yearInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Year") },
                        placeholder = { Text("2024") },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Send button
                Button(
                    onClick = { viewModel.sendRequest(contentName.trim(), selectedType, yearInput.trim().ifBlank { null }) },
                    enabled = contentName.isNotBlank() && sendState !is RequestContentViewModel.SendState.Sending,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (sendState is RequestContentViewModel.SendState.Sending) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Sending…")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Send Request")
                    }
                }
            }
        }
    }
}
