package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.FeaturedLibraryItemsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.model.getArtworkUris
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val TAG = "Featured Library Items"

/**
 * Use case to retrieve data for [FeaturedLibraryItemsFilterResult] domain model which returns
 * the most recently modified playlists and the most recently added songs to populate the
 * Home screen.
 */
class FeaturedLibraryItems @Inject constructor(
    private val mediaRepo: MediaRepo,
    //private val playlistRepo: PlaylistRepo
) {
    // Generates featured library items of PLAYLISTS and SONGS from MediaStore
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterResult> {
        Log.i(TAG, "Start fetching most recent playlists and most recent songs")
        val playlistIdsFlow = mediaRepo.mostRecentPlaylists(5)
        val songIdsFlow = mediaRepo.mostRecentSongsIds(10)

        return combine(
            playlistIdsFlow,
            songIdsFlow
        ) { playlistIds, songIds ->
            Log.i(TAG, "Building Featured Library from fetched IDs")
            if (FLAG) Log.i(TAG, "playlists size: ${playlistIds.size} :: songs size: ${songIds.size}")
            FeaturedLibraryItemsFilterResult(
                recentPlaylists = playlistIds.map { playlistId ->
                    if (FLAG) Log.i(TAG, "Fetch Playlist from ID - $playlistId")
                    val playlist = mediaRepo.getPlaylist(playlistId).asExternalModel()
                    val songs = mediaRepo.findPlaylistTracks(playlistId)
                        .map { track ->
                            if (FLAG) Log.i(TAG, "Track ID: ${track.id} -> Title: ${track.title}")
                            mediaRepo.getAudio(track.audioId)
                        }.map { audio -> audio.asExternalModel() }
                    playlist.copy(playlistImage = playlist.getArtworkUris(songs))
                },
                recentlyAddedSongs = songIds.map { songId ->
                    if (FLAG) Log.i(TAG, "Fetch Song from ID - $songId")
                    val audio = mediaRepo.getAudio(songId)
                    audio.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
                },
            )
        }
    }

    /* // Generates featured library items of PLAYLISTS from MusicDatabase and SONGS from MediaStore
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterResult> {
        Log.i(TAG, "Start fetching most recent playlists and most recent songs")
        val playlistsFlow = playlistRepo.sortPlaylistsByDateLastPlayedDesc(5)
        val songIdsFlow = mediaRepo.mostRecentSongsIds(10)

        return combine(
            playlistsFlow,
            songIdsFlow
        ) { playlists, songIds ->
            FeaturedLibraryItemsFilterResult(
                recentPlaylists = playlists.map { playlist ->
                    if (FLAG) Log.i(TAG, "Fetch Playlist from ID - ${playlist.playlist.id}")
                    playlist.asExternalModel()
                },
                recentlyAddedSongs = songIds.map { songId ->
                    if (FLAG) Log.i(TAG, "Fetch Song from SongID - songId")
                    val audio = mediaRepo.getAudio(songId)
                    audio.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
                },
            )
        }
    }*/
}