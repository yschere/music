package com.example.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class RepeatType {
    OFF, ONE, ON
}

enum class ShuffleType {
    ONCE, ON_LOOP
}
// TODO do these need to be renamed to match MS column names?
enum class AlbumSortOrder {
    TITLE, ALBUM_ARTIST, DATE_LAST_PLAYED, SONG_COUNT //maybe not date last played, could make it year if those columns FUCKING WORKED
}
// TODO do these need to be renamed to match MS column names?
enum class ArtistSortOrder {
    NAME, ALBUM_COUNT, SONG_COUNT
}

enum class ComposerSortOrder {
    NAME, SONG_COUNT
}
// TODO do these need to be renamed to match MS column names?
enum class GenreSortOrder {
    NAME, SONG_COUNT
}

enum class PlaylistSortOrder {
    NAME, DATE_CREATED, DATE_LAST_ACCESSED, DATE_LAST_PLAYED, SONG_COUNT
}
// TODO do these need to be renamed to match MS column names?
enum class SongSortOrder {
    TITLE, ARTIST, ALBUM, DATE_ADDED, LAST_PLAYED//, DURATION, FILE_SIZE
}

data class AppPreferences(
    val repeatType: RepeatType, // enum setting for repeating queue'd song. if app queue ends after last song, if app queue continues after last song, if app song playing in queue is the new next song
    val shuffleType: ShuffleType, // enum setting for shuffling once or reshuffle after queue repeat

    val albumSortOrder: AlbumSortOrder, // enum setting for the album attributes to sort list on
    val artistSortOrder: ArtistSortOrder, // enum setting for the artist attributes to sort list on
    val composerSortOrder: ComposerSortOrder, // enum setting for the composer attributes to sort list on
    val genreSortOrder: GenreSortOrder, // enum setting for the genre attributes to sort list on
    val playlistSortOrder: PlaylistSortOrder, // enum setting for the playlist attributes to sort list on
    val songSortOrder: SongSortOrder, // enum setting for the song attributes to sort list on

    val isAlbumAsc: Boolean, // if album list is in ascending or descending order
    val isArtistAsc: Boolean, // if artist list is in ascending or descending order
    val isComposerAsc: Boolean, // if composer list is in ascending or descending order
    val isGenreAsc: Boolean, // if genre list is in ascending or descending order
    val isPlaylistAsc: Boolean, // if playlist list is in ascending or descending order
    val isSongAsc: Boolean, // if song list is in ascending or descending order
)

class AppPreferencesRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferenceKeys {
        val REPEAT_TYPE = stringPreferencesKey("repeat_type")
        val SHUFFLE_TYPE = stringPreferencesKey("shuffle_type")

        val ALBUM_SORT_ORDER = stringPreferencesKey("album_sort_order")
        val ARTIST_SORT_ORDER = stringPreferencesKey("artist_sort_order")
        val COMPOSER_SORT_ORDER = stringPreferencesKey("composer_sort_order")
        val GENRE_SORT_ORDER = stringPreferencesKey("genre_sort_order")
        val PLAYLIST_SORT_ORDER = stringPreferencesKey("playlist_sort_order")
        val SONG_SORT_ORDER = stringPreferencesKey("song_sort_order")

        val IS_ALBUM_ASC = booleanPreferencesKey("is_album_asc")
        val IS_ARTIST_ASC = booleanPreferencesKey("is_artist_asc")
        val IS_COMPOSER_ASC = booleanPreferencesKey("is_composer_asc")
        val IS_GENRE_ASC = booleanPreferencesKey("is_genre_asc")
        val IS_PLAYLIST_ASC = booleanPreferencesKey("is_playlist_asc")
        val IS_SONG_ASC = booleanPreferencesKey("is_song_asc")
    }

    val appPreferencesFlow: Flow<AppPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val repeatType = RepeatType.valueOf(preferences[PreferenceKeys.REPEAT_TYPE] ?: RepeatType.OFF.name)
            val shuffleType = ShuffleType.valueOf(preferences[PreferenceKeys.SHUFFLE_TYPE] ?: ShuffleType.ONCE.name)

            val albumSortOrder = AlbumSortOrder.valueOf(preferences[PreferenceKeys.ALBUM_SORT_ORDER] ?: AlbumSortOrder.TITLE.name)
            val artistSortOrder = ArtistSortOrder.valueOf(preferences[PreferenceKeys.ARTIST_SORT_ORDER] ?: ArtistSortOrder.NAME.name)
            val composerSortOrder = ComposerSortOrder.valueOf(preferences[PreferenceKeys.COMPOSER_SORT_ORDER] ?: ComposerSortOrder.NAME.name)
            val genreSortOrder = GenreSortOrder.valueOf(preferences[PreferenceKeys.GENRE_SORT_ORDER] ?: GenreSortOrder.NAME.name)
            val playlistSortOrder = PlaylistSortOrder.valueOf(preferences[PreferenceKeys.PLAYLIST_SORT_ORDER] ?: PlaylistSortOrder.NAME.name)
            val songSortOrder = SongSortOrder.valueOf(preferences[PreferenceKeys.SONG_SORT_ORDER] ?: SongSortOrder.TITLE.name)

            val isAlbumAsc = preferences[PreferenceKeys.IS_ALBUM_ASC] ?: true
            val isArtistAsc = preferences[PreferenceKeys.IS_ARTIST_ASC] ?: true
            val isComposerAsc = preferences[PreferenceKeys.IS_COMPOSER_ASC] ?: true
            val isGenreAsc = preferences[PreferenceKeys.IS_GENRE_ASC] ?: false
            val isPlaylistAsc = preferences[PreferenceKeys.IS_PLAYLIST_ASC] ?: true
            val isSongAsc = preferences[PreferenceKeys.IS_SONG_ASC] ?: false

            AppPreferences(repeatType, shuffleType, albumSortOrder, artistSortOrder,
                composerSortOrder, genreSortOrder, playlistSortOrder, songSortOrder,
                isAlbumAsc, isArtistAsc, isComposerAsc, isGenreAsc, isPlaylistAsc, isSongAsc)
        }

    suspend fun updateRepeatType(rpType: RepeatType) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.REPEAT_TYPE] = rpType.name
        }
    }

    suspend fun updateShuffleType(shType: ShuffleType) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHUFFLE_TYPE] = shType.name
        }
    }

    suspend fun updateAlbumSortOrder(albSort: AlbumSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ALBUM_SORT_ORDER] = albSort.name
        }
    }

    suspend fun updateArtistSortOrder(artSort: ArtistSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ARTIST_SORT_ORDER] = artSort.name
        }
    }

    suspend fun updateComposerSortOrder(cmpSort: ComposerSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.COMPOSER_SORT_ORDER] = cmpSort.name
        }
    }

    suspend fun updateGenreSortOrder(genSort: GenreSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.GENRE_SORT_ORDER] = genSort.name
        }
    }

    suspend fun updatePlaylistSortOrder(plySort: PlaylistSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.PLAYLIST_SORT_ORDER] = plySort.name
        }
    }

    suspend fun updateSongSortOrder(sngSort: SongSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SONG_SORT_ORDER] = sngSort.name
        }
    }

    suspend fun updateAlbumAsc(isAsc: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_ALBUM_ASC] = isAsc
        }
    }

    suspend fun updateArtistAsc(isAsc: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_ARTIST_ASC] = isAsc
        }
    }

    suspend fun updateGenreAsc(isAsc: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_GENRE_ASC] = isAsc
        }
    }
}