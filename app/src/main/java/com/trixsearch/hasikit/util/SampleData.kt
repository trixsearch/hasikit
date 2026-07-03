package com.trixsearch.hasikit.util

import android.util.Log
import com.trixsearch.hasikit.domain.model.Video

private const val TAG = "SampleData"

object SampleData {
    val videos = listOf(
        Video(
            id = "1",
            title = "Big Buck Bunny",
            thumbnail = "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            duration = 10000,
            size = 1_500_000
        ),
        Video(
            id = "2",
            title = "Flower MP4",
            thumbnail = "https://interactive-examples.mdn.mozilla.net/media/examples/flower.jpg",
            videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            duration = 8000,
            size = 3_000_000
        ),
        Video(
            id = "3",
            title = "Flower WebM",
            thumbnail = "https://interactive-examples.mdn.mozilla.net/media/examples/flower.jpg",
            videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.webm",
            duration = 8000,
            size = 2_500_000
        )
    ).also {
        Log.d(TAG, "SampleData initialized with ${it.size} videos")
        it.forEach { v -> Log.d(TAG, "  [${v.id}] ${v.title} -> ${v.videoUrl}") }
    }
}
