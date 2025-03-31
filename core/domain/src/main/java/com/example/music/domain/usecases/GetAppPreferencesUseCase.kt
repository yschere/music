package com.example.music.domain

import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.AppPreferencesRepo
import kotlinx.coroutines.flow.Flow
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Retrieves Flow<[AppPreferences]> from Preferences DataStore
 */
class GetAppPreferencesUseCase @Inject constructor(
    private val appPrefRepo: AppPreferencesRepo,
) {
    operator fun invoke(): Flow<AppPreferences> {
        domainLogger.info { "Get App Preferences Flow" }
        return appPrefRepo.appPreferencesFlow
    }
}
