package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.repository.ArtistSortList
import javax.inject.Inject

private const val TAG = "Get Library Artists"

class GetLibraryArtists @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    suspend operator fun invoke(
        sortColumn: String,
        isAscending: Boolean
    ): List<ArtistInfo> {
        var artistsList: List<Artist>
        Log.i(TAG, "START --- sortColumn: $sortColumn - isAscending: $isAscending")

        when (sortColumn) {
            ArtistSortList[0] -> { //"Name"
                artistsList = mediaRepo.getAllArtists(
                    order = MediaStore.Audio.Artists.ARTIST,
                    ascending = isAscending,
                ).sortedBy { it.name.lowercase() }
                if (!isAscending) artistsList = artistsList.reversed()
            }

            ArtistSortList[1] -> { //"Album Count"
                artistsList = mediaRepo.getAllArtists(
                    order = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                    ascending = isAscending,
                ).sortedWith(
                    compareBy<Artist> { it.numAlbums }
                        .thenBy { it.name.lowercase() }
                )
                if (!isAscending) artistsList = artistsList.reversed()
            }

            ArtistSortList[2] -> { //"Song Count"
                artistsList = mediaRepo.getAllArtists(
                    order = MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                    ascending = isAscending,
                ).sortedWith(
                    compareBy<Artist> { it.numTracks }
                        .thenBy { it.name.lowercase() }
                )
                if (!isAscending) artistsList = artistsList.reversed()
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