package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.GenreDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Genre Details"

class GetGenreDetails @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    operator fun invoke(genreId: Long): Flow<GenreDetailsFilterResult> {
        Log.i(TAG, "START --- genreId: $genreId")
        val genreItem: Flow<Genre> = mediaRepo.getGenreFlow(genreId)

        return combine(
            genreItem,
            genreItem.map {
                Log.i(TAG, "Fetching songs from genre $genreId")
                mediaRepo.getGenreAudios(it.id, order = MediaStore.Audio.AudioColumns.TITLE)
            }
        ) { genre, songs ->
            Log.i(TAG, "GENRE: $genre --- \n" +
                "Genre Name: ${genre.name}")
            GenreDetailsFilterResult(
                genre = genre.asExternalModel(),
                songs = songs.map {
                    Log.i(TAG, "SONG: ${it.title}")
                    it.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(it.uri))
                },
            )
        }
    }
}