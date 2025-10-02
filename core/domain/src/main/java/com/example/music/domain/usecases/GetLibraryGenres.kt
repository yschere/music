package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Library Genres"

class GetLibraryGenres @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<GenreInfo> {
        val genresList: List<Genre>
        Log.i(TAG, "START - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            "NAME" -> {
                genresList = mediaRepo.getAllGenres(
                    order = MediaStore.Audio.Genres.NAME,
                    ascending = isAscending
                )
            }

            "SONG_COUNT" -> {
                genresList = mediaRepo.getAllGenres(
                    order = MediaStore.Audio.Genres._ID,
                    ascending = isAscending
                )
            }

            else -> {
                genresList = mediaRepo.getAllGenres(
                    order = MediaStore.Audio.Genres.NAME,
                    ascending = isAscending
                )
            }
        }

        Log.i(TAG, "********** Library Genres count: ${genresList.size} **********")
        return genresList.map { genre ->
            Log.i(TAG, "**** Genre: ${genre.id} + ${genre.name} ****")
            genre.asExternalModel()
        }
    }
}