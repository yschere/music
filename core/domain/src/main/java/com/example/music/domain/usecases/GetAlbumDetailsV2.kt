package com.example.music.domain.usecases

import android.provider.MediaStore
import com.example.music.domain.model.AlbumDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.Album
import com.example.music.domain.util.Artist
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
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
private const val TAG = "Get Album Details V2"

class GetAlbumDetailsV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    operator fun invoke(albumId: Long): Flow<AlbumDetailsFilterResult> {
        domainLogger.info { "$TAG - start: AlbumID: $albumId" }
        val albumItem: Flow<Album> = resolver.getAlbumFlow(albumId)
        val artistItem: Flow<Artist> = resolver.getArtistByAlbumIdFlow(albumId)

        return combine(
            albumItem,
            artistItem,
            albumItem.map {
                domainLogger.info { "$TAG - make songs" }
                resolver.getAlbumAudios(it.albumId, order = MediaStore.Audio.Media.TRACK)
            }
        ) { album, artist, songs ->
            domainLogger.info { "ALBUM: $album --- \n" +
                "Album ID: ${album.albumId} \n" +
                "Album Title: ${album.title} \n" +
                "Artist: ${album.artist} \n"
            }
            domainLogger.info { "ALBUM ARTIST: $artist --- \n" +
                "Artist ID: ${artist.id} \n" +
                "Artist Name: ${artist.name} \n"
                //"Number Songs: ${artist.numTracks} \n" +
                //"Number Albums: ${artist.numAlbums}"
            }
            AlbumDetailsFilterResult(
                album = album.asExternalModel(),
                artist = artist.asExternalModel(),
                songs = songs.map {
                    domainLogger.info { "SONGINFO - PLEASE IS THERE SOMETHING IN HERE: ${it.title}"}
                    it.asExternalModel()
                },
            )
        }
    }
}