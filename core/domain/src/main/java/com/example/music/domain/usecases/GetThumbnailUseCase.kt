package com.example.music.domain.usecases

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.MediaRepo.Companion.toAlbumArtUri
import javax.inject.Inject

class GetThumbnailUseCase @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    @OptIn(UnstableApi::class)
    operator fun invoke(songId: Long): Bitmap {
        return mediaRepo.loadThumb(toAlbumArtUri(songId))
    }
}

//val thumbnail: Bitmap =
//        applicationContext.contentResolver.loadThumbnail(
//        content-uri, Size(640, 480), null)