package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.GenreDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.Genre
import com.example.music.domain.util.MediaRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Changelog:
 * 4/2/2025 - Removing SongInfo to PlayerSong conversion. PlayerSong is no longer
 * needed to display Song data in LazyList or LazyGrid in the UI, as SongInfo has
 * been updated to support this.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/** logger tag for this class */
private const val TAG = "Get Genre Details V2"

class GetGenreDetailsV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    operator fun invoke(genreId: Long): Flow<GenreDetailsFilterResult> {
        Log.i(TAG, "Start: GenreId: $genreId")
        val genreItem: Flow<Genre> = resolver.getGenreById(genreId)

        //val songsFlow = resolver.getSongsForGenre(genre.name)

        return combine(
            genreItem,
            genreItem.map {
                Log.i(TAG, "Fetching songs from genre $genreId")
                resolver.getGenreAudios(it.id, order = MediaStore.Audio.AudioColumns.TITLE)
            }
        ) { genre, songs ->
            Log.i(TAG, "GENRE: $genre --- \n" +
                "Genre Name: ${genre.name}")
            GenreDetailsFilterResult(
                genre = genre.asExternalModel(),
                songs = songs.map {
                    Log.i(TAG, "SONG: ${it.title}")
                    it.asExternalModel()
                },
            )
        }
    }
}