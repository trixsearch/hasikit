package com.trixsearch.hasikit.util

import com.trixsearch.hasikit.domain.model.Video

object SampleData {
    val videos = listOf(
        Video(
            id = "1",
            title = "Big Buck Bunny",
            thumbnail = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Big_Buck_Bunny_Terminal_Screenshot.png/640px-Big_Buck_Bunny_Terminal_Screenshot.png",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            duration = 596000,
            size = 158000000
        ),
        Video(
            id = "2",
            title = "Elephant's Dream",
            thumbnail = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Elephants_Dream_s5_both.jpg/640px-Elephants_Dream_s5_both.jpg",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            duration = 653000,
            size = 190000000
        ),
        Video(
            id = "3",
            title = "For Bigger Blazes",
            thumbnail = "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            duration = 15000,
            size = 10000000
        ),
        Video(
            id = "4",
            title = "For Bigger Escapes",
            thumbnail = "https://i.ytimg.com/vi/T39h9J-6f6k/maxresdefault.jpg",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            duration = 15000,
            size = 10000000
        )
    )
}
