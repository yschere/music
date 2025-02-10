package com.example.music.domain

import androidx.media3.common.Player
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Song
import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.data.repository.SongRepo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import com.example.music.player.model.PlayerSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * Use case to retrieve Artist data and Album data on given song(s).
 * @param song [Song] to return flow of PlayerSong(song, artist, album)
 * @param songs List of type [Song] to return flow of List<PlayerSong(song, artist, album)>
 */
class GetSongDataUseCase @Inject constructor(
    private val songRepo: SongRepo,
    private val albumRepo: AlbumRepo,
    private val artistRepo: ArtistRepo,
) {
    //invoke with single Song to retrieve single PlayerSong
    operator fun invoke(song: Song): Flow<PlayerSong> {
        //val songFlow = flowOf(song)
        val albumFlow = song.albumId?.let { albumRepo.getAlbumById(it) }!!
        val artistFlow = song.artistId?.let { artistRepo.getArtistById(it) }!!

        return combine(flowOf(song), albumFlow, artistFlow) {
            _song, album, artist ->
            PlayerSong(
                _song.id,
                _song.title,
                artist.name,
                album.title,
                _song.duration,
                album.artwork
            )
        }
    }

    //invoke with list of Song to retrieve list of PlayerSong
    operator fun invoke(songs: List<Song>): Flow<List<PlayerSong>> {
        //TODO: return list of PlayerSong
        // want for each item in songs to retrieve the album and artist data for it, and combine on each item to PlayerSong

        val songListFlow = flowOf(songs) //songs

        val albumListFlow2 = songListFlow.map { _songs ->
            _songs.map{ item ->
                albumRepo.getAlbumById(item.albumId!!).first()
            }
        }

        val artistListFlow = songListFlow.map { _songs ->
            _songs.map { item ->
                artistRepo.getArtistById(item.artistId!!).first()
            }
        }

        return combine(songListFlow, albumListFlow2, artistListFlow) {
            songList,
            albumList,
            artistList ->

            //use songList, for each item look up its corresponding album and artist by item.albumId and item.artistId
            songList.map { item ->
                PlayerSong(
                    songInfo = item.asExternalModel(),
                    artistInfo = artistList.single { artist -> artist.id == item.artistId }.asExternalModel(),
                    albumInfo = albumList.single { album -> album.id == item.albumId }.asExternalModel()
                )
            }
        }
    }
}