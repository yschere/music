package com.example.music.data.mediaresolver

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.mediaresolver.model.Playlist
import com.example.music.data.mediaresolver.model.PlaylistTrack
import com.example.music.data.mediaresolver.model.toAlbum
import com.example.music.data.mediaresolver.model.toArtist
import com.example.music.data.mediaresolver.model.toAudio
import com.example.music.data.mediaresolver.model.toGenre
import com.example.music.data.mediaresolver.model.toPlaylist
import com.example.music.data.mediaresolver.model.toPlaylistTrack
import com.example.music.data.util.FLAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

private const val TAG = "Resolver"
private const val DEFAULT_MEDIA_SELECTION = "${MediaStore.Audio.Media._ID} != 0"


/***********************************************************************************************
 *
 * **********  CONTENT RESOLVER BASE FUNCTIONS SECTION ***********
 *
 **********************************************************************************************/

/**
 * Extension on Content Resolver's query to more easily specify parameters for queries
 * to MediaStore to retrieve Cursor containing query result.
 */
@SuppressLint("Recycle")
suspend fun ContentResolver.queryExt(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String = DEFAULT_MEDIA_SELECTION,
    args: Array<String>? = null,
    order: String = MediaStore.MediaColumns._ID,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): Cursor {
    return withContext(Dispatchers.Default) {
        // Use the modern query approach for devices running Android 10 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Compose the arguments for the query
            val args2 = Bundle().apply {
                // Set the limit and offset for pagination
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)

                // Set the sort order
                putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(order))
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    if (ascending)
                        ContentResolver.QUERY_SORT_DIRECTION_ASCENDING
                    else
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )

                // Set the selection arguments and selection string
                if (args != null)
                    putStringArray(
                        ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                        args
                    )
                // Add the selection string.
                // Consider adding support for group by.
                // Currently, using group by on Android 10results in errors,
                // and the argument for group by is only supported on Android 11 and above.
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)

            }
            query(uri, projection, args2, null)
        }
        //Fallback to the traditional query approach for devices running older Android versions
        else {
            // Construct the ORDER BY clause with limit and offset
            //language=SQL
            val order2 =
                order + (if (ascending) " ASC" else " DESC") + " LIMIT $limit OFFSET $offset"
            // Perform the query with the traditional approach
            query(uri, projection, selection, args, order2)
        }
    } ?: throw NullPointerException("Can't retrieve cursor for $uri")
}

/**
 * Transformer for extension query to Content Resolver's query from Cursor to type T.
 * Calls queryExt(), then executes the lambda expression from transform to return the query result
 * as type T. Used to transform Cursor to the data models defined in mediaresolver/model.
 */
internal suspend inline fun <T> ContentResolver.queryExt(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String = DEFAULT_MEDIA_SELECTION,
    args: Array<String>? = null,
    order: String = MediaStore.MediaColumns._ID,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE,
    transform: (Cursor) -> T
): T = queryExt(uri, projection, selection, args, order, ascending, offset, limit).use(transform)

/**
 * Register [ContentObserver] for change in [uri]
 */
inline fun ContentResolver.register(
    uri: Uri,
    crossinline onChanged: () -> Unit
): ContentObserver {
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            onChanged()
        }
    }
    registerContentObserver(uri, false, observer)
    return observer
}

/**
 * Register an observer class that gets callbacks when data identified by a given content URI
 * changes.
 */
fun ContentResolver.observe(uri: Uri) = callbackFlow {
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            trySend(selfChange)
        }
    }
    registerContentObserver(uri, true, observer)
    // trigger first.
    trySend(false)
    awaitClose {
        unregisterContentObserver(observer)
    }
}


/***********************************************************************************************
 *
 * **********  AUDIOS SECTION ***********
 *
 **********************************************************************************************/

/**
 * Projection of Audio model in MediaStore columns
 */
private val AUDIO_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Media._ID, // 0
        MediaStore.Audio.Media.TITLE, // 1
        MediaStore.Audio.Media.MIME_TYPE, // 2
        MediaStore.Audio.Media.DATA, // 3
        MediaStore.Audio.Media.DATE_ADDED, // 4
        MediaStore.Audio.Media.DATE_MODIFIED, // 5
        MediaStore.Audio.Media.SIZE, // 6

        MediaStore.Audio.AudioColumns.ARTIST, // 7
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 8
        MediaStore.Audio.AudioColumns.ALBUM, // 9
        MediaStore.Audio.AudioColumns.ALBUM_ID, // 10
        MediaStore.Audio.AudioColumns.ALBUM_ARTIST, // 11
        MediaStore.Audio.AudioColumns.COMPOSER, // 12
        MediaStore.Audio.AudioColumns.GENRE, // 13
        MediaStore.Audio.AudioColumns.GENRE_ID, // 14
        MediaStore.Audio.AudioColumns.YEAR, // 15

        MediaStore.Audio.AudioColumns.DURATION, // 16
        MediaStore.Audio.AudioColumns.BITRATE, // 17
        MediaStore.Audio.AudioColumns.TRACK, // 18
        MediaStore.MediaColumns.CD_TRACK_NUMBER, // 19
        MediaStore.Audio.AudioColumns.DISC_NUMBER, // 20
    )

