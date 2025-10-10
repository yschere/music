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

private const val TAG = "Get Artist Details"

class GetArtistDetails @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(artistId: Long): Flow<ArtistDetailsFilterResult> {
        Log.i(TAG, "START --- artistID: $artistId")
        val artistItem: Flow<Artist> = mediaRepo.getArtistFlow(artistId)
        val albumsList: Flow<List<Album>> = mediaRepo.getAlbumsByArtistId(artistId)

        return combine(
            artistItem,
            albumsList,
            artistItem.map {
                Log.i(TAG, "Fetching songs from artist $artistId")
                mediaRepo.getArtistAudios(it.id, order = MediaStore.Audio.Media.TITLE)
            },
        ) { artist, albums, songs ->
            Log.i(TAG, "ARTIST: $artist ---\n" +
                "Artist ID: ${artist.id}\n" +
                "Artist Name: ${artist.name}\n" +
                "Number Albums: ${artist.numAlbums}\n" +
                "Number Songs: ${artist.numTracks}"
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