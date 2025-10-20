package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.repository.GenreSortList
import com.example.music.data.util.FLAG
import javax.inject.Inject

private const val TAG = "Get Library Genres"

class GetLibraryGenres @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    suspend operator fun invoke(
        sortColumn: String,
        isAscending: Boolean
    ): List<GenreInfo> {
        var genresList: List<Genre>
        Log.i(TAG, "START --- sortColumn: $sortColumn - isAscending: $isAscending")

        when (sortColumn) {
            GenreSortList[0] -> { // "Name"
                genresList = mediaRepo.findAllGenres(
                    order = MediaStore.Audio.Genres.NAME,
                    ascending = isAscending
                )?.sortedBy { it.name.lowercase() } ?: emptyList()
                if (!isAscending) genresList = genresList.reversed()
            }

            GenreSortList[1] -> { // "Song Count"
                genresList = mediaRepo.findAllGenres(
                    order = MediaStore.Audio.Genres.NAME,
                    ascending = isAscending
                )?.sortedBy { it.numTracks } ?: emptyList()
                if (!isAscending) genresList = genresList.reversed()
            }

            else -> {
                genresList = mediaRepo.findAllGenres(
                    order = MediaStore.Audio.Genres.NAME,
                    ascending = isAscending
                ) ?: emptyList()
            }
        }

        Log.i(TAG, "********** Library Genres count: ${genresList.size} **********")
        return genresList.map { genre ->
            if (FLAG) Log.i(TAG, "**** Genre: ${genre.id} + ${genre.name} + ${genre.numTracks} ****")
            genre.asExternalModel()
        }
    }
}