package com.example.music.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class ShuffleType {
    ONCE, ON_LOOP
}

enum class RepeatType {
    OFF, ONE, ON
}

enum class AlbumSortOrder {
    TITLE, ALBUM_ARTIST, DATE_LAST_PLAYED, SONG_COUNT
}

enum class ArtistSortOrder {
    NAME, ALBUM_COUNT, SONG_COUNT
}

enum class ComposerSortOrder {
    NAME, SONG_COUNT
}

enum class GenreSortOrder {
    NAME, SONG_COUNT//, ARTIST_COUNT, ALBUM_COUNT
}

enum class PlaylistSortOrder {
    NAME, DATE_CREATED, DATE_LAST_ACCESSED, DATE_LAST_PLAYED, SONG_COUNT
}

enum class SongSortOrder {
    TITLE, ARTIST, ALBUM, DATE_ADDED, LAST_PLAYED//, DURATION, FILE_SIZE
}

data class AppPreferences(
    val shuffleType: ShuffleType, // enum setting for shuffling once or reshuffle after queue repeat
    val repeatType: RepeatType, // enum setting for repeating queue'd song. if app queue ends after last song, if app queue continues after last song, if app song playing in queue is the new next song

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

private const val APP_PREFERENCES_NAME = "app_preferences"

private object PreferenceKeys {
    val SHUFFLE_TYPE = stringPreferencesKey("shuffle_type")
    val REPEAT_TYPE = stringPreferencesKey("repeat_type")
    val ALBUM_SORT_ORDER_KEY = stringPreferencesKey("album_sort_order")
    val ARTIST_SORT_ORDER_KEY = stringPreferencesKey("artist_sort_order")
    val COMPOSER_SORT_ORDER_KEY = stringPreferencesKey("composer_sort_order")
    val GENRE_SORT_ORDER_KEY = stringPreferencesKey("genre_sort_order")
    val PLAYLIST_SORT_ORDER_KEY = stringPreferencesKey("playlist_sort_order")
    val SONG_SORT_ORDER_KEY = stringPreferencesKey("song_sort_order")
    val IS_ALBUM_ASC_KEY = booleanPreferencesKey("is_album_asc")
    val IS_ARTIST_ASC_KEY = booleanPreferencesKey("is_artist_asc")
    val IS_COMPOSER_ASC_KEY = booleanPreferencesKey("is_composer_asc")
    val IS_GENRE_ASC_KEY = booleanPreferencesKey("is_genre_asc")
    val IS_PLAYLIST_ASC_KEY = booleanPreferencesKey("is_playlist_asc")
    val IS_SONG_ASC_KEY = booleanPreferencesKey("is_song_asc")
}

private val Context.dataStore by preferencesDataStore(name = APP_PREFERENCES_NAME)
//private val Context.appPreferencesStore by dataStore ()

//class AppPreferencesRepo private constructor(
class AppPreferencesRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    context: Context
) {

    val appPreferencesFlow: Flow<AppPreferences> = dataStore.data.map { preferences ->
        val shuffleType = ShuffleType.valueOf(preferences[PreferenceKeys.SHUFFLE_TYPE] ?: ShuffleType.ONCE.name)
        val repeatType = RepeatType.valueOf(preferences[PreferenceKeys.REPEAT_TYPE] ?: RepeatType.OFF.name)

        val albumSortOrder = AlbumSortOrder.valueOf(preferences[PreferenceKeys.ALBUM_SORT_ORDER_KEY] ?: AlbumSortOrder.ALBUM_ARTIST.name)
        val artistSortOrder = ArtistSortOrder.valueOf(preferences[PreferenceKeys.ARTIST_SORT_ORDER_KEY] ?: ArtistSortOrder.SONG_COUNT.name)
        val composerSortOrder = ComposerSortOrder.valueOf(preferences[PreferenceKeys.COMPOSER_SORT_ORDER_KEY] ?: ComposerSortOrder.SONG_COUNT.name)
        val genreSortOrder = GenreSortOrder.valueOf(preferences[PreferenceKeys.GENRE_SORT_ORDER_KEY] ?: GenreSortOrder.NAME.name)
        val playlistSortOrder = PlaylistSortOrder.valueOf(preferences[PreferenceKeys.PLAYLIST_SORT_ORDER_KEY] ?: PlaylistSortOrder.DATE_CREATED.name)
        val songSortOrder = SongSortOrder.valueOf(preferences[PreferenceKeys.SONG_SORT_ORDER_KEY] ?: SongSortOrder.ALBUM.name)

        val isAlbumAsc = preferences[PreferenceKeys.IS_ALBUM_ASC_KEY] ?: true
        val isArtistAsc = preferences[PreferenceKeys.IS_ARTIST_ASC_KEY] ?: true
        val isComposerAsc = preferences[PreferenceKeys.IS_COMPOSER_ASC_KEY] ?: true
        val isGenreAsc = preferences[PreferenceKeys.IS_GENRE_ASC_KEY] ?: false
        val isPlaylistAsc = preferences[PreferenceKeys.IS_PLAYLIST_ASC_KEY] ?: true
        val isSongAsc = preferences[PreferenceKeys.IS_SONG_ASC_KEY] ?: false

        AppPreferences(shuffleType, repeatType, albumSortOrder, artistSortOrder,
            composerSortOrder, genreSortOrder, playlistSortOrder, songSortOrder,
            isAlbumAsc, isArtistAsc, isComposerAsc, isGenreAsc, isPlaylistAsc, isSongAsc)
    }

    suspend fun updateShuffleType(shType: ShuffleType) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHUFFLE_TYPE] = shType.name
        }
    }

    suspend fun updateRepeatType(rpType: RepeatType) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.REPEAT_TYPE] = rpType.name
        }
    }

    suspend fun updateAlbumSortOrder(albSort: AlbumSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ALBUM_SORT_ORDER_KEY] = albSort.name
        }
    }

    suspend fun updateArtistSortOrder(artSort: ArtistSortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ARTIST_SORT_ORDER_KEY] = artSort.name
        }
    }


}