package com.example.music.domain.usecases

import android.provider.MediaStore
import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.SongSortOrder
import com.example.music.domain.model.ArtistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.player.model.audioToPlayerSong
import com.example.music.domain.util.Album
import com.example.music.domain.util.Artist
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Artist Details V2"

class GetArtistDetailsV2 @Inject constructor(
    private val resolver: MediaRepo,
    //private val getAppPref: GetAppPreferencesUseCase,
) {
    operator fun invoke(artistId: Long): Flow<ArtistDetailsFilterResult> {
        domainLogger.info { "$TAG - start - ArtistID: $artistId" }
        val artistItem: Flow<Artist> = resolver.getArtistFlow(artistId)
        val albumsList: Flow<List<Album>> = resolver.getAlbumsByArtistId(artistId)
//        var order: SongSortOrder = SongSortOrder.TITLE
//        val songSortFlow = getAppPref().map { pref ->
//            order = pref.songSortOrder
//        }

        return combine(
            artistItem,
            albumsList,
            artistItem.map {
                domainLogger.info { "$TAG - make songs" }
                resolver.getArtistAudios(it.id, order = MediaStore.Audio.Media.TITLE)//MediaStore.Audio.AudioColumns.TRACK)
            },
        ) { artist, albums, songs ->
            domainLogger.info { "ARTIST: $artist --- \n" +
                "Artist ID: ${artist.id} \n" +
                "Artist Name: ${artist.name}" }

            ArtistDetailsFilterResult(
                artist = artist.asExternalModel(),
                albums = albums.map {
                    it.asExternalModel()
                },
                songs = songs.map {
                    domainLogger.info { "SONGINFO - PLEASE IS THERE SOMETHING IN HERE: ${it.title}"}
                    it.asExternalModel()
                },
                pSongs = songs.map {
                    domainLogger.info { "PLAYERSONG - PLEASE IS THERE SOMETHING IN HERE: ${it.title} "}
                    it.audioToPlayerSong()
                }
            )
        }
    }
}