package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.domain.model.FeaturedLibraryItemsFilter
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private const val TAG = "Featured Library Items"

/**
 * Generates featured library items of ALBUMS and SONGS from MediaStore
 */
class FeaturedLibraryItems @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(): Flow<FeaturedLibraryItemsFilter> {
        Log.i(TAG, "Start fetching most recent albums and most recent songs")

        // albumItems should return albumRepo date created desc limit 5
        val albumIdsFlow = mediaRepo.mostRecentAlbumsIds(5)

        // mediaItems should return songRepo date created desc limit 10
        val mediaIdsFlow = mediaRepo.mostRecentSongsIds(10)

        return combine(
            mediaIdsFlow,
            albumIdsFlow
        ) { mediaIds, albumIds ->
            Log.i(TAG, "Building Featured Library from fetched IDs")
            FeaturedLibraryItemsFilter(
                recentAlbums = albumIds.map { albumId ->
                    Log.i(TAG, "Fetch Album from AlbumID - $albumId")
                    mediaRepo.getAlbum(albumId).asExternalModel()
                },
                recentlyAddedSongs = mediaIds.map { mediaId ->
                    Log.i(TAG, "Fetch Song from SongID - $mediaId")
                    val audio = mediaRepo.getAudio(mediaId)
                    audio.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
                },
            )
        }
    }
}