package com.example.music.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "App Preferences Datastore"

enum class RepeatType { OFF, ONE, ON }
enum class ShuffleType { ONCE, ON_LOOP }
val ShuffleTypeList = listOf("Once", "On Loop")
val ThemeList = listOf("System default", "Light", "Dark")

val AlbumSortList = listOf("Title", "Artist", "Song Count", "Year")
val ArtistSortList = listOf("Name", "Album Count", "Song Count")
val ComposerSortList = listOf("Name", "Song Count")
val GenreSortList = listOf("Name", "Song Count")
val PlaylistSortList = listOf("Name", "Song Count", "Date Created", "Date Last Accessed")
val SongSortList = listOf("Title", "Artist", "Album", "Date Added", "Date Modified", "Duration")

data class LibrarySortOrders(
    val albumSortColumn: String, // string for the album attributes to sort list on
    val artistSortColumn: String, // string for the artist attributes to sort list on
    val composerSortColumn: String, // string for the composer attributes to sort list on
    val genreSortColumn: String, // string for the genre attributes to sort list on
    val playlistSortColumn: String, // string for the playlist attributes to sort list on
    val songSortColumn: String, // string for the song attributes to sort list on

    val isAlbumAsc: Boolean, // if album list is in ascending or descending order
    val isArtistAsc: Boolean, // if artist list is in ascending or descending order
    val isComposerAsc: Boolean, // if composer list is in ascending or descending order
    val isGenreAsc: Boolean, // if genre list is in ascending or descending order
    val isPlaylistAsc: Boolean, // if playlist list is in ascending or descending order
    val isSongAsc: Boolean, // if song list is in ascending or descending order
)

data class UserSettings(
    val shuffleType: ShuffleType,
    val theme: String,
)

class AppPreferencesRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    /******************
     * Media Controller Settings Section - Repeat Type
     ******************/

    suspend fun getRepeatTypeAsInt(): Int =
        RepeatType.valueOf(dataStore.data.first()[REPEAT_TYPE] ?: RepeatType.OFF.name).ordinal
    val getRepeatTypeFlow: Flow<RepeatType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) }
            else throw exception
        }
        .map { preferences ->
            RepeatType.valueOf(preferences[REPEAT_TYPE] ?: RepeatType.OFF.name)
        }
        .distinctUntilChanged()

    suspend fun updateRepeatType(rpType: RepeatType) {
        Log.i(TAG, "Update Repeat Type -> $rpType")
        dataStore.edit { preferences -> preferences[REPEAT_TYPE] = rpType.name }
    }

    /******************
     * User Settings Preferences Section - Shuffle Type & Theme
     ******************/

    val userSettingsFlow: Flow<UserSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) }
            else throw exception
        }
        .map { preferences ->
            val shuffleType = ShuffleType.valueOf(preferences[SHUFFLE_TYPE] ?: ShuffleType.ONCE.name)
            val theme = preferences[THEME] ?: ThemeList[0]

            UserSettings(shuffleType, theme)
        }

    suspend fun getShuffleType(): ShuffleType =
        ShuffleType.valueOf(dataStore.data.first()[SHUFFLE_TYPE] ?: ShuffleType.ONCE.name)
    val getShuffleTypeFlow: Flow<ShuffleType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) }
            else throw exception
        }
        .map { preferences ->
            ShuffleType.valueOf(preferences[SHUFFLE_TYPE] ?: ShuffleType.ONCE.name)
        }
        .distinctUntilChanged()

    suspend fun updateShuffleType(shType: ShuffleType) {
        Log.i(TAG, "Update Shuffle Type -> $shType")
        dataStore.edit { preferences -> preferences[SHUFFLE_TYPE] = shType.name }
    }

    suspend fun getTheme(): String = dataStore.data.first()[THEME] ?: ThemeList[0]
    val getThemeFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) }
            else throw exception
        }
        .map { preferences -> preferences[THEME] ?: ThemeList[0] }
        .distinctUntilChanged()

    suspend fun updateTheme(theme: String) {
        Log.i(TAG, "Update Theme -> $theme")
        dataStore.edit { preferences -> preferences[THEME] = theme }
    }

    /******************
     * Library Sort Orders Section
     ******************/

    val librarySortOrdersFlow: Flow<LibrarySortOrders> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) }
            else throw exception
        }
        .map { preferences ->
            val albumSortColumn = preferences[ALBUM_SORT_COLUMN] ?: AlbumSortList[0]
            val artistSortColumn = preferences[ARTIST_SORT_COLUMN] ?: ArtistSortList[0]
            val composerSortColumn = preferences[COMPOSER_SORT_COLUMN] ?: ComposerSortList[0]
            val genreSortColumn = preferences[GENRE_SORT_COLUMN] ?: GenreSortList[0]
            val playlistSortColumn = preferences[PLAYLIST_SORT_COLUMN] ?: PlaylistSortList[0]
            val songSortColumn = preferences[SONG_SORT_COLUMN] ?: SongSortList[0]

            val isAlbumAsc = preferences[IS_ALBUM_ASC] ?: true
            val isArtistAsc = preferences[IS_ARTIST_ASC] ?: true
            val isComposerAsc = preferences[IS_COMPOSER_ASC] ?: true
            val isGenreAsc = preferences[IS_GENRE_ASC] ?: true
            val isPlaylistAsc = preferences[IS_PLAYLIST_ASC] ?: true
            val isSongAsc = preferences[IS_SONG_ASC] ?: true

            LibrarySortOrders(albumSortColumn, artistSortColumn, composerSortColumn,
                genreSortColumn, playlistSortColumn, songSortColumn, isAlbumAsc,
                isArtistAsc, isComposerAsc, isGenreAsc, isPlaylistAsc, isSongAsc)
        }

    suspend fun updateAlbumAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Album Asc/Desc -> $isAsc")
        dataStore.edit { preferences ->
            preferences[IS_ALBUM_ASC] = isAsc
        }
    }
    suspend fun updateAlbumSortColumn(sort: String) {
        Log.i(TAG, "Update Album Sort Column -> $sort")
        dataStore.edit { preferences ->
            preferences[ALBUM_SORT_COLUMN] = sort
        }
    }

    suspend fun updateArtistAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Artist Asc/Desc -> $isAsc")
        dataStore.edit { preferences ->
            preferences[IS_ARTIST_ASC] = isAsc
        }
    }
    suspend fun updateArtistSortColumn(sort: String) {
        Log.i(TAG, "Update Artist Sort Column -> $sort")
        dataStore.edit { preferences ->
            preferences[ARTIST_SORT_COLUMN] = sort
        }
    }

    suspend fun updateComposerAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Composer Asc/Desc -> $isAsc")
        dataStore.edit { preferences ->
            preferences[IS_COMPOSER_ASC] = isAsc
        }
    }
    suspend fun updateComposerSortColumn(sort: String) {
        Log.i(TAG, "Update Composer Sort Column -> $sort")
        dataStore.edit { preferences ->
            preferences[COMPOSER_SORT_COLUMN] = sort
        }
    }

    suspend fun updateGenreAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Genre Asc/Desc -> $isAsc")
        dataStore.edit { preferences ->
            preferences[IS_GENRE_ASC] = isAsc
        }
    }
    suspend fun updateGenreSortColumn(sort: String) {
        Log.i(TAG, "Update Genre Sort Column -> $sort")
        dataStore.edit { preferences ->
            preferences[GENRE_SORT_COLUMN] = sort
        }
    }

    suspend fun updatePlaylistAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Playlist Asc/Desc -> $isAsc")
        dataStore.edit { preferences ->
            preferences[IS_PLAYLIST_ASC] = isAsc
        }
    }
    suspend fun updatePlaylistSortColumn(sort: String) {
        Log.i(TAG, "Update Playlist Sort Column -> $sort")
        dataStore.edit { preferences ->
            preferences[PLAYLIST_SORT_COLUMN] = sort
        }
    }

    suspend fun updateSongAsc(isAsc: Boolean) {
        Log.i(TAG, "Update Song Asc/Desc -> $isAsc")
        dataStore.edit { preferences ->
            preferences[IS_SONG_ASC] = isAsc
        }
    }
    suspend fun updateSongSortColumn(sort: String) {
        Log.i(TAG, "Update Song Sort Column -> $sort")
        dataStore.edit { preferences ->
            preferences[SONG_SORT_COLUMN] = sort
        }
    }

    private companion object {
        val REPEAT_TYPE = stringPreferencesKey("repeat_type")
        val SHUFFLE_TYPE = stringPreferencesKey("shuffle_type")
        val THEME = stringPreferencesKey("theme")

        val ALBUM_SORT_COLUMN = stringPreferencesKey("album_sort_column")
        val ARTIST_SORT_COLUMN = stringPreferencesKey("artist_sort_column")
        val COMPOSER_SORT_COLUMN = stringPreferencesKey("composer_sort_column")
        val GENRE_SORT_COLUMN = stringPreferencesKey("genre_sort_column")
        val PLAYLIST_SORT_COLUMN = stringPreferencesKey("playlist_sort_column")
        val SONG_SORT_COLUMN = stringPreferencesKey("song_sort_column")

        val IS_ALBUM_ASC = booleanPreferencesKey("is_album_asc")
        val IS_ARTIST_ASC = booleanPreferencesKey("is_artist_asc")
        val IS_COMPOSER_ASC = booleanPreferencesKey("is_composer_asc")
        val IS_GENRE_ASC = booleanPreferencesKey("is_genre_asc")
        val IS_PLAYLIST_ASC = booleanPreferencesKey("is_playlist_asc")
        val IS_SONG_ASC = booleanPreferencesKey("is_song_asc")
    }
}