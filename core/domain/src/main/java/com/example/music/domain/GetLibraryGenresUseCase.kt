package com.example.music.domain

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.repository.AlbumStore
import com.example.music.model.AlbumInfo
import com.example.music.model.AlbumSortModel
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import javax.inject.Inject

/**
 * Use case for retrieving library albums to populate Albums List in Library Screen.
 */
class GetLibraryAlbumsUseCase @Inject constructor(
    private val albumStore: AlbumStore
) {
    /**
     * Create a [AlbumSortModel] from the list of albums in [albumStore].
     * @param sortOption: the column to sort by. If not met, default to sorting by album title.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<AlbumSortModel> {
        //how to choose which one is mapped, since either one can happen
        var albumsList: Flow<List<Album>> = flowOf()
        var albumsExtraList: Flow<List<AlbumWithExtraInfo>> = flowOf()
        when (sortOption) {
            "artist" -> {
                albumsList = if (isAscending) albumStore.sortAlbumsByAlbumArtistAsc() else albumStore.sortAlbumsByAlbumArtistDesc()
                return albumsList.map { albums ->
                    AlbumSortModel(
                        albums = albums.map { it.asExternalModel() },
                        count = albumStore.count()
                    )
                }
            }
            "dateLastPlayed" -> {
                albumsExtraList = if (isAscending) albumStore.sortAlbumsByDateLastPlayedAsc() else albumStore.sortAlbumsByDateLastPlayedDesc()
                return albumsExtraList.map { albums ->
                    AlbumSortModel(
                        albums = albums.map { it.asExternalModel() },
                        count = albumStore.count()
                    )
                }
            }
            "songCount" -> {
                albumsExtraList = if (isAscending) albumStore.sortAlbumsBySongCountAsc() else albumStore.sortAlbumsBySongCountDesc()
                return albumsExtraList.map { albums ->
                    AlbumSortModel(
                        albums = albums.map { it.asExternalModel() },
                        count = albumStore.count()
                    )
                }
            }
            else -> {
                albumsList = if (isAscending) albumStore.sortAlbumsByTitleAsc() else albumStore.sortAlbumsByTitleDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return albumsList.map { albums ->
            AlbumSortModel(
                albums = albums.map { it.asExternalModel() },
                count = albumStore.count()
            )
        }
    }
}