package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.MediaRepo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.example.music.domain.util.domainLogger

private const val TAG = "FeaturedLibraryItemsV2"

class FeaturedLibraryItemsV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterV2> {
        Log.i(TAG, "using MediaStore - start")

        // albumItems should return albumRepo date created desc limit 5
        val albumItems = resolver.mostRecentAlbums(5)
        Log.i(TAG, "album item == $albumItems")

        // mediaItems should return songRepo date created desc limit 10
        val mediaItems = resolver.mostRecentSongs(10)
        Log.i(TAG, " media item == $mediaItems")

        return combine(
            mediaItems,
            albumItems
        ) { mediaIds, albumIds ->
            Log.i(TAG, mediaIds.toString())
            Log.i(TAG, albumIds.toString())
            FeaturedLibraryItemsFilterV2(
                recentAlbums = albumIds.map { albumId ->
                    Log.i(TAG, "AlbumID - $albumId")
                    resolver.getAlbum(albumId).asExternalModel()
                },
                recentlyAddedSongs = mediaIds.map { mediaId ->
                    Log.i(TAG, "SongID - $mediaId")
                    resolver.getAudio(mediaId).asExternalModel()
                },
            )
        }
    }
}

data class FeaturedLibraryItemsFilterV2 (
    val recentAlbums: List<AlbumInfo> = emptyList(),
    val recentlyAddedSongs: List<SongInfo> = emptyList()
)