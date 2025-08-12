package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Song
import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong, but only commented out this class because it used to be the
 * function that supported creation of PlayerSong for the UI. On the off chance that I do return
 * to needing a separate UI model from SongInfo, want this to be here as the historical basis
 * for how its generated.
 */

/**
 * Use case to retrieve Artist data and Album data on given song(s).
 * @property albumRepo [AlbumRepo] The repository for accessing Album data
 * @property artistRepo [ArtistRepo] The repository for accessing Artist data
 */
class GetSongDataUseCase @Inject constructor(
//    private val albumRepo: AlbumRepo,
//    private val artistRepo: ArtistRepo,
) {
//    /**
//     * Invoke with single Song to retrieve single PlayerSong data
//     * @param song [Song] to return flow of PlayerSong(song, artist, album)
//     */
//    operator fun invoke(song: Song): Flow<SongInfo> {
//        Log.i("Get Song Data Use Case", "1 song - start:\n" +
//                " song.id: ${song.id};\n" +
//                " song.artistId: ${song.artistId ?: "null"};\n" +
//                " song.albumId: ${song.albumId ?: "null"};")
//
//        val albumFlow = song.albumId?.let { albumRepo.getAlbumById(it) } ?: flowOf<Album>()
//        val artistFlow = song.artistId?.let { artistRepo.getArtistById(it) } ?: flowOf<Artist>()
//
//        return combine(
//            flowOf(song),
//            albumFlow,
//            artistFlow
//        ) {
//            _song,
//            album,
//            artist, ->
//            Log.i("Get Song Data Use Case", "Player Song Data for ${_song.id}:\n" +
//                " Song Title: ${_song.title};\n Song Artist: ${artist.name};\n" +
//                " Song Album: ${album.title};\n Song Duration: ${_song.duration};" )
//            SongInfo(
//                _song.id,
//                _song.title,
//                _song.artistId ?: 0,
//                artist.name,
//                _song.albumId ?: 0,
//                album.title,
//                _song.duration,
//                album.artwork
//            )
//        }
//    }
//
//    /**
//     * Invoke with list of Song to retrieve list of PlayerSong data. PlayerSong list built by
//     * querying for songFlow, albumFlow, and artistFlow for each song, then returning a flow combine that
//     * maps the songFlow to PlayerSong(SongInfo, ArtistInfo, AlbumInfo)
//     * @param songs [List] of type [Song] to return flow of List<PlayerSong(song, artist, album)>
//     */
//    operator fun invoke(songs: List<Song>): Flow<List<PlayerSong>> {
//        Log.i("Get Song Data Use Case", "multi songs - start:\n" +
//                " songs size: ${songs.size}")
//
//        val songListFlow = flowOf(songs)
//
//        val albumListFlow2 = songListFlow.map { _songs ->
//            _songs.map{ item ->
//                item.albumId?.let { albumRepo.getAlbumById(it) }?.firstOrNull()
//            }
//        }
//
//        val artistListFlow = songListFlow.map { _songs ->
//            _songs.map { item ->
//                item.artistId?.let { artistRepo.getArtistById(it) }?.firstOrNull()
//                //artistRepo.getArtistById(item.artistId!!).firstOrNull()//.first()
//            }
//        }
//
//        return combine(
//            songListFlow,
//            albumListFlow2,
//            artistListFlow
//        ) {
//            songList,
//            albumList,
//            artistList, ->
//            Log.i("Get Song Data Use Case", "return combine start\n" +
//                " songs size: ${songList.size};\n" +
//                " albums size: ${albumList.size};\n" +
//                " artists size: ${artistList.size};" )
//
//            //using songList for each item, look up its corresponding album and artist by item.albumId and item.artistId
//            songList.map { item ->
//                PlayerSong(
//                    songInfo = item.asExternalModel(),
//                    artistInfo = artistList.first{ artist ->
//                        artist?.id == item.artistId
//                    }?.asExternalModel() ?: ArtistInfo(),
//                    albumInfo = albumList.first{ album ->
//                        album?.id == item.albumId
//                    }?.asExternalModel() ?: AlbumInfo()
//                )
//            }
//        }
//    }
}