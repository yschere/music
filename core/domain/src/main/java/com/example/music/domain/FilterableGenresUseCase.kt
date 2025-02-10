package com.example.music.domain

import com.example.music.data.repository.GenreRepo
import com.example.music.model.FilterableGenresModel
import com.example.music.model.GenreInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for genres that can be used to filter albums, artists, songs.
 */

class FilterableGenresUseCase @Inject constructor(
    private val genreRepo: GenreRepo
) {
    /**
     * Created a [FilterableGenresModel] from the list of genres in [genreRepo].
     * @param selectedGenre the currently selected genre. If null, the first genre
     *        returned by the backing genre list will be selected in the returned
     *        FilterableGenresModel
     */
    //tries to take in param selectedGenre as GenreInfo to transform to save into genreRepo
    //with genreRepo called on genresSortedByAlbumCount, map the genres to the
    //FilterableGenresModel where the genres will be transformed
    operator fun invoke(selectedGenre: GenreInfo?): Flow<FilterableGenresModel> =
        genreRepo.sortGenresByAlbumCountDesc().map { genres ->
            FilterableGenresModel(
                genres = genres.map { it.asExternalModel() },
                selectedGenre = selectedGenre
                    ?: genres.firstOrNull()?.asExternalModel()
            )
        }
}
