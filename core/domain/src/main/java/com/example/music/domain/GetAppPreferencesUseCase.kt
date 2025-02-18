package com.example.music.domain

import com.example.music.data.repository.SortPreferences
import com.example.music.data.repository.SortPreferencesRepository
import kotlinx.coroutines.flow.Flow
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Retrieves Flow<[SortPreferences]> from Preferences DataStore
 */
class GetSortPreferencesUseCase @Inject constructor(
    private val sortPrefRepo: SortPreferencesRepository,
) {
    operator fun invoke(): Flow<SortPreferences> {
        domainLogger.info { "Get Sort Preferences Flow" }
        return sortPrefRepo.sortPreferencesFlow
    }
}
