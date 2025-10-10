package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.AppPreferencesRepo
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
