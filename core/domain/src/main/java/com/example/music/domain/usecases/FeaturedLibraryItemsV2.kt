package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.FeaturedLibraryItemsFilterV2
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.MediaRepo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val TAG = "FeaturedLibraryItemsV2"

class FeaturedLibraryItemsV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterV2> {
        Log.i(TAG, "Start fetching most recent albums and most recent songs")

        // albumItems should return albumRepo date created desc limit 5
        val albumItems = resolver.mostRecentAlbums(5)
        Log.i(TAG, "album items == $albumItems")

        // mediaItems should return songRepo date created desc limit 10
        val mediaItems = resolver.mostRecentSongs(10)
        Log.i(TAG, "media items == $mediaItems")

        return combine(
            mediaItems,
            albumItems
        ) { mediaIds, albumIds ->
            Log.i(TAG, "Building Featured Library from fetched IDs")
            FeaturedLibraryItemsFilterV2(
                recentAlbums = albumIds.map { albumId ->
                    Log.i(TAG, "Fetch Album from AlbumID - $albumId")
                    resolver.getAlbum(albumId).asExternalModel()
                },
                recentlyAddedSongs = mediaIds.map { mediaId ->
                    Log.i(TAG, "Fetch Song from SongID - $mediaId")
                    resolver.getAudio(mediaId).asExternalModel()
                },
            )
        }
    }
}
