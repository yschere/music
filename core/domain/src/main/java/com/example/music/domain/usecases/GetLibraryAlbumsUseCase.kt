package com.example.music.domain.usecases

import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.repository.AlbumRepo
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving library albums to populate Albums List in Library Screen.
 * @property albumRepo [AlbumRepo] The repository for accessing Album data
 */
class GetLibraryAlbumsUseCase @Inject constructor(
    private val albumRepo: AlbumRepo
) {
    /**
     * Invoke to create a list of [AlbumInfo] from all of the albums in [albumRepo].
     * @param sortOption [String] The data property/attribute to sort by. If not met, default to sorting by album title.
     * @param isAscending [Boolean] The order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<AlbumInfo>> {
        val albumsList: Flow<List<AlbumWithExtraInfo>>// = flowOf()
        domainLogger.info { "Building Album List:\n Sort Option: $sortOption, isAscending: $isAscending" }

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {

            "ARTIST" -> { //"artist" -> {
                albumsList = if (isAscending) albumRepo.sortAlbumsByAlbumArtistAsc() else albumRepo.sortAlbumsByAlbumArtistDesc()
            }

            "DATE_LAST_PLAYED" -> { //"dateLastPlayed" -> {
                albumsList = if (isAscending) albumRepo.sortAlbumsByDateLastPlayedAsc() else albumRepo.sortAlbumsByDateLastPlayedDesc()
            }

            "SONG_COUNT" -> { //"songCount" -> {
                albumsList = if (isAscending) albumRepo.sortAlbumsBySongCountAsc() else albumRepo.sortAlbumsBySongCountDesc()
            }

            else -> { //"TITLE" //"title"
                albumsList = if (isAscending) albumRepo.sortAlbumsByTitleAsc() else albumRepo.sortAlbumsByTitleDesc()
            }
        }

        return albumsList.map { items ->
            domainLogger.info { "********** Library Albums count: ${items.size} **********" }
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}