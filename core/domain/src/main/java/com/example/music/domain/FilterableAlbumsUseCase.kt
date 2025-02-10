package com.example.music.domain

import com.example.music.data.repository.AlbumRepo
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
    private val albumRepo: AlbumRepo
) {
    /**
     * Created a [FilterableAlbumsModel] from the list of categories in [albumRepo].
     * @param selectedAlbum the currently selected category. If null, the first category
     *        returned by the backing category list will be selected in the returned
     *        FilterableCategoriesModel
     */
    //tries to take in param selectedAlbum as AlbumInfo to transform to save into albumRepo
    //with albumRepo called on albumsSortedByAlbumCount, map the albums to the
    //FilterableAlbumsModel where the albums will be transformed
    operator fun invoke(selectedAlbum: AlbumInfo?): Flow<FilterableAlbumsModel> =
        albumRepo.sortAlbumsBySongCountDesc().map { albums ->
            FilterableAlbumsModel(
                albums = albums.map { it.asExternalModel() },
                selectedAlbum = selectedAlbum
                    ?: albums.firstOrNull()?.asExternalModel()
            )
        }
}
