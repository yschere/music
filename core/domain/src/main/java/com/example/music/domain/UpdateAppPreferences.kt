package com.example.music.domain

import com.example.music.data.repository.PlayerPreferences
import com.example.music.data.repository.PlayerPreferencesRepository
import com.example.music.data.repository.RepeatType
import com.example.music.data.repository.ShuffleType
import kotlinx.coroutines.flow.Flow
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Functions for updating [PlayerPreferences] from Preferences DataStore
 */
class UpdatePlayerPreferences @Inject constructor(
    private val playerPrefRepo: PlayerPreferencesRepository,
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
