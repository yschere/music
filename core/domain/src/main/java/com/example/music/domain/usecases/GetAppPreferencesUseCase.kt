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
}