/**
 * Default query select column for media store audio table
 */
private const val DEFAULT_AUDIO_SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

/**
 * Default query order column for media store audio table
 */
private const val DEFAULT_AUDIO_ORDER = MediaStore.Audio.Media.TITLE

/**
 * @return list of [Audio] from the [MediaStore].
 */
suspend fun ContentResolver.findAudios(
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio>? = queryExt(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = AUDIO_PROJECTION,
    selection = DEFAULT_AUDIO_SELECTION +
        if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        else "",
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val s = when (sQuery) {
            null -> "-no filter given-"
            else -> "on '$sQuery'"
        }
        if (FLAG) Log.i(TAG, "Find Audios Search $s:\n" +
            "Audio(s) count returned: ${c.count}")
        if (c.count == 0) return null

        val result = List(c.count) {
            c.moveToPosition(it)
            c.toAudio()
        }
        c.close()
        result
    },
)

/**
 * @return list of [Audio] from given bucket/directory
 */
private suspend inline fun ContentResolver.findBucketAudios(
    selection: String,
    args: Array<String>? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio>? = queryExt(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = AUDIO_PROJECTION,
    selection = selection,
    args = args,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        if (FLAG) Log.i(TAG, "Find Bucket Audios Search on selection $selection:\n" +
            "Audio(s) count returned: ${c.count}")
        if (c.count == 0) return null

        val result = List(c.count) {
            c.moveToPosition(it)
            c.toAudio()
        }
        c.close()
        result
    }
)

/**
 * Search for [Audio] on [MediaStore.Audio.Media._ID]
 * @param id audio id
 * @return [Cursor] transformed to [Audio]
 */
suspend fun ContentResolver.getAudio(
    id: Long
): Audio = queryExt(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = AUDIO_PROJECTION,
    selection = "${MediaStore.Audio.Media._ID} == ?",
    args = arrayOf("$id"),
    transform = { c ->
        c.moveToFirst()
        val result = c.toAudio()
        c.close()
        if (FLAG) Log.i(TAG, "Get Audio via audioId $id - AUDIO DATA:\n" +
            "ID: ${result.id}\n" +
            "Title: ${result.title}\n" +
            "Artist: ${result.artist}\n" +
            "Album: ${result.album}\n" +
            "Genre: ${result.genre}\n" +
            "Date Added: ${result.dateAdded}\n" +
            "Date Modified: ${result.dateModified}\n" +
            "CD TrackNumber: ${result.cdTrackNumber}\n" +
            "Disc Number: ${result.discNumber}\n" +
            "File Path: ${result.path}\n" +
            "File Size: ${result.size}\n" +
            "Year: ${result.year}"
        )
        result
    },
)

/**
 * Search for [Audio] on [MediaStore.Audio.Media.DATA], limit 1
 * @param path audio file path
 * @return [Cursor] transformed to [Audio]
 */
suspend fun ContentResolver.getAudio(
    path: String
): Audio = queryExt(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = AUDIO_PROJECTION,
    selection = "${MediaStore.Audio.Media.DATA} == ?",
    args = arrayOf(path),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toAudio()
        c.close()
        if (FLAG) Log.i(TAG, "Get Audio via filepath $path - AUDIO DATA:\n" +
            "ID: ${result.id}\n" +
            "Title: ${result.title}\n" +
            "Artist: ${result.artist}\n" +
            "Album: ${result.album}\n" +
            "Genre: ${result.genre}\n" +
            "Date Added: ${result.dateAdded}\n" +
            "Date Modified: ${result.dateModified}\n" +
            "CD TrackNumber: ${result.cdTrackNumber}\n" +
            "Disc Number: ${result.discNumber}\n" +
            "File Path: ${result.path}\n" +
            "File Size: ${result.size}\n" +
            "Year: ${result.year}"
        )
        result
    },
)

/**
 * Search for [Audio] on Audio.uri, limit 1
 * @param uri audio uri
 * @return [Cursor] transformed to [Audio]
 */
suspend fun ContentResolver.getAudio(
    uri: Uri
): Audio = queryExt(
    uri = uri,
    projection = AUDIO_PROJECTION,
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toAudio()
        c.close()
        if (FLAG) Log.i(TAG, "Get Audio via Uri $uri - AUDIO DATA:\n" +
            "ID: ${result.id}\n" +
            "Title: ${result.title}\n" +
            "Artist: ${result.artist}\n" +
            "Album: ${result.album}\n" +
            "Genre: ${result.genre}\n" +
            "Date Added: ${result.dateAdded}\n" +
            "Date Modified: ${result.dateModified}\n" +
            "CD TrackNumber: ${result.cdTrackNumber?:" null "}\n" +
            "Disc Number: ${result.discNumber?:" null "}\n" +
            "File Path: ${result.path}\n" +
            "File Size: ${result.size}\n" +
            "Year: ${result.year}"
        )
        result
    },
)


