package com.example.music.domain

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Song
import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.model.asExternalModel
import com.example.music.player.model.PlayerSong
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to retrieve Artist data and Album data on given song(s).
 * @param song [Song] to return flow of PlayerSong(song, artist, album)
 * @param songs List of type [Song] to return flow of List<PlayerSong(song, artist, album)>
 */
class GetSongDataUseCase @Inject constructor(
    private val albumRepo: AlbumRepo,
    private val artistRepo: ArtistRepo,
) {
    //invoke with single Song to retrieve single PlayerSong
    operator fun invoke(song: Song): Flow<PlayerSong> {
        domainLogger.info { "GetSongDataUseCase start" }
        domainLogger.info { "song.id: ${song.id}; song.artistId: ${song.artistId}"}

        val albumFlow = song.albumId?.let { albumRepo.getAlbumById(it) } ?: flowOf<Album>()
        val artistFlow = song.artistId?.let { artistRepo.getArtistById(it) } ?: flowOf<Artist>()

        return combine(flowOf(song), albumFlow, artistFlow) {
            _song, album, artist ->
            domainLogger.info { "Player Song Data for ${_song.id}:\n " +
                    "Song Title: ${_song.title};\n Song Artist: ${artist.name};\n " +
                    "Song Album: ${album.title};\n Song Duration: ${_song.duration};" }
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
        domainLogger.info { "GetSongDataUseCase start" }
        domainLogger.info { "songs size: ${songs.size}" }


        val songListFlow = flowOf(songs)

        val albumListFlow2 = songListFlow.map { _songs ->
            _songs.map{ item ->
                item.albumId?.let { albumRepo.getAlbumById(it) }?.firstOrNull()
            }
        }

        val artistListFlow = songListFlow.map { _songs ->
            _songs.map { item ->
                item.artistId?.let { artistRepo.getArtistById(it) }?.firstOrNull()
                //artistRepo.getArtistById(item.artistId!!).firstOrNull()//.first()
            }
        }

        return combine(songListFlow, albumListFlow2, artistListFlow) {
            songList,
            albumList,
            artistList ->
            domainLogger.info { "GetSongDataUseCase - return combine start" }
            domainLogger.info { "songs size: ${songList.size}" }
            domainLogger.info { "albums size: ${albumList.size}" }
            domainLogger.info { "artists size: ${artistList.size}" }

            //using songList for each item, look up its corresponding album and artist by item.albumId and item.artistId
            songList.map { item ->
                PlayerSong(
                    songInfo = item.asExternalModel(),
                    artistInfo = artistList.first{ artist -> artist?.id == item.artistId }!!.asExternalModel(),
                    albumInfo = albumList.first{ album -> album?.id == item.albumId }!!.asExternalModel()
                )
            }
        }
    }
}