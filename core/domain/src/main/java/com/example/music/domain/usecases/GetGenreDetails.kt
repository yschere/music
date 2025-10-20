package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.GenreDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Genre Details"

/**
 * Use case to retrieve data for [GenreDetailsFilterResult] domain model which returns
 * the GenreInfo data and the genre's songs as list of SongInfo to populate the GenreDetails screen.
 * @property mediaRepo Content Resolver Repository for MediaStore
 */
class GetGenreDetails @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    operator fun invoke(genreId: Long): Flow<GenreDetailsFilterResult> {
        Log.i(TAG, "START --- genreId: $genreId")
        val genreItem: Flow<Genre> = mediaRepo.getGenreFlow(genreId)

        return combine(
            genreItem,
            genreItem.map {
                mediaRepo.getGenreAudios(
                    id = it.id,
                    order = MediaStore.Audio.AudioColumns.TITLE
                )
            }
        ) { genre, songs ->
            Log.i(TAG, "GENRE: $genre ---\n" +
                "Genre ID: ${genre.id}\n" +
                "Genre Name: ${genre.name}"
            )
            GenreDetailsFilterResult(
                genre = genre.asExternalModel(),
                songs = songs.map { song ->
                    if (FLAG) Log.i(TAG, "SONG: ${song.title}")
                    song.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
                },
            )
        }
    }
}