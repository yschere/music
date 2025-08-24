package com.example.music.data.mediaresolver

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.util.combine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "MediaRepo"

/**
 * Class that serves as a repository for the data layer to provide the ability to request and
 * retrieve data from the ContentResolver.
 */
class MediaRepo (
    @ApplicationContext context: Context
) {
    /***********************************************************************************************
     *
     * **********  MEDIA REPOSITORY BASE PROPERTIES AND METHODS SECTION ***********
     *
     **********************************************************************************************/

    /**
     * Object by which MediaRepo accesses media data
     */
    private val resolver: ContentResolver = context.contentResolver

    companion object {
        private val ALBUM_ART_URI: Uri = Uri.parse("content://media/external/audio/albumart")
        fun toAlbumArtUri(id: Long): Uri = ContentUris.withAppendedId(ALBUM_ART_URI, id)
    }

    /**
     * Registers an observer class for getting preference settings.
     * --Hope/want to find a way to repurpose this for getting data store user settings or at least
     * save-able data
     */
    fun observe(uri: Uri) =
        combine(
            flow = resolver.observe(uri),
            flow2 = resolver.observe(uri),
        ) { self, _ ->
            self
        }

    /**
     * Inspect the MediaStore to retrieve the total counts of songs, artists, albums and genres.
     */
    fun inspectMediaStore(): MutableList<Int> {
        val counts = mutableListOf<Int>()

        val songCursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (songCursor != null && songCursor.moveToFirst()) {
            counts.add(songCursor.count)
        }

        val artistCursor = resolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (artistCursor != null && artistCursor.moveToFirst()) {
            counts.add(artistCursor.count)
        }

        val albumCursor = resolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (albumCursor != null && albumCursor.moveToFirst()) {
            counts.add(albumCursor.count)
        }

        val genreCursor = resolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (genreCursor != null && genreCursor.moveToFirst()) {
            counts.add(genreCursor.count - 1) // this is to remove the 'null' row that genre counts
        }

        albumCursor?.close()
        artistCursor?.close()
        genreCursor?.close()
        songCursor?.close()

        return counts
    }

    /**
     * Returns the bitmap of a thumbnail from a given uri. This is an alternative to loading
     * album art by loading the thumbnail directly associated with an audio file.
     */
    fun loadThumbnail(uri: Uri): Bitmap = resolver.loadThumbnail(uri, Size(640, 480), null)


    /***********************************************************************************************
     *
     * **********  AUDIOS SECTION ***********
     *
     **********************************************************************************************/

    /**
     * Get ids of the most recently added songs from MediaStore
     * @return [Flow] of [List] of [Long]
     */
    fun mostRecentSongsIds(limit: Int) =
        observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map {
                resolver.queryExt(
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection = arrayOf(MediaStore.Audio.Media._ID),
                    order = MediaStore.Audio.AudioColumns.DATE_ADDED,
                    limit = limit,
                    ascending = false,
                ) { c ->
                    Array(c.count) {
                        c.moveToPosition(it)
                        c.getLong(0)
                    }
                }
            }

    /**
     * Get all audios
     * @return [List] of [Audio]
     */
    suspend fun getAllAudios(
        order: String,
        ascending: Boolean
    ): List<Audio> {
        Log.i(TAG, "Get All Audios")
        return resolver.getAudios(
            order = order,
            ascending = ascending
        )
    }

    /**
     * Get all audios
     * @return [Flow] of [List] of [Audio]
     */
    fun getAllAudiosFlow(
        order: String,
        ascending: Boolean
    ): Flow<List<Audio>> =
        observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map {
                Log.i(TAG, "Flow Get All Audios: observe MediaStore result: $it")
                resolver.getAudios(
                    order = order,
                    ascending = ascending
                )
            }

    /**
     * Get Audio based on id
     * @return [Audio] filtered to match id
     */
    suspend fun getAudio(id: Long) = resolver.findAudio(id)

    /**
     * Get Flow of Audio based on id
     * @return [Flow] of [Audio]
     */
    fun getAudioFlow(id: Long) =
        observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map {
                Log.i(TAG, "Flow Get Audio by ID: $id; observe MediaStore result: $it")
                resolver.findAudio(id)
            }

    /**
     * Get Audios based on list of ids
     * @return [List] of [Audio] filtered to match ids
     */
    suspend fun getAudios(ids: List<Long>) =
        ids.map { id ->
            Log.i(TAG, "Get Audios by ID: $id")
            resolver.findAudio(id)
        }

    /**
     * Get Flow of Audios based on list of ids
     * @return [Flow] of [List] on [Audio]
     */
    fun getAudiosFlow(ids: List<Long>) =
        observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map {
                ids.map { id ->
                    Log.i(TAG, "Flow Get Audios by ID: $id")
                    resolver.findAudio(id)
                }
            }

    /**
     * Search Audios, with query for user input
     */
    suspend fun findAudios(
        query: String? = null,
        order: String = MediaStore.Audio.Media.TITLE,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getAudios(query, order, ascending, offset, limit)


    /***********************************************************************************************
     *
     * **********  ARTISTS SECTION ***********
     *
     **********************************************************************************************/

    /**
     * Get all artists
     * @return [List] of [Artist]
     */
    suspend fun getAllArtists(
        order: String,
        ascending: Boolean
    ): List<Artist> {
        Log.i(TAG, "Get All Artists")
        return resolver.getArtists(
            order = order,
            ascending = ascending,
        )
    }

    /**
     * Get all artists
     * @return [Flow] of [List] of [Artist]
     */
    fun getAllArtistsFlow(
        order: String,
        ascending: Boolean
    ): Flow<List<Artist>> =
        observe(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI)
            .map {
                Log.i(TAG, "Flow Get All Artists: observe Media Store result: $it")
                resolver.getArtists(
                    order = order,
                    ascending = ascending,
                )
            }
    /**
     * Search for artist, with query for user input
     * @param query string for filtering based on user input
     * @return [List] of [Artist]
     */
    suspend fun findArtists(
        query: String? = null,
        order: String = MediaStore.Audio.Artists.ARTIST,
        ascending: Boolean = true,
        limit: Int = Integer.MAX_VALUE,
    ): List<Artist> = resolver.getArtists(
        sQuery = query,
        order = order,
        ascending = ascending,
        limit = limit
    )

    /**
     * Search for Artist based on id
     * @return [Artist]
     */
    suspend fun getArtist(
        id: Long
    ): Artist = resolver.findArtist(id)

    /**
     * Search for Artist based on id
     * @return [Flow] of [Artist]
     */
    fun getArtistFlow(
        artistId: Long
    ): Flow<Artist> = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        .map {
            Log.i(TAG, "Flow Get Artist by ID: $artistId")
            resolver.findArtist(artistId)
        }

    /**
     * Search for Artist based on an Album Id
     * @return [Artist]
     */
    suspend fun getArtistByAlbumId(
        albumId: Long
    ): Artist {
        Log.i(TAG, "Get Artist by Album ID: $albumId")
        val album = resolver.findAlbum(albumId)
        Log.i(TAG, "Get Artist by AlbumArtistId: ${album.artistId}")
        return resolver.findArtist(album.artistId)
    }

    /**
     * Search for Artist based on an Album Id
     * @return [Flow] of [Artist]
     */
    fun getArtistByAlbumIdFlow(
        albumId: Long
    ): Flow<Artist> = observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
        .map {
            Log.i(TAG, "Flow Get Artist by Album ID: $albumId")
            val album = resolver.findAlbum(albumId)
            Log.i(TAG, "Flow Get Artist by AlbumArtistId: ${album.artistId}")
            resolver.findArtist(album.artistId)
        }

    /**
     * Search for Albums by an Artist based on an Artist Id
     * @return [Flow] of [List] of [Album]
     */
    fun getAlbumsByArtistId(
        artistId: Long,
        sortOrder: String = MediaStore.Audio.Albums.ALBUM,
        ascending: Boolean = true,
    ): Flow<List<Album>> = observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
        .map {
            resolver.queryExt(
                uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ID),
                selection = "${MediaStore.Audio.Albums.ARTIST_ID} == ?",
                args = arrayOf("$artistId"),
                order = sortOrder,
                ascending = ascending,
                transform = { c ->
                    Array(c.count) {
                        c.moveToPosition(it)
                        c.getLong(0)
                    }.map { id ->
                        getAlbum(id)
                    }
                }
            )
        }

    /**
     * Get Audios by Artist name, with query for user input
     * @return [List] of [Audio]
     */
    suspend fun getArtistAudios(
        name: String,
        query: String? = null,
        order: String = MediaStore.Audio.Media.TITLE,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getArtistAudios(name, query, order, ascending, offset, limit)

    /**
     * Get Audios by Artist id
     * @return [List] of [Audio]
     */
    suspend fun getArtistAudios(
        artistId: Long,
        order: String = MediaStore.Audio.Media.TITLE,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getArtistAudiosById(artistId, order, ascending, offset, limit)


    /***********************************************************************************************
     *
     * **********  ALBUMS SECTION ***********
     *
     **********************************************************************************************/

    /**
     * returns the ids of the most recently released albums
     * @return [Flow] of [List] of [Long]
     */
    fun mostRecentAlbumsIds(limit: Int) =
        observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
            .map {
                resolver.queryExt(
                    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ID),
                    order = MediaStore.Audio.Albums.LAST_YEAR,
                    limit = limit,
                    ascending = false,
                    transform = { c ->
                        Array(c.count) {
                            c.moveToPosition(it)
                            c.getLong(0)
                        }
                    }
                )
            }

    /**
     * get all albums
     * @return [List] of [Album]
     */
    suspend fun getAllAlbums( order: String, ascending: Boolean ) =
        findAlbums(
            order = order,
            ascending = ascending,
        )

    /**
     * get all albums
     * @return [Flow] of [List] of [Album]
     */
    fun getAllAlbumsFlow( order: String, ascending: Boolean ) =
        observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
            .map {
                findAlbums(
                    order = order,
                    ascending = ascending,
                )
            }

    /**
     * Search for albums, with query for user input
     * @param query string for filtering based on user input
     * @return [List] of [Album]
     */
    suspend fun findAlbums(
        query: String? = null,
        order: String = MediaStore.Audio.Albums.ALBUM,
        ascending: Boolean = true,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getAlbums(
        sQuery = query,
        order = order,
        ascending = ascending,
        limit = limit
    )

    /**
     * Search for Album based on id
     * @return [Album]
     */
    suspend fun getAlbum(id: Long) = resolver.findAlbum(id)

    /**
     * Search for Album based on id
     * @return [Flow] of [Album]
     */
    fun getAlbumFlow(id: Long) =
        observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
            .map {
                Log.i(TAG, "Flow Get Album by ID: $id")
                resolver.findAlbum(id)
            }

    /**
     * Get Audios based on Album title
     * @return [List] of [Audio]
     */
    suspend fun getAlbumAudios(
        title: String,
        query: String? = null,
        order: String = MediaStore.Audio.Media.TRACK,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getAlbumAudios(title, query, order, ascending, offset, limit)

    /**
     * Get Audios based on Album id
     * @return [List] of [Audio]
     */
    suspend fun getAlbumAudios(
        albumId: Long,
        order: String = MediaStore.Audio.Media.TRACK,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getAlbumAudiosById(albumId, order, ascending, offset, limit)


    /***********************************************************************************************
     *
     * **********  GENRES SECTION ***********
     *
     **********************************************************************************************/

    /**
     * get all genres
     * @return [List] of [Genre]
     */
    suspend fun getAllGenres(order: String, ascending: Boolean) =
        findGenres(
            order = order,
            ascending = ascending
        )

    /**
     * get all genres
     * @return [Flow] of [List] of [Genre]
     */
    fun getAllGenresFlow(order: String, ascending: Boolean) =
        observe(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI)
            .map {
                findGenres(
                    order = order,
                    ascending = ascending
                )
            }

    /**
     * Search for Genre based on id
     * @return [Genre]
     */
    suspend fun getGenre(id: Long) = resolver.findGenre(id)

    /**
     * Search for Genre based on id
     * @return [Flow] of [Genre]
     */
    fun getGenreFlow(id: Long) =
        observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map {
                Log.i(TAG, "Flow Get Genre by ID: $id")
                resolver.findGenre(id)
            }

    /**
     * Get Genres via resolver
     * @return [List] of [Genre]
     */
    private suspend fun findGenres(
        query: String? = null,
        order: String = MediaStore.Audio.Genres.NAME,
        ascending: Boolean = true,
    ) = resolver.getGenres(query, order, ascending)

    /**
     * Get Audios based on Genre name
     * @return [List] of [Audio]
     */
    suspend fun getGenreAudios(
        name: String,
        query: String? = null,
        order: String = MediaStore.Audio.Media.TITLE,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getGenreAudios(name, query, order, ascending, offset, limit)

    /**
     * Get Audios based on Genre id
     * @return [List] of [Audio]
     */
    suspend fun getGenreAudios(
        id: Long,
        order: String = MediaStore.Audio.Media.TITLE,
        ascending: Boolean = true,
        offset: Int = 0,
        limit: Int = Integer.MAX_VALUE,
    ) = resolver.getGenreAudiosById(id, order, ascending, offset, limit)

    //would likely get playlists here as well
    // and be able to get playlist's songs here too, ie "playlist members" or "playlist tracks"
    //rest of the playlist functions may as well be dbRepo functions that should be encapsulated elsewhere before coming here

    /* // insert new track into playlist, still don't get how it specifies a playlist ... or is it just calling the playlist db that?
    // FUCKING IS THAT WHAT IT IS??
    suspend fun insert(track: Track): Boolean {
        val playlistsDb = playlistz
        // if the item is already in playlist return false;
        // because we don't support same uri's in single playlist
        if (exists(track.playlistID, value.uri))
            return false
        val order = playlistsDb.lastPlayOrder(track.playlistID) ?: -1
        // ensure that order is coerced in limit.
        val member =
            if (track.order < 0 || track.order > order + 1)
                track.copy(order = track.order.coerceIn(0, order + 1))
            else
                track
        val success = playlistsDb.insert(member = member) != -1L
        if (success) {
            // update the modified time of the playlist.
            // here this should not be null
            // but we should play safe
            val old = playlistsDb.get(track.playlistID) ?: return true
            update(old.clone())
        }
        return success
    }*/

    /* // update track within Playlist, return true if update successful, return false otherwise
    suspend fun update(track: Track): Boolean {
        val playlistsDb = playlistz
        // if the item is not in playlist return false
        // Question: what happens if the user has changed/updated the uri of the item?
        if (!exists(track.playlistID, track.uri))
            return false
        val order = playlistsDb.lastPlayOrder(track.playlistID) ?: -1
        // ensure that order is coerced in limit.
        val member =
            if (track.order < 0 || track.order > order + 1)
                track.copy(order = track.order.coerceIn(0, order + 1))
            else
                track
        val success = playlistsDb.update(member = member) != -1L
        if (success) {
            // update the modified time of the playlist.
            // here this should not be null
            // but we should play safe
            val old = playlistsDb.get(track.playlistID) ?: return true
            update(old.clone())
        }
        return success
    }*/

    /* // permanently deletes specified URIs from device's content
    suspend fun delete(activity: Activity, vararg uri: Uri, trash: Boolean = true): Int {
        return withContext(Dispatchers.IO) {
            val result = runCatching {
                // if less than R simply delete the items.
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R)
                    return@runCatching resolver.delete(*uri)
                if (activity is ComponentActivity)
                    return@runCatching if (trash) resolver.trash(
                        activity,
                        *uri
                    ) else resolver.delete(activity, *uri)
                return@runCatching if (trash) resolver.trash(activity, *uri) else resolver.trash(
                    activity,
                    *uri
                )
            }
            // return -1 if failure.
            result.getOrElse { -1 }
        }
    }*/
}