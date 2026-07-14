package com.trixsearch.hasikit.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "FastScroller"

/**
 * Fast-scroller overlay — wraps content and adds a draggable thumb on the right side.
 * Behavior matches Telegram / WhatsApp / Contacts app.
 * Only visible when the list has more than [minItemsToShow] items.
 */
@Composable
fun FastScrollerBox(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    minItemsToShow: Int = 20,
    content: @Composable BoxScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val totalItems by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }

    var containerHeightPx by remember { mutableFloatStateOf(0f) }

    val thumbHeightDp = 48.dp
    val thumbHeightPx = with(density) { thumbHeightDp.toPx() }

    var isDragging by remember { mutableStateOf(false) }
    var thumbOffsetPx by remember { mutableFloatStateOf(0f) }

    // Compute scroll fraction (0.0 top → 1.0 bottom) from list layout info
    val scrollFraction by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val total = info.totalItemsCount
            if (total == 0) return@derivedStateOf 0f
            val first = info.visibleItemsInfo.firstOrNull() ?: return@derivedStateOf 0f
            val itemH = first.size.toFloat().coerceAtLeast(1f)
            val scrolled = first.index * itemH - first.offset
            val totalScrollable = total * itemH - info.viewportSize.height
            if (totalScrollable <= 0f) 0f else (scrolled / totalScrollable).coerceIn(0f, 1f)
        }
    }

    // Sync thumb Y to list scroll position when not dragging
    LaunchedEffect(scrollFraction, containerHeightPx, isDragging) {
        if (!isDragging && containerHeightPx > 0f) {
            val maxOffset = (containerHeightPx - thumbHeightPx).coerceAtLeast(0f)
            thumbOffsetPx = (scrollFraction * maxOffset).coerceIn(0f, maxOffset)
        }
    }

    val showScroller = totalItems >= minItemsToShow

    // Log fast-scroller initialization state
    LaunchedEffect(showScroller, totalItems) {
        Log.d(TAG, "FastScroller visible=$showScroller totalItems=$totalItems")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerHeightPx = it.height.toFloat() }
    ) {
        content()

        // Render thumb only when list is long enough to warrant fast scrolling
        if (showScroller) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    // Wide touch target so thumb is easy to grab
                    .width(32.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isDragging = true
                                Log.d(TAG, "FastScroller drag started")
                            },
                            onDragEnd = {
                                isDragging = false
                                Log.d(TAG, "FastScroller drag ended")
                            },
                            onDragCancel = { isDragging = false },
                            onVerticalDrag = { _, dragAmount ->
                                val maxOffset = (containerHeightPx - thumbHeightPx).coerceAtLeast(0f)
                                thumbOffsetPx = (thumbOffsetPx + dragAmount).coerceIn(0f, maxOffset)
                                // Convert thumb position fraction to target list index and jump
                                val fraction = if (maxOffset > 0f) thumbOffsetPx / maxOffset else 0f
                                val targetIndex = (fraction * (totalItems - 1))
                                    .roundToInt().coerceIn(0, totalItems - 1)
                                scope.launch { listState.scrollToItem(targetIndex) }
                            }
                        )
                    }
            ) {
                // Visual thumb pill — highlighted while dragging
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, thumbOffsetPx.roundToInt()) }
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp)
                        .size(width = 4.dp, height = thumbHeightDp)
                        .background(
                            color = if (isDragging)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}
