package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.ArtistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Changelog:
 * 4/2/2025 - Removing SongInfo to PlayerSong conversion. PlayerSong is no longer
 * needed to display Song data in LazyList or LazyGrid in the UI, as SongInfo has
 * been updated to support this.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/** logger tag for this class */
private const val TAG = "Get Artist Details V2"

class GetArtistDetailsV2 @Inject constructor(
    private val mediaRepo: MediaRepo,
    //private val getAppPref: GetAppPreferencesUseCase,
) {
    operator fun invoke(artistId: Long): Flow<ArtistDetailsFilterResult> {
        Log.i(TAG, "Start: ArtistID: $artistId")
        val artistItem: Flow<Artist> = mediaRepo.getArtistFlow(artistId)
        val albumsList: Flow<List<Album>> = mediaRepo.getAlbumsByArtistId(artistId)
//        var order: SongSortOrder = SongSortOrder.TITLE
//        val songSortFlow = getAppPref().map { pref ->
//            order = pref.songSortOrder
//        }

        return combine(
            artistItem,
            albumsList,
            artistItem.map {
                Log.i(TAG, "Fetching songs from artist $artistId")
                mediaRepo.getArtistAudios(it.id, order = MediaStore.Audio.Media.TITLE)
            },
        ) { artist, albums, songs ->
            Log.i(TAG, "ARTIST: $artist --- \n" +
                "Artist ID: ${artist.id} \n" +
                "Artist Name: ${artist.name}" +
                "Number Songs: ${artist.numTracks} \n" +
                "Number Albums: ${artist.numAlbums}"
            )

            ArtistDetailsFilterResult(
                artist = artist.asExternalModel(),
                albums = albums.map {
                    Log.i(TAG, "ALBUM: ${it.title}")
                    it.asExternalModel()
                },
                songs = songs.map {
                    Log.i(TAG, "SONG: ${it.title}")
                    it.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(it.uri))
                },
            )
        }
    }
}