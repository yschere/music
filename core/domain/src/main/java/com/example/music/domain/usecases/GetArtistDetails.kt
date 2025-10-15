package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.ArtistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
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

        return combine(
            artistItem,
            artistItem.map { artist ->
                mediaRepo.getArtistAlbums(
                    artistId = artist.id,
                    order = MediaStore.Audio.Albums.ALBUM
                )
            },
            artistItem.map { artist ->
                mediaRepo.getArtistAudios(
                    artistId = artist.id,
                    order = MediaStore.Audio.Media.TITLE
                )
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
                albums = albums.map { album ->
                    if (FLAG) Log.i(TAG, "ALBUM: ${album.title}")
                    album.asExternalModel()
                },
                songs = songs.map { song ->
                    if (FLAG) Log.i(TAG, "SONG: ${song.title}")
                    song.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
                },
            )
        }
    }
}