/***********************************************************************************************
 *
 * **********  ARTISTS SECTION ***********
 *
 **********************************************************************************************/

/**
 * Projection of Artist data class in MediaRetriever columns
 */
private val ARTIST_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Artists._ID,
        MediaStore.Audio.Artists.ARTIST,
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
    )

/**
 * Default query order column for media store artist table
 */
private const val DEFAULT_ARTIST_ORDER = MediaStore.Audio.Artists.ARTIST

/**
 * @return list of [Artist] from the [MediaStore].
 */
suspend fun ContentResolver.findArtists(
    sQuery: String? = null,
    order: String = DEFAULT_ARTIST_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Artist>? = queryExt(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection =
        if (sQuery != null) "${MediaStore.Audio.Artists.ARTIST} LIKE ?"
        else DEFAULT_MEDIA_SELECTION,
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val s = when (sQuery) {
            null -> "-no filter given-"
            else -> "on '$sQuery'"
        }
        if (FLAG) Log.i(TAG, "Get Artists Search $s:\n" +
            "Artist(s) count returned: ${c.count}")
        if (c.count == 0) return null

        val result = List(c.count) {
            c.moveToPosition(it)
            c.toArtist()
        }
        c.close()
        result
    },
)

/**
 * @param name artist's name
 * @param sQuery audio title query string
 * @return list of [Audio] based on Artist name
 */
