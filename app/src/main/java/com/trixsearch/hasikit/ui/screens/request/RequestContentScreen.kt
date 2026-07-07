package com.trixsearch.hasikit.ui.screens.request

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private const val BOT_USERNAME = "hasikit_m_bot"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestContentScreen(navController: NavController) {
    val context = LocalContext.current
    var requestText by remember { mutableStateOf("") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Your request will be sent to @$BOT_USERNAME via Telegram.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            OutlinedTextField(
                value = requestText,
                onValueChange = { requestText = it },
                label = { Text("What would you like to watch?") },
                placeholder = { Text("e.g. Attack on Titan, Inception, Breaking Bad\u2026") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 6
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Movie", "Anime", "Series").forEach { tag ->
                    SuggestionChip(
                        onClick = {
                            if (requestText.isBlank()) requestText = "I would like to request a $tag: "
                        },
                        label = { Text(tag) }
                    )
                }
            }

            Button(
                onClick = {
                    val message = "I would like to request: ${requestText.trim()}"
                    val encoded = Uri.encode(message)
                    val telegramUrl = "https://t.me/$BOT_USERNAME?text=$encoded"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    context.startActivity(intent)
                },
                enabled = requestText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Send Request via Telegram")
            }
        }
    }
}
