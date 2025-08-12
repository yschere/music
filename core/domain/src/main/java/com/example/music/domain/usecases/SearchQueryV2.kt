package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SearchQueryFilterV2
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import javax.inject.Inject

/** Changelog:
 * Created on 4/4/2025 to invoke a search function for the app
 * to use for querying string matches to media in MediaStore.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/** logger tag for this class */
private const val TAG = "Search Query V2"

/**
 * Use case to search the music library's songs, artists and albums
 * for any title or name that is similar to the provided query string.
 */
class SearchQueryV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    //not sure yet if this needs to be suspend or return flowOf
    // forgot that the 'find' functions are suspend functions
    suspend operator fun invoke(query: String): SearchQueryFilterV2 {
        Log.i(TAG, "Start")

        //want this to trigger the find functions in MediaRepo for audio, artist, album
        // TBD if genre or composer or playlists should be included

        val audios = resolver.findAudios(
            query = query,
            limit = 20,
        ).map { audio ->
            audio.asExternalModel()
        }

        val artists = resolver.findArtists(
            query = query,
            limit = 20,
        ).map { artist ->
            artist.asExternalModel()
        }

        val albums = resolver.findAlbums(
            query = query,
            limit = 20,
        ).map { album ->
            album.asExternalModel()
        }

        Log.i(TAG, "Results: \n" +
            "Song list: ${audios.size}\n" +
            "Artists list: ${artists.size}\n" +
            "Albums list: ${albums.size}")

        return SearchQueryFilterV2(
            songs = audios,
            artists = artists,
            albums = albums,
        )
    }
}
