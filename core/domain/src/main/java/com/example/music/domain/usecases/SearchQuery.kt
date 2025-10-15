package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.domain.model.SearchQueryFilterResult
import com.example.music.domain.model.asExternalModel
import javax.inject.Inject

private const val TAG = "Search Query"

/**
 * Use case to search the music library's songs, artists and albums
 * for any title or name that is similar to the provided query string.
 */
class SearchQuery @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    suspend operator fun invoke(query: String): SearchQueryFilterResult {
        Log.i(TAG, "START --- query: $query")

        val audios = mediaRepo.findAudios(
            query = query,
            //limit = 20,
        )?.map { audio ->
            audio.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
        } ?: emptyList()

        val artists = mediaRepo.findArtists(
            query = query,
            //limit = 20,
        )?.map { artist ->
            artist.asExternalModel()
        } ?: emptyList()

        val albums = mediaRepo.findAlbums(
            query = query,
            //limit = 20,
        )?.map { album ->
            album.asExternalModel()
        } ?: emptyList()

        Log.i(TAG, "Results:\n" +
            "Songs list: ${audios.size}\n" +
            "Artists list: ${artists.size}\n" +
            "Albums list: ${albums.size}")

        return SearchQueryFilterResult(
            songs = audios,
            songCount = audios.size,
            artists = artists,
            artistCount = artists.size,
            albums = albums,
            albumCount = albums.size,
        )
    }
}
