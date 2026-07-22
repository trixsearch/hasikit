package com.trixsearch.hasikit.ui.components;

import android.util.Log;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001aB\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00030\u000b\u00a2\u0006\u0002\b\r\u00a2\u0006\u0002\b\u000eH\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"TAG", "", "FastScrollerBox", "", "listState", "Landroidx/compose/foundation/lazy/LazyListState;", "modifier", "Landroidx/compose/ui/Modifier;", "minItemsToShow", "", "content", "Lkotlin/Function1;", "Landroidx/compose/foundation/layout/BoxScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "app_debug"})
public final class FastScrollerKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "FastScroller";
    
    /**
     * Fast-scroller overlay — wraps content and adds a draggable thumb on the right side.
     * Behavior matches Telegram / WhatsApp / Contacts app.
     * Only visible when the list has more than [minItemsToShow] items.
     */
    @androidx.compose.runtime.Composable()
    public static final void FastScrollerBox(@org.jetbrains.annotations.NotNull()
    androidx.compose.foundation.lazy.LazyListState listState, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, int minItemsToShow, @org.jetbrains.annotations.NotNull()
    androidx.compose.runtime.internal.ComposableFunction1<? super androidx.compose.foundation.layout.BoxScope, kotlin.Unit> content) {
    }
}