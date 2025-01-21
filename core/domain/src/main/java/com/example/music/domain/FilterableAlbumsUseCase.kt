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

import com.example.music.data.repository.AlbumStore
import com.example.music.model.FilterableAlbumsModel
import com.example.music.model.AlbumInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for categories that can be used to filter podcasts.
 */
class FilterableAlbumsUseCase @Inject constructor(
    private val albumStore: AlbumStore
) {
    /**
     * Created a [FilterableAlbumsModel] from the list of categories in [albumStore].
     * @param selectedAlbum the currently selected category. If null, the first category
     *        returned by the backing category list will be selected in the returned
     *        FilterableCategoriesModel
     */
    //tries to take in param selectedAlbum as AlbumInfo to transform to save into albumStore
    //with albumStore called on albumsSortedByAlbumCount, map the albums to the
    //FilterableAlbumsModel where the albums will be transformed
    operator fun invoke(selectedAlbum: AlbumInfo?): Flow<FilterableAlbumsModel> =
        albumStore.albumsSortedBySongCount().map { albums ->
            FilterableAlbumsModel(
                albums = albums.map { it.asExternalModel() },
                selectedAlbum = selectedAlbum
                    ?: albums.firstOrNull()?.asExternalModel()
            )
        }
}
