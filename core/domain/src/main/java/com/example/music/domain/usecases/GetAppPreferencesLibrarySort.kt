package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.LibrarySortOrders
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "App Pref :: Library Sort"

/**
 * Use case for accessing Library Sort Orders saved to data store
 * @property appRepo Repository for App Preferences DataStore
 */
class GetAppPreferencesLibrarySort @Inject constructor(
    private val appRepo: AppPreferencesRepo,
) {
    operator fun invoke(): Flow<LibrarySortOrders> {
        Log.i(TAG, "Get Library Sort Flow")
        return appRepo.librarySortOrdersFlow
    }
}
