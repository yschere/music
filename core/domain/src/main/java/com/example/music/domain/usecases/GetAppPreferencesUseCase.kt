package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.AlbumSortOrder
import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.ArtistSortOrder
import com.example.music.data.repository.ComposerSortOrder
import com.example.music.data.repository.GenreSortOrder
import com.example.music.data.repository.PlaylistSortOrder
import com.example.music.data.repository.RepeatType
import com.example.music.data.repository.ShuffleType
import com.example.music.data.repository.SongSortOrder
import kotlinx.coroutines.flow.Flow
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "App Preferences Use Case"

/**
 * Functions for Accessing App Preferences DataStore through a repository
 */
class GetAppPreferencesUseCase @Inject constructor(
    private val appPrefRepo: AppPreferencesRepo,
) {
    operator fun invoke(): Flow<AppPreferences> {
        Log.i(TAG, "Get App Preferences Flow - ${appPrefRepo.appPreferencesFlow}")
        return appPrefRepo.appPreferencesFlow
    }

    suspend fun updateRepeatType(rpType: RepeatType) {
        Log.i(TAG, "Update Current Preferences - RepeatType to $rpType")
        appPrefRepo.updateRepeatType(rpType)
    }

    suspend fun updateShuffleType(shType: ShuffleType) {
        Log.i(TAG, "Update Current Preferences - ShuffleType to $shType")
        appPrefRepo.updateShuffleType(shType)
    }

    suspend fun updateAlbumSortOrder(albSort: AlbumSortOrder) {
        Log.i(TAG, "Update Current Preferences - AlbumSortOrder to $albSort")
        appPrefRepo.updateAlbumSortOrder(albSort)
    }

    suspend fun updateArtistSortOrder(artSort: ArtistSortOrder) {
        Log.i(TAG, "Update Current Preferences - ArtistSortOrder to $artSort")
        appPrefRepo.updateArtistSortOrder(artSort)
    }

    suspend fun updateComposerSortOrder(cmpSort: ComposerSortOrder) {
        Log.i(TAG, "Update Current Preferences - ComposerSortOrder to $cmpSort")
        appPrefRepo.updateComposerSortOrder(cmpSort)
    }

    suspend fun updateGenreSortOrder(genSort: GenreSortOrder) {
        Log.i(TAG, "Update Current Preferences - GenreSortOrder to $genSort")
        appPrefRepo.updateGenreSortOrder(genSort)
    }

    suspend fun updatePlaylistSortOrder(plySort: PlaylistSortOrder) {
        Log.i(TAG, "Update Current Preferences - PlaylistSortOrder to $plySort")
        appPrefRepo.updatePlaylistSortOrder(plySort)
    }

    suspend fun updateSongSortOrder(sngSort: SongSortOrder) {
        Log.i(TAG, "Update Current Preferences - SongSortOrder to $sngSort")
        appPrefRepo.updateSongSortOrder(sngSort)
    }

    suspend fun updateAlbumAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Current Preferences - updateAlbumAsc to $isAsc")
        appPrefRepo.updateAlbumAsc(isAsc)
    }

    suspend fun updateArtistAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Current Preferences - updateArtistAsc to $isAsc")
        appPrefRepo.updateArtistAsc(isAsc)
    }

    suspend fun updateGenreAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Current Preferences - updateGenreAsc to $isAsc")
        appPrefRepo.updateGenreAsc(isAsc)
    }
}
