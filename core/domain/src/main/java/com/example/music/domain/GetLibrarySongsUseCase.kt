package com.example.music.domain

import com.example.music.data.database.model.Song
import com.example.music.data.repository.SongRepo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving library songs to populate Songs List in Library Screen.
 */
class GetLibrarySongsUseCase @Inject constructor(
    private val songRepo: SongRepo
) {
    /**
     * Create a list of [SongInfo] from the list of songs in [songRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by song title.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<SongInfo>> {
        val songsList: Flow<List<Song>>// = flowOf()
        domainLogger.info { "Building Songs List: \nSort Option: $sortOption, isAscending: $isAscending" }
        when (sortOption) {

            "ARTIST" -> { //"artist" -> {
                songsList = if (isAscending) songRepo.sortSongsByArtistAsc() else songRepo.sortSongsByArtistDesc()
            }

            "ALBUM" -> { //"album" -> {
                songsList = if (isAscending) songRepo.sortSongsByAlbumAsc() else songRepo.sortSongsByAlbumDesc()
            }

            "DATE_ADDED" -> { //"dateAdded" -> {
                songsList = if (isAscending) songRepo.sortSongsByDateAddedAsc() else songRepo.sortSongsByDateAddedDesc()
            }

            "DATE_LAST_PLAYED" -> { //"dateLastPlayed" -> {
                songsList = if (isAscending) songRepo.sortSongsByDateLastPlayedAsc() else songRepo.sortSongsByDateLastPlayedDesc()
            }

            else -> { //"TITLE" //"title"
                songsList = if (isAscending) songRepo.sortSongsByTitleAsc() else songRepo.sortSongsByTitleDesc()
            }
        }

        return songsList.map { items ->
            domainLogger.info { "********** Library Songs count: ${items.size} **********" }
            items.map { it.asExternalModel() }
        }
    }
}