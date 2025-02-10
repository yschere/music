package com.example.music.domain

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.repository.PlaylistRepo
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
    private val playlistRepo: PlaylistRepo
) {
    /**
     * Create a [PlaylistSortModel] from the list of playlists in [playlistRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by playlist title.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<PlaylistSortModel> {
        //how to choose which one is mapped, since either one can happen
        var playlistsList: Flow<List<Playlist>> = flowOf()
        var playlistsExtraList: Flow<List<PlaylistWithExtraInfo>> = flowOf()
        when (sortOption) {
            "dateCreated" -> {
                playlistsList = if (isAscending) playlistRepo.sortPlaylistsByDateCreatedAsc() else playlistRepo.sortPlaylistsByDateCreatedDesc()
                return playlistsList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistRepo.count()
                    )
                }
            }
            "dateLastAccessed" -> {
                playlistsList = if (isAscending) playlistRepo.sortPlaylistsByDateLastAccessedAsc() else playlistRepo.sortPlaylistsByDateLastAccessedDesc()
                return playlistsList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistRepo.count()
                    )
                }
            }
            "dateLastPlayed" -> {
                playlistsExtraList = if (isAscending) playlistRepo.sortPlaylistsByDateLastPlayedAsc() else playlistRepo.sortPlaylistsByDateLastPlayedDesc()
                return playlistsExtraList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistRepo.count()
                    )
                }
            }
            "songCount" -> {
                playlistsExtraList = if (isAscending) playlistRepo.sortPlaylistsBySongCountAsc() else playlistRepo.sortPlaylistsBySongCountDesc()
                return playlistsExtraList.map { playlists ->
                    PlaylistSortModel(
                        playlists = playlists.map { it.asExternalModel() },
                        count = playlistRepo.count()
                    )
                }
            }
//            "duration" -> {
//
//            }
            else -> {
                playlistsList = if (isAscending) playlistRepo.sortPlaylistsByNameAsc() else playlistRepo.sortPlaylistsByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return playlistsList.map { playlists ->
            PlaylistSortModel(
                playlists = playlists.map { it.asExternalModel() },
                count = playlistRepo.count()
            )
        }
    }
}