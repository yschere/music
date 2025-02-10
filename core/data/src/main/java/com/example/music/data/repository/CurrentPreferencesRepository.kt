package com.example.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

enum class ShuffleType {
    ONCE, ONLOOP
}

enum class RepeatType {
    OFF, ONE, ON
}

enum class SongSortOrder {
    TITLE, ARTIST, ALBUM, DATE_ADDED, LAST_PLAYED//, DURATION, FILE_SIZE
}

enum class ArtistSortOrder {
    NAME, SONG_COUNT, ALBUM_COUNT
}

enum class AlbumSortOrder {
    TITLE, SONG_COUNT, ALBUM_ARTIST
}

enum class GenreSortOrder {
    NAME, SONG_COUNT//, ARTIST_COUNT, ALBUM_COUNT
}

enum class PlaylistSortOrder {
    NAME, SONG_COUNT, DATE_CREATED, DATE_LAST_ACCESSED
}

data class CurrentPreferences(
    val isShuffled: Boolean,
    val shuffleType: ShuffleType,
    val repeatType: RepeatType,
    val songSortOrder: SongSortOrder,
    val artistSortOrder: ArtistSortOrder,
    val albumSortOrder: AlbumSortOrder,
    val genreSortOrder: GenreSortOrder,
    val playlistSortOrder: PlaylistSortOrder
)

//example has showCompleted as a boolean
//example has sortOrder as an enum
//both these part of userPreferences
//taskRepo class has the task data
//task is class for a model

private const val CURRENT_PREFERENCES_NAME = "current_preferences"

private object PreferenceKeys {
    val IS_SHUFFLED = booleanPreferencesKey("isShuffled")
    val SHUFFLE_TYPE = stringPreferencesKey("shuffle_type")
    val REPEAT_TYPE = stringPreferencesKey("repeat_type")
    val SONG_SORT_ORDER_KEY = stringPreferencesKey("song_sort_order")
    val ARTIST_SORT_ORDER_KEY = stringPreferencesKey("artist_sort_order")
    val ALBUM_SORT_ORDER_KEY = stringPreferencesKey("album_sort_order")
    val GENRE_SORT_ORDER_KEY = stringPreferencesKey("genre_sort_order")
    val PLAYLIST_SORT_ORDER_KEY = stringPreferencesKey("playlist_sort_order")
}

private val Context.dataStore by preferencesDataStore(name = CURRENT_PREFERENCES_NAME)

//private val Context.currentPreferencesStore by dataStore ()


class CurrentPreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>,
    context: Context
) {
    val currentPreferencesFlow: Flow<CurrentPreferences> = dataStore.data.map { preferences ->
        val isShuffled = preferences[PreferenceKeys.IS_SHUFFLED] ?: false
        val shuffleType = ShuffleType.valueOf(preferences[PreferenceKeys.SHUFFLE_TYPE] ?: ShuffleType.ONCE.name)
        val repeatType = RepeatType.valueOf(preferences[PreferenceKeys.REPEAT_TYPE] ?: RepeatType.OFF.name)
        val songSortOrder = SongSortOrder.valueOf(preferences[PreferenceKeys.SONG_SORT_ORDER_KEY] ?: SongSortOrder.TITLE.name)
        val artistSortOrder = ArtistSortOrder.valueOf(preferences[PreferenceKeys.ARTIST_SORT_ORDER_KEY] ?: ArtistSortOrder.NAME.name)
        val albumSortOrder = AlbumSortOrder.valueOf(preferences[PreferenceKeys.ALBUM_SORT_ORDER_KEY] ?: AlbumSortOrder.TITLE.name)
        val genreSortOrder = GenreSortOrder.valueOf(preferences[PreferenceKeys.GENRE_SORT_ORDER_KEY] ?: GenreSortOrder.NAME.name)
        val playlistSortOrder = PlaylistSortOrder.valueOf(preferences[PreferenceKeys.PLAYLIST_SORT_ORDER_KEY] ?: PlaylistSortOrder.NAME.name)

        CurrentPreferences(isShuffled, shuffleType, repeatType, songSortOrder, artistSortOrder, albumSortOrder, genreSortOrder, playlistSortOrder)
    }

    suspend fun updateIsShuffled(isShuffled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_SHUFFLED] = isShuffled
        }
    }
//    private val _songSortOrderFlow = MutableStateFlow(songSortOrder)
//    val sortOrderFlow = StateFlow<SortOrder> = _sortOrderFlow
}