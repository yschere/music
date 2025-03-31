package com.example.music.domain.usecases

import android.provider.MediaStore
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.Genre
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Library Genres V2"

class GetLibraryGenresV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    /*operator fun invoke( sortOption: String, isAscending: Boolean ): Flow<List<GenreInfo>> {
        val genresList: Flow<List<Genre>>
        domainLogger.info { "$TAG - start - sortOption: $sortOption - isAscending: $isAscending" }

        when (sortOption) {

            "SONG_COUNT" -> {
                genresList = resolver.getAllGenresFlow()
            }

            else -> {
                genresList = resolver.getAllGenresFlow()
            }
        }

        return genresList.map { items ->
            domainLogger.info { "********** Library Genres count: ${items.size} **********" }
            items.map { genre ->
                domainLogger.info { "**** Genre: ${genre.id} + ${genre.name} ****" }
                genre.asExternalModel()
            }
        }
    }*/

    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<GenreInfo> {
        val genresList: List<Genre>
        domainLogger.info { "$TAG - start - sortOption: $sortOption - isAscending: $isAscending" }

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

        domainLogger.info { "********** Library Genres count: ${genresList.size} **********" }
        return genresList.map { genre ->
            domainLogger.info { "**** Genre: ${genre.id} + ${genre.name} ****" }
            genre.asExternalModel()
        }
    }
}