suspend fun ContentResolver.findArtistAudios(
    name: String,
    sQuery: String? = null,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio>? {
    val like =
        if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        else ""
    val selection = "${MediaStore.Audio.Media.ARTIST} == ?" + like
    val args =
        if (sQuery != null) arrayOf(name, "%$sQuery%")
        else arrayOf(name)
    if (FLAG) Log.i(TAG, "Get Artist Audios - selection: $selection + args: $args")
    return findBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * @return list of [Audio] based on Artist Id
 */
suspend fun ContentResolver.getArtistAudiosById(
    artistId: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE,
): List<Audio> {
    val selection = "${MediaStore.Audio.Media.ARTIST_ID} == ?"
    val args = arrayOf("$artistId")
    if (FLAG) Log.i(TAG, "Get Artist Audios via ID - selection: $selection + args: $args")
    return findBucketAudios(selection, args, order, ascending, offset, limit) ?: emptyList()
}

/**
 * @return list of [Album] based on Artist Id
 */
suspend fun ContentResolver.findArtistAlbums(
    artistId: Long,
    order: String = MediaStore.Audio.Albums.ALBUM,
    ascending: Boolean = true,
): List<Album>? = queryExt(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ID),
    selection = "${MediaStore.Audio.Albums.ARTIST_ID} == ?",
    args = arrayOf("$artistId"),
    order = order,
    ascending = ascending,
    transform = { c ->
        if (c.count == 0) return null
        val result = Array(c.count) {
            c.moveToPosition(it)
            c.getLong(0)
        }.map { id ->
            getAlbum(id)
        }
        c.close()
        result
    }
)

/**
 * Search for [Artist] on name, limit 1
 * @param name
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtist(
    name: String?
): Artist? {
    if (name == null) return null
    return queryExt(
        uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        projection = ARTIST_PROJECTION,
        selection = "${MediaStore.Audio.Artists.ARTIST} == ?",
        args = arrayOf(name),
        limit = 1,
        transform = { c ->
            if (!c.moveToFirst()) return null
            val result = c.toArtist()
            c.close()
            if (FLAG) Log.i(
                TAG, "Find Artist Search via name $name - ARTIST DATA: \n" +
                        "ID: ${result.id} \n" +
                        "Name: ${result.name} \n" +
                        "Album count: ${result.numAlbums} \n" +
                        "Track count: ${result.numTracks}"
            )
            result
        },
    )
}

/**
 * Search for [Artist] on _id, limit 1
 * @param id
 * @return [Cursor] transformed to [Artist]
 */
suspend fun ContentResolver.getArtist(
    id: Long
): Artist = queryExt(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection = "${MediaStore.Audio.Artists._ID} == ?",
    args = arrayOf("$id"),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toArtist()
        c.close()
        if (FLAG) Log.i(TAG, "Find Artist Search via artistId $id - ARTIST DATA: \n" +
            "ID: ${result.id} \n" +
            "Name: ${result.name} \n" +
            "Album count: ${result.numAlbums} \n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)

/**
 * Trying to find artist info based on album Id, limit 1
 * @param albumId
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtistByAlbumId(
    albumId: Long
): Artist? = queryExt(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection = "${MediaStore.Audio.Albums.ALBUM_ID} == ?",
    args = arrayOf("$albumId"),
    limit = 1,
    transform = { c ->
        if (!c.moveToFirst()) return null
        val result = c.toArtist()
        c.close()
        if (FLAG) Log.i(TAG, "Find Artist Search via albumId $albumId - ARTIST DATA: \n" +
            "ID: ${result.id}\n" +
            "Name: ${result.name}\n" +
            "Album count: ${result.numAlbums}\n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)


/***********************************************************************************************
 *
 * **********  ALBUMS SECTION ***********
 *
 **********************************************************************************************/

/**
 * Projection of Album data class in MediaStore columns
 */
private val ALBUM_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.ARTIST_ID,
        MediaStore.Audio.Albums.LAST_YEAR,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    )

/**
 * Default query order column for media store album table
 */
private const val DEFAULT_ALBUM_ORDER = MediaStore.Audio.Albums.ALBUM

/**
 * @return list of [Album] from the [MediaStore].
 */
suspend fun ContentResolver.findAlbums(
    sQuery: String? = null,
    order: String = DEFAULT_ALBUM_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Album>? = queryExt(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = ALBUM_PROJECTION,
    selection =
        if (sQuery != null) "${MediaStore.Audio.Albums.ALBUM} LIKE ?"
        else DEFAULT_MEDIA_SELECTION,
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val s = when (sQuery) {
            null -> "-no filter given-"
            else -> "on '$sQuery'"
        }
        if (FLAG) Log.i(TAG, "Get Albums Search $s:\n" +
            "Album(s) count returned: ${c.count}")
        if (c.count == 0) return null

        val result = List(c.count) {
            c.moveToPosition(it)
            var album = c.toAlbum()
            val firstTrackAlbumArtist = findFirstTrackAlbumArtist(album.id)
            if (album.artist != firstTrackAlbumArtist) {
                val artist = findArtist(firstTrackAlbumArtist)
                album = album.copy(
                    artist = artist?.name ?: album.artist ?: "null",
                    artistId = artist?.id ?: album.artistId ?: 0L
                )
            }
            album
        }
        c.close()
        result
    },
)

/**
 * @return list of [Audio] based on Album
 */
suspend fun ContentResolver.findAlbumAudios(
    title: String,
    sQuery: String? = null,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio>? {
    val like =
        if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        else ""
    val selection = "${MediaStore.Audio.Media.ALBUM} == ?" + like
    val args =
        if (sQuery != null) arrayOf(title, "%$sQuery%")
        else arrayOf(title)
    if (FLAG) Log.i(TAG, "Get Album Audios - selection: $selection + args $args")
    return findBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * @return list of [Audio] based on Album Id
 */
suspend fun ContentResolver.getAlbumAudiosById(
    albumId: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val selection = "${MediaStore.Audio.Media.ALBUM_ID} == ?"
    val args = arrayOf("$albumId")
    if (FLAG) Log.i(TAG, "Get Album Audios By ID - selection: $selection + args: $args")
    return findBucketAudios(selection, args, order, ascending, offset, limit) ?: emptyList()
}

/**
 * @return album artist from the first track of an album based on album's id
 */
suspend fun ContentResolver.findFirstTrackAlbumArtist(
    albumId: Long
): String? = queryExt(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.ALBUM_ARTIST,
    ),
    selection = "${MediaStore.Audio.Media.ALBUM_ID} == ?",
    args = arrayOf("$albumId"),
    order = MediaStore.Audio.Media.TRACK,
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.getStringOrNull(2)
        c.close()
        if (FLAG) Log.i(TAG, "From Album $albumId -> Get First Track Album Artist: $result")
        result
    }
)

/**
 * Search for [Album] on [MediaStore.Audio.Media._ID], limit 1
 * @param id
 * @return [Cursor] transformed to [Album]
 */
suspend fun ContentResolver.getAlbum(
    id: Long
): Album = queryExt(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = ALBUM_PROJECTION,
    selection = "${MediaStore.Audio.Albums.ALBUM_ID} == ?",
    args = arrayOf("$id"),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        var result = c.toAlbum()
        c.close()
        val firstTrackAlbumArtist = findFirstTrackAlbumArtist(result.id)
        if (result.artist != firstTrackAlbumArtist) {
            val artist = findArtist(firstTrackAlbumArtist)
            result = result.copy(
                artist = artist?.name ?: result.artist ?: "null",
                artistId = artist?.id ?: result.artistId ?: 0L
            )
        }
        if (FLAG) Log.i(TAG, "Get Album via albumId $id - ALBUM DATA: \n" +
            "ID: ${result.id} \n" +
            "Title: ${result.title} \n" +
            "ArtistId: ${result.artistId} \n" +
            "Artist Name: ${result.artist} \n" +
            "Year: ${result.lastYear} \n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)

/**
 * Search for [Album] on [MediaStore.Audio.Albums.ALBUM], limit 1
 * @param title
 * @return [Cursor] transformed to [Album]
 */
suspend fun ContentResolver.findAlbum(
    title: String
): Album? = queryExt(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = ALBUM_PROJECTION,
    selection = "${MediaStore.Audio.Albums.ALBUM} == ?",
    args = arrayOf(title),
    limit = 1,
    transform = { c ->
        if (!c.moveToFirst()) return null
        var result = c.toAlbum()
        c.close()
        val firstTrackAlbumArtist = findFirstTrackAlbumArtist(result.id)
        if (result.artist != firstTrackAlbumArtist) {
            val artist = findArtist(firstTrackAlbumArtist)
            result = result.copy(
                artist = artist?.name ?: result.artist ?: "null",
                artistId = artist?.id ?: result.artistId ?: 0L
            )
        }
        if (FLAG) Log.i(TAG, "Find Album Search via title $title - ALBUM DATA:\n" +
            "ID: ${result.id}\n" +
            "Title: ${result.title}\n" +
            "ArtistId: ${result.artistId}\n" +
            "Artist Name: ${result.artist}\n" +
            "Year: ${result.lastYear}\n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)


/***********************************************************************************************
 *
 * **********  GENRES SECTION ***********
 *
 **********************************************************************************************/

/**
 * Projection of Genre data class in MediaRetriever columns
 */
private val GENRE_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Genres._ID,
        MediaStore.Audio.Genres.NAME,
    )

/**
 * Default query order column for media store genre table
 */
private const val DEFAULT_GENRE_ORDER = MediaStore.Audio.Genres.NAME

/**
 * @return list of [Genre] from the [MediaStore].
 */
suspend fun ContentResolver.findGenres(
    sQuery: String? = null,
    order: String = DEFAULT_GENRE_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Genre>? = queryExt(
    uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    projection = GENRE_PROJECTION,
    selection =
        if (sQuery != null) "${MediaStore.Audio.Genres.NAME} LIKE ?"
        else DEFAULT_MEDIA_SELECTION,
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val s = when (sQuery) {
            null -> "-no filter given-"
            else -> "on '$sQuery'"
        }
        if (FLAG) Log.i(TAG, "Get Genres Search $s:\n" +
            "Genre(s) count returned: ${c.count}")
        if (c.count == 0) return null

        val result = List(c.count) {
            c.moveToPosition(it)
            c.toGenre(
                getGenreAudioCount( c.getLong(0) )
            )
        }
        c.close()
        result
    },
)

/**
 * Builds Audio list via getting genre id through genre name
 * Then gets Audio ids filtered by genre id
 * Last builds list of Audios from Audio ids
 * @return list of [Audio] based on Genre
 */
suspend fun ContentResolver.findGenreAudios(
    name: String,
    sQuery: String? = null,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio>? {
    // find the genre id using parameter name
    val genreId = queryExt(
        uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
        projection = arrayOf(MediaStore.Audio.Genres._ID),
        selection = "${MediaStore.Audio.Genres.NAME} == ?",
        args = arrayOf(name),
        limit = 1
    ) { c ->
        if (c.count == 0) return null
        c.moveToPosition(0)
        val temp = c.getLong(0)
        c.close()
        temp
    }

    // calculate the audio ids with the genre id, and it into a String
    val audioIds = queryExt(
        uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
        projection = arrayOf(MediaStore.Audio.Genres.Members.AUDIO_ID),
    ) { c ->
        if (c.count == 0) return null
        val buffer = StringBuilder()
        while (c.moveToNext()) {
            if (!c.isFirst) buffer.append(",")
            val element = c.getLong(0)
            buffer.append("'$element'")
        }
        c.close()
        buffer.toString()
    }

    // building the rest of the query arguments
    val like =
        if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        else ""
    val selection = DEFAULT_AUDIO_SELECTION + " AND ${MediaStore.Audio.Media._ID} IN ($audioIds)" + like
    val args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null

    if (FLAG) Log.i(TAG, "Get Genre Audios - selection: $selection + args $args")
    return findBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * @return list of [Audio] based on Genre ID
 */
suspend fun ContentResolver.getGenreAudiosById(
    id: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val selection = "${MediaStore.Audio.Media.GENRE_ID} == ?"
    val args = arrayOf("$id")
    if (FLAG) Log.i(TAG, "Get Genre Audios By Genre ID - selection: $selection + args $args")
    return findBucketAudios(selection, args, order, ascending, offset, limit) ?: emptyList()
}

/**
 * Get genre via genre id
 * @return [Genre]
 */
suspend fun ContentResolver.getGenre(
    id: Long
): Genre = queryExt(
    uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    projection = GENRE_PROJECTION,
    selection = "${MediaStore.Audio.Genres._ID} == ?",
    args = arrayOf("$id"),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toGenre(
            getGenreAudioCount( c.getLong(0) )
        )
        c.close()
        result
    }
)

/**
 * @return count of [Audio] for [Genre] via [id] from the [MediaStore].
 */
suspend fun ContentResolver.getGenreAudioCount(
    id: Long
): Int = queryExt(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = arrayOf(MediaStore.Audio.Media._ID),
    selection = "${MediaStore.Audio.Media.GENRE_ID} == ?",
    args = arrayOf("$id"),
    transform = { c ->
        c.moveToFirst()
        val result = c.count
        c.close()
        result
    }
)


/***********************************************************************************************
 *
 * **********  PLAYLISTS SECTION ***********
 *
 **********************************************************************************************/

/**
 * Projection of Playlist model in MediaStore columns
 */
private val PLAYLIST_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Playlists._ID,
        MediaStore.Audio.Playlists.NAME,
        MediaStore.Audio.Playlists.DISPLAY_NAME,
        MediaStore.Audio.Playlists.DATE_ADDED,
        MediaStore.Audio.Playlists.DATE_MODIFIED,
        MediaStore.Audio.Playlists.DATA,
    )

/**
 * Projection of Playlist Track Member model in MediaStore columns
 */
private val PLAYLIST_MEMBER_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Playlists.Members._ID,
        MediaStore.Audio.Playlists.Members.TITLE,
        MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
        MediaStore.Audio.Playlists.Members.ARTIST,
        MediaStore.Audio.Playlists.Members.ARTIST_ID,
        MediaStore.Audio.Playlists.Members.ALBUM_ARTIST,
        MediaStore.Audio.Playlists.Members.ALBUM,
        MediaStore.Audio.Playlists.Members.ALBUM_ID,
        MediaStore.Audio.Playlists.Members.DURATION,
        MediaStore.Audio.Playlists.Members.DATE_ADDED,
        MediaStore.Audio.Playlists.Members.DATE_MODIFIED,
        MediaStore.Audio.Playlists.Members.PLAY_ORDER,
        MediaStore.Audio.Playlists.Members.AUDIO_ID,
    )

/**
 * Default query select column for media store playlist table
 */
private const val DEFAULT_PLAYLIST_SELECTION = "${MediaStore.Audio.Playlists._ID} != 0"

/**
 * Default query order column for media store playlist table
 */
private const val DEFAULT_PLAYLIST_ORDER = MediaStore.Audio.Playlists.DISPLAY_NAME

/**
 * Default query order column for media store playlist member table
 */
private const val DEFAULT_PLAYLIST_MEMBER_ORDER = MediaStore.Audio.Playlists.Members.PLAY_ORDER

/**
 * @return list of [Playlist] from the [MediaStore].
 */
suspend fun ContentResolver.findPlaylists(
    order: String = DEFAULT_PLAYLIST_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Playlist>? = queryExt(
    uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
    projection = PLAYLIST_PROJECTION,
    selection = DEFAULT_PLAYLIST_SELECTION,
    args = null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        if (FLAG) Log.i(TAG, "Get Playlists Search:\n" +
                "Playlist(s) count returned: ${c.count}")
        if (c.count == 0) return null

        val result = List(c.count) {
            c.moveToPosition(it)
            c.toPlaylist(
                getPlaylistTrackCount( c.getLong(0) )
            )
        }
        c.close()
        result
    },
)

/**
 * @return [Playlist] via [id] from the [MediaStore].
 */
suspend fun ContentResolver.getPlaylist(
    id: Long
): Playlist  = queryExt(
    uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
    projection = PLAYLIST_PROJECTION,
    selection = "${MediaStore.Audio.Playlists._ID} == ?",
    args = arrayOf("$id"),
    transform = { c ->
        c.moveToFirst()
        val result = c.toPlaylist(
            getPlaylistTrackCount( c.getLong(0) )
        )
        c.close()
        if (FLAG) Log.i(TAG, "Find Playlist Search via ID: $id\n" +
            "Name: ${result.name}\n" +
            "Date Added: ${result.dateAdded}\n" +
            "Date Modified: ${result.dateModified}\n" +
            "File Path: ${result.path}")
        result
    },
)

/**
 * @return list of [PlaylistTrack] for [Playlist] via [id] from the [MediaStore].
 */
suspend fun ContentResolver.findPlaylistTracks(
    id: Long,
    order: String = DEFAULT_PLAYLIST_MEMBER_ORDER,
    ascending: Boolean = true,
    limit: Int = Int.MAX_VALUE,
): List<PlaylistTrack>? = queryExt(
    uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id),
    projection = PLAYLIST_MEMBER_PROJECTION,
    selection = "${MediaStore.Audio.Playlists.Members.PLAYLIST_ID} == ?",
    args = arrayOf("$id"),
    order = order,
    ascending = ascending,
    limit = limit,
    transform = { c ->
        if (c.count == 0) return null
        if (FLAG) Log.i(TAG, "Find Playlist Tracks:\n" +
                "Track(s) count returned: ${c.count}")
        val result = List(c.count) {
            c.moveToPosition(it)
            c.toPlaylistTrack()
        }
        result
    }
)

/**
 * @return count of [PlaylistTrack] for [Playlist] via [id] from the [MediaStore].
 */
suspend fun ContentResolver.getPlaylistTrackCount(
    id: Long
): Int = queryExt(
    uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id),
    projection = PLAYLIST_MEMBER_PROJECTION,
    selection = "${MediaStore.Audio.Playlists.Members.PLAYLIST_ID} == ?",
    args = arrayOf("$id"),
    transform = { c ->
        c.moveToFirst()
        val result = c.count
        c.close()
        result
    }
)


/***********************************************************************************************
 *
 * **********  EXTRA PROPERTIES SECTION ***********
 *
 **********************************************************************************************/

/**
 * Fetches the content URIs for the given media IDs.
 *
 * This function queries the MediaStore to determine the typeof each media item (image or video)
 * based on the provided IDs and constructs the corresponding content URIs.
 *
 * @param ids The IDs of the media items to fetch URIs for.
 * @return A list of content URIs corresponding to the given IDs.
 */
internal suspend fun ContentResolver.fetchContentUri(vararg ids: Long): List<Uri> {
    // Create a comma-separated string of IDs for the SQL IN clause.
    val idsString = ids.joinToString(",") { it.toString() }

    // Define the projection to retrieve the ID and media type of each item.
    val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.CONTENT_TYPE)

    // Define the selection clause to filter items based on the provided IDs.
    val selection = "${MediaStore.Audio.Media._ID} IN ($idsString)"

    // Query the MediaStore and transform the result into a list of content URIs.
    return queryExt(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // The base content URI for media files
        projection, // The columns to retrieve
        selection, // The selection clause to filter results
        transform = { c ->
            List(c.count) { index -> // Iterate over the cursor results
                c.moveToPosition(index) // Move to the current row
                val type = c.getInt(1) // Get the media type (image or video)
                // Construct the appropriate content URI based on the media type.
                val uri = when (type) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                ContentUris.withAppendedId(uri, c.getLong(0)) // Append the ID to the URI
            }
        }
    )
}

/**
 * Delete entry from media store by id
 */
/*suspend fun ContentResolver.deleteFile(id: Long) {
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    //val selection = "${MediaStore.Audio.Media._ID} == ?"
    //val args = arrayOf("1003104264")
    val args = arrayOf("$id")
    return withContext(Dispatchers.Default) {
        // Use the modern query approach for devices running Android 10 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Compose the arguments for the query
            val args2 = Bundle().apply {
                // Set the limit and offset for pagination
                putInt(ContentResolver.QUERY_ARG_LIMIT, 1)
                putInt(ContentResolver.QUERY_ARG_OFFSET, 0)

                // Set the selection arguments and selection string
                if (args != null)
                    putStringArray(
                        ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                        args
                    )
                //putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Audio.Media._ID} == ?")

            }
            delete(uri, args2)
        }
    } ?: throw NullPointerException("Can't retrieve cursor for $uri")
}*/

/**
 * Deletes list uri from the device persistently.
 * @return -1 if error occurred otherwise -2 indicating an dialog is about to be shown to user
 * and he needs to confirm before deleting.
 */
/*@RequiresApi(Build.VERSION_CODES.R)
fun ContentResolver.delete(activity: Activity, vararg uri: Uri): Int {
    val deleteRequest = MediaStore.createDeleteRequest(this, uri.toList()).intentSender
    activity.startIntentSenderForResult(deleteRequest, 100, null, 0, 0, 0)
    return -2 // dialog is about to be shown.
}*/

/**
 * Deletes the specified URIs from the device's persistent storage permanently.
 *
 *  Note hat this fun works only unto android 10.
 * param uri The URIs to delete.
 * return The number of items that were deleted, or -1 if an error occurred.
 */
/*suspend fun ContentResolver.delete(vararg uri: Uri): Int {
    if (uri.isEmpty())
        return 0
    // construct which ids have been removed.
    val ids = uri.joinToString(", ") {
        "${ContentUris.parseId(it)}"
    }
    val parent = uri[0].removeId
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val res =
        queryExt(parent, projection, "${MediaStore.MediaColumns._ID} IN ($ids)") { c ->
            List(c.count) { c.moveToPosition(it);c.getString(0) }
        }
    // check if res in null
    if (res == null) return -1 // error
    var count = delete(parent, "${MediaStore.MediaColumns._ID} IN ($ids)", null)
    if (count == 0) return -1 // error
    res.forEach {
        // decrease count in case files cant be deleted
        if (!File(it).delete())
            count--
    }
    // error
    return if (count == 0) -1 else count
}*/

/**
 * Deletes the specified URIs from the device's content permanently.
 *
 * param activity The ComponentActivity that is making the call.
 * param uri The URIs of the content to be deleted.
 * return The number of items that were deleted, or -1 if an error occurred. -3 if cancelled by user.
 */
/*@RequiresApi(Build.VERSION_CODES.R)
suspend fun ContentResolver.delete(activity: ComponentActivity, vararg uri: Uri): Int {
    if (uri.isEmpty()) return -1 // error
    return suspendCoroutine { continuation ->
        // Create a lazy ActivityResultLauncher object
        var launcher: ActivityResultLauncher<IntentSenderRequest>? = null
        // Assign result to launcher in such a way tha it allows us to
        // unregister later.
        val contract = ActivityResultContracts.StartIntentSenderForResult()
        launcher = activity.registerActivityResultLauncher(contract) {
            // unregister launcher
            launcher?.unregister()
            // user cancelled
            if (it.resultCode == Activity.RESULT_CANCELED) {
                continuation.resume(-3 /*cancelled*/)
                return@registerActivityResultLauncher
            }
            // some unknown error occurred
            if (it.resultCode != Activity.RESULT_OK) {
                continuation.resume(-1 /*Error*/)
                return@registerActivityResultLauncher
            }
            // construct which ids have been removed.
            val ids = uri.joinToString(", ") {
                "${ContentUris.parseId(it)}"
            }
            // assuming that all uri's are content uris.
            val parent = ContentUris.removeId(uri[0])
            // check how many are still there; maybe some error occurred while deleting some files.
            val projection = arrayOf(MediaStore.MediaColumns._ID)
            val count =
                query(
                    parent,
                    projection,
                    "${MediaStore.MediaColumns._ID} IN ($ids)",
                    null,
                    null
                ).use { it?.count ?: -1 }
            // resume with how many files have been deleted or error code.
            continuation.resume(if (count > 0) uri.size - count else if (count == 0) uri.size else count)
        }
        val request = MediaStore.createDeleteRequest(this, uri.toList()).intentSender
        // Create an IntentSenderRequest object from the IntentSender object
        val intentSenderRequest = IntentSenderRequest.Builder(request).build()
        // Launch the activity for result using the IntentSenderRequest object
        launcher.launch(intentSenderRequest)
    }
}*/

