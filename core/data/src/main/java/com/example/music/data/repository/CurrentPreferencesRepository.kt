package com.example.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

enum class Shuffle_Type {
    ONCE, ONLOOP
}
data class CurrentPreferences(val isShuffled: Boolean)

//want to have sortingCriteria for each table/type
//want to have shuffle setting
//want isRepeat setting
//want isShuffle

//example has showCompleted as a boolean
//example has sortOrder as an enum
//both these part of userPreferences
//taskRepo class has the task data
//task is class for a model

private const val CURRENT_PREFERENCES_NAME = "current_preferences"

private val Context.dataStore by preferencesDataStore(
    name = CURRENT_PREFERENCES_NAME
)

class CurrentPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
    context: Context
) {
    
}