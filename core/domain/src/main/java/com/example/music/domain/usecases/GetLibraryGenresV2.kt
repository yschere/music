package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.Genre
import com.example.music.domain.util.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Library Genres V2"

class GetLibraryGenresV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<GenreInfo> {
        val genresList: List<Genre>
        Log.i(TAG, "Start - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            "SONG_COUNT" -> {
                genresList = resolver.getAllGenres(
                    order = MediaStore.Audio.Genres._ID,
                    ascending = isAscending
                )
            }

            else -> {
                genresList = resolver.getAllGenres(
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