/**
 * A simple extension method that trashes files instead of deleting them.
 * see delete
 */
/*@RequiresApi(Build.VERSION_CODES.R)
suspend fun ContentResolver.trash(activity: ComponentActivity, vararg uri: Uri): Int {
    if (uri.isEmpty()) return -1 // error
    return suspendCoroutine { continuation ->
        // Create a lazy ActivityResultLauncher object
        var launcher: ActivityResultLauncher<IntentSenderRequest>? = null
        // Assign result to launcher in such a way tha it allows us to
        // unregister later.
        val contract = ActivityResultContracts.StartIntentSenderForResult()
        launcher = activity.registerActivityResultLauncher(contract) {
            // unregister launcher
            launcher?.unregister()
            // user cancelled
            if (it.resultCode == Activity.RESULT_CANCELED) {
                continuation.resume(-3 /*cancelled*/)
                return@registerActivityResultLauncher
            }
            // some unknown error occurred
            if (it.resultCode != Activity.RESULT_OK) {
                continuation.resume(-1 /*Error*/)
                return@registerActivityResultLauncher
            }
            // construct which ids have been removed.
            val ids = uri.joinToString(", ") {
                "${ContentUris.parseId(it)}"
            }
            // assuming that all uri's are content uris.
            val parent = ContentUris.removeId(uri[0])
            // check how many are still there; maybe some error occurred while deleting some files.
            val projection = arrayOf(MediaStore.MediaColumns._ID)
            val count =
                query(
                    parent,
                    projection,
                    "${MediaStore.MediaColumns._ID} IN ($ids)",
                    null,
                    null
                ).use { it?.count ?: -1 }
            // resume with how many files have been trashed or error code.
            continuation.resume(if (count > 0) uri.size - count else if (count == 0) uri.size else count)
        }
        val request = MediaStore.createTrashRequest(this, uri.toList(), true).intentSender
        // Create an IntentSenderRequest object from the IntentSender object
        val intentSenderRequest = IntentSenderRequest.Builder(request).build()
        // Launch the activity for result using the IntentSenderRequest object
        launcher.launch(intentSenderRequest)
    }
}*/

/**
 * A simple extension method that trashes files instead of deleting them.
 * see delete
 */
/*@RequiresApi(Build.VERSION_CODES.R)
fun ContentResolver.trash(activity: Activity, vararg uri: Uri): Int {
    val deleteRequest = MediaStore.createTrashRequest(this, uri.toList(), true).intentSender
    activity.startIntentSenderForResult(deleteRequest, 100, null, 0, 0, 0)
    return -2 // dialog is about to be shown.
}*/