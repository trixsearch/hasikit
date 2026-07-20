package com.trixsearch.hasikit.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Shimmer brush — animated gradient that sweeps left-to-right to simulate loading
@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.14f),
        Color.White.copy(alpha = 0.06f)
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

// Single skeleton card matching HorizontalVideoCard dimensions
@Composable
fun SkeletonVideoCard(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(82.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .width(136.dp)
                .fillMaxSize()
                .background(brush)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            // Subtitle line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

// Section header skeleton
@Composable
fun SkeletonSectionHeader(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Box(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp)
            .width(120.dp)
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
    )
}

// Full feed skeleton — shown while first page is loading
// Matches the layout of the real feed: section header + list of cards
@Composable
fun FeedSkeletonLoader(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { SkeletonSectionHeader() }
        items(8) { SkeletonVideoCard() }
    }
}
