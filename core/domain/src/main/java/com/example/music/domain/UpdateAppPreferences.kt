package com.example.music.domain

import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.RepeatType
import com.example.music.data.repository.ShuffleType
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Functions for updating [AppPreferences] from Preferences DataStore
 */
class UpdateAppPreferences @Inject constructor(
    private val playerPrefRepo: AppPreferencesRepo,
) {

    suspend fun updateRepeatType(rpType: RepeatType) {
        domainLogger.info { "Update Current Preferences - RepeatType to $rpType" }
        playerPrefRepo.updateRepeatType(rpType)
    }

    suspend fun updateShuffleType(shType: ShuffleType) {
        domainLogger.info { "Update Current Preferences - ShuffleType to $shType" }
        playerPrefRepo.updateShuffleType(shType)
    }
}
