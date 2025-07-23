package com.example.music.domain.usecases

import android.content.ContentResolver
import android.graphics.Bitmap
import android.util.Size
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.toAlbumArtUri
import javax.inject.Inject

class GetThumbnailUseCase @Inject constructor(
    private val resolver: MediaRepo
) {
    @OptIn(UnstableApi::class)
    operator fun invoke(songId: Long): Bitmap {
        return resolver.loadThumb(toAlbumArtUri(songId))
    }
}

//val thumbnail: Bitmap =
//        applicationContext.contentResolver.loadThumbnail(
//        content-uri, Size(640, 480), null)