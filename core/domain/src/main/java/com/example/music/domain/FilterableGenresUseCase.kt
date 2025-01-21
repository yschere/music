/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.domain

import com.example.music.data.repository.GenreStore
import com.example.music.model.FilterableGenresModel
import com.example.music.model.GenreInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for categories that can be used to filter podcasts.
 */
class FilterableGenresUseCase @Inject constructor(
    private val genreStore: GenreStore
) {
    /**
     * Created a [FilterableGenresModel] from the list of categories in [genreStore].
     * @param selectedGenre the currently selected category. If null, the first category
     *        returned by the backing category list will be selected in the returned
     *        FilterableCategoriesModel
     */
    //tries to take in param selectedGenre as GenreInfo to transform to save into genreStore
    //with genreStore called on genresSortedByAlbumCount, map the genres to the
    //FilterableGenresModel where the genres will be transformed
    operator fun invoke(selectedGenre: GenreInfo?): Flow<FilterableGenresModel> =
        genreStore.genresSortedByAlbumCount().map { genres ->
            FilterableGenresModel(
                genres = genres.map { it.asExternalModel() },
                selectedGenre = selectedGenre
                    ?: genres.firstOrNull()?.asExternalModel()
            )
        }
}
