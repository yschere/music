package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Library Artist V2"

class GetLibraryArtistsV2 @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<ArtistInfo> {
        val artistsList: List<Artist>
        Log.i(TAG, "Start - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            "ALBUM_COUNT" -> {
                artistsList = mediaRepo.getAllArtists(
                    order = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                    ascending = isAscending,
                )
            }

            "SONG_COUNT" -> {
                artistsList = mediaRepo.getAllArtists(
                    order = MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                    ascending = isAscending,
                )
            }

            else -> {
                artistsList = mediaRepo.getAllArtists(
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