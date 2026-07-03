package com.trixsearch.hasikit.util

import android.util.Log
import com.trixsearch.hasikit.domain.model.Video

private const val TAG = "SampleData"

object SampleData {
    val videos = listOf(
        // MP4 - Google CDN (no hotlink protection, reliable)
        Video(
            id = "1",
            title = "Big Buck Bunny",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            duration = 596000,
            size = 158_000_000
        ),
        // MP4 - Google CDN
        Video(
            id = "2",
            title = "Elephant's Dream",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            duration = 653000,
            size = 190_000_000
        ),
        // MP4 - Google CDN short clip
        Video(
            id = "3",
            title = "For Bigger Blazes",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            duration = 15000,
            size = 10_000_000
        ),
        // MP4 - Google CDN short clip
        Video(
            id = "4",
            title = "For Bigger Escapes",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            duration = 15000,
            size = 10_000_000
        ),
        // MP4 - Google CDN short clip
        Video(
            id = "5",
            title = "For Bigger Fun",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            duration = 60000,
            size = 25_000_000
        ),
        // MP4 - Google CDN short clip
        Video(
            id = "6",
            title = "For Bigger Joyrides",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            duration = 15000,
            size = 10_000_000
        ),
        // WebM - open media format test
        Video(
            id = "7",
            title = "Subaru Outback (WebM)",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            duration = 60000,
            size = 30_000_000
        ),
        // MP4 - Tears of Steel (Blender Foundation, open CDN)
        Video(
            id = "8",
            title = "Tears of Steel",
            thumbnail = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            duration = 734000,
            size = 185_000_000
        )
    ).also {
        Log.d(TAG, "SampleData initialized with ${it.size} videos")
        it.forEach { v -> Log.d(TAG, "  [${v.id}] ${v.title} -> ${v.videoUrl}") }
    }
}
