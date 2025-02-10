package com.example.music.domain

import com.example.music.data.database.model.Song
import com.example.music.data.repository.SongStore
import com.example.music.model.SongInfo
import com.example.music.model.SongSortModel
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import javax.inject.Inject

/**
 * Use case for retrieving library songs to populate Songs List in Library Screen.
 */
class GetLibrarySongsUseCase @Inject constructor(
    private val songStore: SongStore
) {
    /**
     * Create a [SongSortModel] from the list of songs in [songStore].
     * @param sortOption: the column to sort by. If not met, default to sorting by song title.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<SongSortModel> {
        var songsList: Flow<List<Song>> = flowOf()
        when (sortOption) {
            "artist" -> {
                songsList = if (isAscending) songStore.sortSongsByArtistAsc() else songStore.sortSongsByArtistDesc()
            }
            "album" -> {
                songsList = if (isAscending) songStore.sortSongsByAlbumAsc() else songStore.sortSongsByAlbumDesc()
            }
            "dateAdded" -> {
                songsList = if (isAscending) songStore.sortSongsByDateAddedAsc() else songStore.sortSongsByDateAddedDesc()
            }
            "dateLastPlayed" -> {
                songsList = if (isAscending) songStore.sortSongsByDateLastPlayedAsc() else songStore.sortSongsByDateLastPlayedDesc()
            }
//            "duration" -> {
//
//            }
            else -> {
                songsList = if (isAscending) songStore.sortSongsByTitleAsc() else songStore.sortSongsByTitleDesc()
            }
        }

        return songsList.map { songs ->
            SongSortModel(
                songs = songs.map { it.asExternalModel() },
                count = songStore.count()
            )
        }
    }
}