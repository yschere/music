package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.AlbumDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Album Details"

class GetAlbumDetails @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(albumId: Long): Flow<AlbumDetailsFilterResult> {
        Log.i(TAG, "START --- albumID: $albumId")
        val albumItem: Flow<Album> = mediaRepo.getAlbumFlow(albumId)

        return combine(
            albumItem,
            albumItem.map { mediaRepo.getArtist(id = it.artistId) },
            albumItem.map {
                mediaRepo.getAlbumAudios(
                    albumId = it.id,
                    order = MediaStore.Audio.Media.TRACK
                )
            }
        ) { album, artist, songs ->
            Log.i(TAG, "ALBUM: $album ---\n" +
                "Album ID: ${album.id}\n" +
                "Album Title: ${album.title}\n" +
                "Artist: ${album.artist}"
            )
            Log.i(TAG, "ALBUM ARTIST: $artist ---\n" +
                "Artist ID: ${artist.id}\n" +
                "Artist Name: ${artist.name}\n" +
                "Number Albums: ${artist.numAlbums}\n" +
                "Number Songs: ${artist.numTracks}"
            )
            AlbumDetailsFilterResult(
                album = album.asExternalModel(),
                artist = artist.asExternalModel(),
                songs = songs.map {
                    if (FLAG) Log.i(TAG, "SONG: ${it.title}")
                    it.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(it.uri))
                },
            )
        }
    }
}