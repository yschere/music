package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "App Pref :: User Settings"

/**
 * Use case for accessing User Settings saved to data store
 * @property appRepo Repository for App Preferences DataStore
 */
class GetAppPreferencesUserSettings @Inject constructor(
    private val appRepo: AppPreferencesRepo,
) {
    operator fun invoke(): Flow<UserSettings> {
        Log.i(TAG, "Get UserSettings Flow")
        return appRepo.userSettingsFlow
    }
}