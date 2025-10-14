package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.FeaturedLibraryAlbumsFilterResult
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private const val TAG = "Featured Library Albums"

/**
 * Use case to retrieve data for [FeaturedLibraryAlbumsFilterResult] domain model which returns
 * the most recently modified albums and the most recently added songs to populate the
 * Home screen.
 */
class FeaturedLibraryAlbums @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    // Generates featured library items of ALBUMS and SONGS from MediaStore
    operator fun invoke(): Flow<FeaturedLibraryAlbumsFilterResult> {
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
            FeaturedLibraryAlbumsFilterResult(
                recentAlbums = albumIds.map { albumId ->
                    if (FLAG) Log.i(TAG, "Fetch Album from AlbumID - $albumId")
                    mediaRepo.getAlbum(albumId).asExternalModel()
                },
                recentlyAddedSongs = mediaIds.map { mediaId ->
                    if (FLAG) Log.i(TAG, "Fetch Song from SongID - $mediaId")
                    val audio = mediaRepo.getAudio(mediaId)
                    audio.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
                },
            )
        }
    }
}