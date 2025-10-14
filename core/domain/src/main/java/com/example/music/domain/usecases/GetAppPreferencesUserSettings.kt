package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "App Pref :: User Settings"

/**
 * Function for Accessing User Settings from App Preferences DataStore
 */
class GetAppPreferencesUserSettings @Inject constructor(
    private val appRepo: AppPreferencesRepo,
) {
    operator fun invoke(): Flow<UserSettings> {
        Log.i(TAG, "Get UserSettings Flow")
        return appRepo.userSettingsFlow
    }
}