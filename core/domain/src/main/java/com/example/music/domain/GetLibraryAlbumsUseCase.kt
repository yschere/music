package com.example.music.domain

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.repository.PlaylistStore
import com.example.music.model.PlaylistInfo
import com.example.music.model.PlaylistSortModel
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import javax.inject.Inject

/**
 * Use case for retrieving library playlists to populate Playlists List in Library Screen.
 */
class GetLibraryPlaylistsUseCase @Inject constructor(
    private val playlistStore: PlaylistStore
) {
    /**
     * Create a [PlaylistSortModel] from the list of playlists in [playlistStore].
     * @param sortOption: the column to sort by. If not met, default to sorting by playlist title.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<PlaylistSortModel> {
        //how to choose which one is mapped, since either one can happen
        var playlistsList: Flow<List<Playlist>> = flowOf()
        var playlistsExtraList: Flow<List<PlaylistWithExtraInfo>> = flowOf()
        when (sortOption) {
            "dateCreated" -> {
                playlistsList = if (isAscending) playlistStore.sortPlaylistsByDateCreatedAsc() else playlistStore.sortPlaylistsByDateCreatedDesc()
                return playlistsList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistStore.count()
                    )
                }
            }
            "dateLastAccessed" -> {
                playlistsList = if (isAscending) playlistStore.sortPlaylistsByDateLastAccessedAsc() else playlistStore.sortPlaylistsByDateLastAccessedDesc()
                return playlistsList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistStore.count()
                    )
                }
            }
            "dateLastPlayed" -> {
                playlistsExtraList = if (isAscending) playlistStore.sortPlaylistsByDateLastPlayedAsc() else playlistStore.sortPlaylistsByDateLastPlayedDesc()
                return playlistsExtraList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistStore.count()
                    )
                }
            }
            "songCount" -> {
                playlistsExtraList = if (isAscending) playlistStore.sortPlaylistsBySongCountAsc() else playlistStore.sortPlaylistsBySongCountDesc()
                return playlistsExtraList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistStore.count()
                    )
                }
            }
//            "duration" -> {
//
//            }
            else -> {
                playlistsList = if (isAscending) playlistStore.sortPlaylistsByNameAsc() else playlistStore.sortPlaylistsByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return playlistsList.map { playlists ->
            PlaylistSortModel(
                playlists = playlists.map { it.asExternalModel() },
                count = playlistStore.count()
            )
        }
    }
}