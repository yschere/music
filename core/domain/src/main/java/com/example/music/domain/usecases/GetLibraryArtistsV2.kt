package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.Artist
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Library Artist V2"

class GetLibraryArtistsV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    /*operator fun invoke( sortOption: String, isAscending: Boolean ): Flow<List<ArtistInfo>> {
        val artistsList: Flow<List<Artist>>// = flowOf()
        domainLogger.info { "$TAG - start - sortOption: $sortOption - isAscending: $isAscending" }

        when (sortOption) {
            "ALBUM_COUNT" -> {
                artistsList = resolver.getAllArtistsFlow(
                    order = MediaRetriever.COLUMN_ARTIST_NUMBER_ALBUMS,
                    ascending = isAscending,
                )

            }

            "SONG_COUNT" -> {
                artistsList = resolver.getAllArtistsFlow(
                    order = MediaRetriever.COLUMN_ARTIST_NUMBER_TRACKS,
                    ascending = isAscending,
                )
            }

            else -> {
                artistsList = resolver.getAllArtistsFlow(
                    order = MediaRetriever.COLUMN_ARTIST_NAME,
                    ascending = isAscending,
                )
            }
        }

        return artistsList.map { items ->
            domainLogger.info { "********** Library Artists count: ${items.size} **********" }
            items.map { artist ->
                domainLogger.info { "**** Artist: ${artist.id} + ${artist.name} ****" }
                artist.asExternalModel()
            }
        }
    }*/

    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<ArtistInfo> {
        val artistsList: List<Artist>// = flowOf()
        Log.i(TAG, "Start - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            "ALBUM_COUNT" -> {
                artistsList = resolver.getAllArtists(
                    order = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                    ascending = isAscending,
                )
            }

            "SONG_COUNT" -> {
                artistsList = resolver.getAllArtists(
                    order = MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                    ascending = isAscending,
                )
            }

            else -> {
                artistsList = resolver.getAllArtists(
                    order = MediaStore.Audio.Artists.ARTIST,
                    ascending = isAscending,
                )
            }
        }

        Log.i(TAG, "********** Library Artists count: ${artistsList.size} **********")
        return artistsList.map { artist ->
            Log.i(TAG, "**** Artist: ${artist.id} + ${artist.name} ****")
            artist.asExternalModel()
        }
    }
}