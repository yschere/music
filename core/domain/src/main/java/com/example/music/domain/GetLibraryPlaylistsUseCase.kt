package com.example.music.domain

import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.repository.PlaylistRepo
import com.example.music.model.PlaylistInfo
import com.example.music.model.asExternalModel
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving library playlists to populate Playlists List in Library Screen.
 * @property playlistRepo [PlaylistRepo] The repository for accessing Playlist and SongPlaylistEntry data
 */
class GetLibraryPlaylistsUseCase @Inject constructor(
    private val playlistRepo: PlaylistRepo
) {
    /**
     * Invoke to create a list of [PlaylistInfo] from all of the playlists in [playlistRepo].
     * @param sortOption [String] The data property/attribute to sort by. If not met, default to sorting by playlist name.
     * @param isAscending [Int] The order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<PlaylistInfo>> {
        val playlistsList: Flow<List<PlaylistWithExtraInfo>> //= flowOf()
        domainLogger.info { "Building Playlists List:\n Sort Option: $sortOption, isAscending: $isAscending" }

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {

            "DATE_CREATED" -> { //"dateCreated" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateCreatedAsc()
                    else playlistRepo.sortPlaylistsByDateCreatedDesc()
            }

            "DATE_LAST_ACCESSED" -> { //"dateLastAccessed" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateLastAccessedAsc()
                    else playlistRepo.sortPlaylistsByDateLastAccessedDesc()
            }

            "DATE_LAST_PLAYED" -> { //"dateLastPlayed" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateLastPlayedAsc()
                    else playlistRepo.sortPlaylistsByDateLastPlayedDesc()
            }

            "SONG_COUNT" -> { //"songCount" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsBySongCountAsc()
                    else playlistRepo.sortPlaylistsBySongCountDesc()
            }

            else -> { //"NAME" //"name"
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByNameAsc()
                    else playlistRepo.sortPlaylistsByNameDesc()
            }
        }

        return playlistsList.map { items ->
            domainLogger.info { "********** Library Playlists count: ${items.size} **********" }
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}