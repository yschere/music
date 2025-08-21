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
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.model.Genre
import com.example.music.data.mediaresolver.model.toAlbum
import com.example.music.data.mediaresolver.model.toArtist
import com.example.music.data.mediaresolver.model.toAudio
import com.example.music.data.mediaresolver.model.toGenre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val TAG = "Resolver"
private const val DUMMY_SELECTION = "${MediaStore.Audio.Media._ID} != 0"


/***********************************************************************************************
 *
 * **********  CONTENT RESOLVER BASE FUNCTIONS SECTION ***********
 *
 **********************************************************************************************/


/**
 * Content Resolver's query to MediaStore to retrieve Cursor containing query result
 */
@SuppressLint("Recycle")
suspend fun ContentResolver.query2(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String = DUMMY_SELECTION,
    args: Array<String>? = null,
    order: String = MediaStore.MediaColumns._ID,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): Cursor {
    //return withContext(dispatcher) {
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
            //appContext.contentResolver.query(uri, projection, args2, null)
            query(uri, projection, args2, null)
        }
        //Fallback to the traditional query approach for devices running older Android versions
        else {
            // Construct the ORDER BY clause with limit and offset
            //language=SQL
            val order2 =
                order + (if (ascending) " ASC" else " DESC") + " LIMIT $limit OFFSET $offset"
            // Perform the query with the traditional approach
            //appContext.contentResolver.query(uri, projection, selection, args, order2)
            query(uri, projection, selection, args, order2)
        }
    } ?: throw NullPointerException("Can't retrieve cursor for $uri")
}

/**
 * Transformer for Content Resolver queries from Cursor to type T
 */
internal suspend inline fun <T> ContentResolver.query2(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String = DUMMY_SELECTION,
    args: Array<String>? = null,
    order: String = MediaStore.MediaColumns._ID,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE,
    transform: (Cursor) -> T
): T = query2(uri, projection, selection, args, order, ascending, offset, limit).use(transform)

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
        MediaStore.Audio.AudioColumns.TRACK, // 16
        MediaStore.Audio.AudioColumns.DURATION, // 17
        MediaStore.Audio.AudioColumns.BITRATE, // 18

        //adding for testing what these columns return as
        MediaStore.Audio.AudioColumns._ID, // 19
        MediaStore.Audio.AudioColumns.DISC_NUMBER, // 20
        MediaStore.Audio.AudioColumns.NUM_TRACKS, // 21
        MediaStore.MediaColumns.CD_TRACK_NUMBER, // 22
    )

private const val DEFAULT_AUDIO_SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
private const val DEFAULT_AUDIO_ORDER = MediaStore.Audio.Media.TITLE

/**
 * @return list of [Audio] from the [MediaStore].
 */
suspend fun ContentResolver.getAudios(
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val id = MediaStore.Audio.Media._ID
    val title = MediaStore.Audio.Media.TITLE
    //val artist = MediaStore.Audio.Media.ARTIST
    //val album = MediaStore.Audio.Media.ALBUM

    return query2(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection = AUDIO_PROJECTION,
        selection = DEFAULT_AUDIO_SELECTION +
            if (sQuery != null) " AND $id || $title LIKE ?"
            else "",
        args =
            if (sQuery != null) arrayOf("%$sQuery%")
            else null,
        order = order,
        ascending = ascending,
        offset = offset,
        limit = limit,
        transform = { c ->
            val result = List(c.count) {
                c.moveToPosition(it)
                c.toAudio()
            }
            c.close()
            Log.i(TAG, "search query ${sQuery?:""} - AUDIO DATA: \n" +
                "Audio count returned: ${result.count()}\n" +
                "ID: ${result[0].id} \n" +
                "Title: ${result[0].title} \n" +
                "Artist: ${result[0].artist} \n" +
                "Album: ${result[0].album} \n" +
                "Genre: ${result[0].genre} \n" +
                "CD TrackNumber: ${result[0].cdTrackNumber} \n" +
                "Date Added: ${result[0].dateAdded} \n" +
                "Date Modified: ${result[0].dateModified} \n" +
                "Disc Number: ${result[0].discNumber} \n" +
                "Src TrackNumber: ${result[0].srcTrackNumber} \n" +
                "File Path: ${result[0].path} \n" +
                "File Size: ${result[0].size} \n" +
                "Year: ${result[0].year} \n"
            )
            result
        },
    )
}

/**
 * @return list of [Audio] from given bucket/directory
 */
private suspend inline fun ContentResolver.getBucketAudios(
    selection: String,
    args: Array<String>? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    return query2(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection = AUDIO_PROJECTION,
        selection = selection,
        args = args,
        order = order,
        ascending = ascending,
        offset = offset,
        limit = limit,
        transform = { c ->
            Log.i(TAG, "Get Bucket Audios returned: ${c.count} rows + ${c.columnNames}" )
            val result = List(c.count) {
                c.moveToPosition(it)
                c.toAudio()
            }
            c.close()
            Log.i(TAG, "AUDIO DATA: \n" +
                "Audio count returned: ${result.count()}\n" +
                "ID: ${result[0].id} \n" +
                "Title: ${result[0].title} \n" +
                "Artist: ${result[0].artist} \n" +
                "Album: ${result[0].album} \n" +
                "Genre: ${result[0].genre} \n" +
                "CD TrackNumber: ${result[0].cdTrackNumber} \n" +
                "Date Added: ${result[0].dateAdded} \n" +
                "Date Modified: ${result[0].dateModified} \n" +
                "Disc Number: ${result[0].discNumber} \n" +
                "Src TrackNumber: ${result[0].srcTrackNumber} \n" +
                "File Path: ${result[0].path} \n" +
                "File Size: ${result[0].size} \n" +
                "Year: ${result[0].year} \n"
            )
            result
        }
    )
}

/**
 * Search for [Audio] on [MediaStore.Audio.Media._ID]
 * @param id [Long]
 * @return [Cursor] transformed to [Audio]
 */
suspend fun ContentResolver.findAudio(id: Long): Audio =
    query2(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection = AUDIO_PROJECTION,
        selection = "${MediaStore.Audio.Media._ID} == ?",
        args = arrayOf("$id"),
        transform = { c ->
            c.moveToFirst()
            val result = c.toAudio()
            c.close()
            Log.i(TAG, "findAudio via audioId $id - AUDIO DATA: \n" +
                "ID: ${result.id} \n" +
                "Title: ${result.title} \n" +
                "Artist: ${result.artist} \n" +
                "Album: ${result.album} \n" +
                "Genre: ${result.genre} \n" +
                "CD TrackNumber: ${result.cdTrackNumber} \n" +
                "Date Added: ${result.dateAdded} \n" +
                "Date Modified: ${result.dateModified} \n" +
                "Disc Number: ${result.discNumber} \n" +
                "Src TrackNumber: ${result.srcTrackNumber} \n" +
                "File Path: ${result.path} \n" +
                "File Size: ${result.size} \n" +
                "Year: ${result.year} \n"
            )
            result
        },
    )

/**
 * Search for [Audio] on [MediaStore.Audio.Media.DATA], limit 1
 * @param path [String]
 * @return [Cursor] transformed to [Audio]
 */
suspend fun ContentResolver.findAudio(path: String): Audio =
    query2(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection = AUDIO_PROJECTION,
        selection = "${MediaStore.Audio.Media.DATA} == ?",
        args = arrayOf(path),
        limit = 1,
        transform = { c ->
            c.moveToFirst()
            val result = c.toAudio()
            c.close()
            Log.i(TAG, "findAudio via filepath $path - AUDIO DATA: \n" +
                    "ID: ${result.id} \n" +
                    "Title: ${result.title} \n" +
                    "Artist: ${result.artist} \n" +
                    "Album: ${result.album} \n" +
                    "Genre: ${result.genre} \n" +
                    "CD TrackNumber: ${result.cdTrackNumber} \n" +
                    "Date Added: ${result.dateAdded} \n" +
                    "Date Modified: ${result.dateModified} \n" +
                    "Disc Number: ${result.discNumber} \n" +
                    "Src TrackNumber: ${result.srcTrackNumber} \n" +
                    "File Path: ${result.path} \n" +
                    "File Size: ${result.size} \n" +
                    "Year: ${result.year} \n"
            )
            result
        },
    )

/**
 * Search for [Audio] on uri, limit 1
 * @param uri [Uri]
 * @return [Cursor] transformed to [Audio]
 */
private suspend fun ContentResolver.findAudio(uri: Uri): Audio =
    query2(
        uri = uri,
        projection = AUDIO_PROJECTION,
        limit = 1,
        transform = { c ->
            c.moveToFirst()
            val result = c.toAudio()
            c.close()
            Log.i(TAG, "findAudio via uri $uri - AUDIO DATA: \n" +
                "ID: ${result.id} \n" +
                "Title: ${result.title} \n" +
                "Artist: ${result.artist} \n" +
                "Album: ${result.album} \n" +
                "Genre: ${result.genre} \n" +
                "CD TrackNumber: ${result.cdTrackNumber} \n" +
                "Date Added: ${result.dateAdded} \n" +
                "Date Modified: ${result.dateModified} \n" +
                "Disc Number: ${result.discNumber} \n" +
                "Src TrackNumber: ${result.srcTrackNumber} \n" +
                "File Path: ${result.path} \n" +
                "File Size: ${result.size} \n" +
                "Year: ${result.year} \n"
            )
            result
        },
    )

/**
 * External function for content resolver to retrieve audios
 */
fun ContentResolver.audios(
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).map {
    getAudios(sQuery, order, ascending)
}


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
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
        MediaStore.Audio.Artists.DEFAULT_SORT_ORDER,
    )

/**
 * default query selection for media store albums
 */
private const val DEFAULT_ARTIST_SELECTION = "${MediaStore.Audio.Artists._ID} != null"
private const val DEFAULT_ARTIST_ORDER = MediaStore.Audio.Artists.ARTIST

/**
 * @return list of [Artist] from the [MediaStore].
 */
suspend fun ContentResolver.getArtists(
    sQuery: String? = null,
    order: String = DEFAULT_ARTIST_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Artist> = query2(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection = DEFAULT_ARTIST_SELECTION +
        if (sQuery != null) " AND ${MediaStore.Audio.Artists.ARTIST} LIKE ?"
        else "",
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val result = List(c.count) {
            c.moveToPosition(it)
            c.toArtist()
        }
        c.close()
        Log.i(TAG, "search query ${sQuery?:""} - ARTIST DATA: \n" +
            "Artist count returned: ${result.count()}\n" +
            "ID: ${result[0].id} \n" +
            "Name: ${result[0].name} \n" +
            "Default Sort Order: ${result[0].sort} \n" +
            "Album count: ${result[0].numAlbums} \n" +
            "Track count: ${result[0].numTracks}"
        )
        result
    },
)

/**
 * @return list of [Audio] based on Artist
 */
suspend fun ContentResolver.getArtistAudios(
    name: String,
    sQuery: String? = null,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val like = 
        if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?" 
        else ""
    val selection = "${MediaStore.Audio.Media.ARTIST} == ?" + like
    val args = 
        if (sQuery != null) arrayOf(name, "%$sQuery%") 
        else arrayOf(name)
    Log.i(TAG, "Get Artist Audios - selection: $selection + args: $args")
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * @return list of [Audio] based on Artist Id
 */
suspend fun ContentResolver.getArtistAudiosById(
    id: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val selection = "${MediaStore.Audio.Media.ARTIST_ID} == ?"
    val args = arrayOf("$id")
    Log.i(TAG, "Get Artist Audios via ID - selection: $selection + args: $args")
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * Search for [Artist] on name, limit 1
 * @param name [String]
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtist(name: String): Artist = query2(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection = "${MediaStore.Audio.Artists.ARTIST} == ?",
    args = arrayOf(name),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toArtist()
        c.close()
        Log.i(TAG, "Find Artist via Name $name - ARTIST DATA: \n" +
            "ID: ${result.id} \n" +
            "Name: ${result.name} \n" +
            "Default Sort Order: ${result.sort} \n" +
            "Album count: ${result.numAlbums} \n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)

/**
 * Search for [Artist] on _id, limit 1
 * @param id [Long]
 * @return [Cursor] transformed to [Artist]
 */
suspend fun ContentResolver.findArtist(id: Long): Artist = query2(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection = "${MediaStore.Audio.Artists._ID} == ?",
    args = arrayOf("$id"),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toArtist()
        c.close()
        Log.i(TAG, "findArtist via artistId $id - ARTIST DATA: \n" +
            "ID: ${result.id} \n" +
            "Name: ${result.name} \n" +
            "Default Sort Order: ${result.sort} \n" +
            "Album count: ${result.numAlbums} \n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)

/**
 * Trying to find artist info based on album Id, limit 1
 * @param albumId [Long]
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtistByAlbumId(albumId: Long): Artist = query2(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection = "${MediaStore.Audio.Albums.ALBUM_ID} == ?",
    args = arrayOf("$albumId"),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toArtist()
        c.close()
        Log.i(TAG, "findArtist via albumId $albumId - ARTIST DATA: \n" +
            "ID: ${result.id} \n" +
            "Name: ${result.name} \n" +
            "Default Sort Order: ${result.sort} \n" +
            "Album count: ${result.numAlbums} \n" +
            "Track count: ${result.numTracks}"
        )
        result
    },
)

/**
 * External function for content resolver to retrieve artists
 */
fun ContentResolver.artists(
    sQuery: String? = null,
    order: String = DEFAULT_ARTIST_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI).map {
    getArtists(sQuery, order, ascending)
}

/**
 * External function for content resolver to retrieve artist audios
 */
fun ContentResolver.artistAudios(
    name: String,
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).map {
    getArtistAudios(name, sQuery, order, ascending)
}


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
        MediaStore.Audio.Albums.ALBUM_ID,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.ARTIST_ID,
        MediaStore.Audio.Albums.LAST_YEAR,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        MediaStore.Audio.Albums.DEFAULT_SORT_ORDER,
        //MediaStore.Audio.Albums.ALBUM_ART,//says to use ContentResolver.loadThumbnail?
    )

/**
 * default query selection for media store albums
 */
private const val DEFAULT_ALBUM_SELECTION = "${MediaStore.Audio.Albums.ALBUM_ID} != null"
private const val DEFAULT_ALBUM_ORDER = MediaStore.Audio.Albums.ALBUM

/**
 * @return list of [Album] from the [MediaStore].
 */
suspend fun ContentResolver.getAlbums(
    sQuery: String? = null,
    order: String = DEFAULT_ALBUM_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Album> = query2(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = ALBUM_PROJECTION,
    selection = DEFAULT_ALBUM_SELECTION +
        if (sQuery != null) " AND ${MediaStore.Audio.Albums.ALBUM} LIKE ?"
        else "",
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val result = List(c.count) {
            c.moveToPosition(it)
            c.toAlbum()
        }
        c.close()
        Log.i(TAG, "search query ${sQuery?:""} - ALBUM DATA: \n" +
            "Album count returned: ${result.count()}\n" +
            "ID: ${result[0].id} \n" +
            "Title: ${result[0].title} \n" +
            "ArtistId: ${result[0].artistId} \n" +
            "Artist Name: ${result[0].artist} \n" +
            "Year: ${result[0].lastYear} \n" +
            "Track count: ${result[0].numTracks} \n" +
            "Default Sort Order: ${result[0].sort}"
        )
        result
    },
)

/**
 * @return list of [Audio] based on Album
 */
suspend fun ContentResolver.getAlbumAudios(
    title: String,
    sQuery: String? = null,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val like =
        if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        else ""
    val selection = "${MediaStore.Audio.Media.ALBUM} == ?" + like
    val args =
        if (sQuery != null) arrayOf(title, "%$sQuery%")
        else arrayOf(title)
    Log.i(TAG, "Get Album Audios - selection: $selection + args $args")
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * @return list of [Audio] based on Album Id
 */
suspend fun ContentResolver.getAlbumAudiosById(
    id: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val selection = "${MediaStore.Audio.Media.ALBUM_ID} == ?"
    val args = arrayOf("$id")
    Log.i(TAG, "Get Album Audios By ID - selection: $selection + args: $args")
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * Search for [Album] on [MediaStore.Audio.Media._ID], limit 1
 * @param id [Long]
 * @return [Cursor] transformed to [Album]
 */
suspend fun ContentResolver.findAlbum(id: Long): Album = query2(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = ALBUM_PROJECTION,
    selection = "${MediaStore.Audio.Albums.ALBUM_ID} == ?",
    args = arrayOf("$id"),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toAlbum()
        c.close()
        Log.i(TAG, "findAlbum via albumId $id - ALBUM DATA: \n" +
            "ID: ${result.id} \n" +
            "Title: ${result.title} \n" +
            "ArtistId: ${result.artistId} \n" +
            "Artist Name: ${result.artist} \n" +
            "Year: ${result.lastYear} \n" +
            "Track count: ${result.numTracks} \n" +
            "Default Sort Order: ${result.sort}"
        )
        result
    },
)

/**
 * Search for [Album] on title, limit 1
 * @param title [String]
 * @return [Cursor] transformed to [Album]
 */
suspend fun ContentResolver.findAlbum(title: String): Album = query2(
    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    projection = ALBUM_PROJECTION,
    selection = "${MediaStore.Audio.Albums.ALBUM} == ?",
    args = arrayOf(title),
    limit = 1,
    transform = { c ->
        c.moveToFirst()
        val result = c.toAlbum()
        c.close()
        Log.i(TAG, "findAlbum via title $title - ALBUM DATA: \n" +
            "ID: ${result.id} \n" +
            "Title: ${result.title} \n" +
            "ArtistId: ${result.artistId} \n" +
            "Artist Name: ${result.artist} \n" +
            "Year: ${result.lastYear} \n" +
            "Track count: ${result.numTracks} \n" +
            "Default Sort Order: ${result.sort}"
        )
        result
    },
)

/**
 * External function for content resolver to retrieve albums
 */
fun ContentResolver.albums(
    sQuery: String? = null,
    order: String = DEFAULT_ALBUM_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI).map {
    Log.i(TAG, "Get Albums query")
    getAlbums(sQuery, order, ascending)
}

/**
 * External function for content resolver to retrieve album audios
 */
fun ContentResolver.albumAudios(
    title: String,
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).map {
    Log.i(TAG, "Get Album Audios query")
    getAlbumAudios(title, sQuery, order, ascending)
}


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
        MediaStore.Audio.Genres.DEFAULT_SORT_ORDER,
    )

/**
 * default query selection for media store albums
 */
private const val DEFAULT_GENRE_SELECTION = "${MediaStore.Audio.Genres._ID} != null"
private const val DEFAULT_GENRE_ORDER = MediaStore.Audio.Genres.NAME

/**
 * @return list of [Genre] from the [MediaStore].
 */
suspend fun ContentResolver.getGenres(
    sQuery: String? = null,
    order: String = DEFAULT_GENRE_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Genre> = query2(
    uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    projection = GENRE_PROJECTION,
    selection = DEFAULT_GENRE_SELECTION +
        if (sQuery != null) " AND ${MediaStore.Audio.Genres.NAME} LIKE ?"
        else "",
    args =
        if (sQuery != null) arrayOf("%$sQuery%")
        else null,
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val result = List(c.count) {
            c.moveToPosition(it)
            c.toGenre(
                getGenreAudioCount(
                    c.getLong(0)
                )
            )
        }
        c.close()
        Log.i(TAG, "search query ${sQuery?:""} - GENRE DATA:\n" +
            "Genre count returned: ${result.count()}\n" +
            "ID: ${result[0].id}\n" +
            "Name: ${result[0].name}\n"
        )
        result
    },
)

/**
 * Builds Audio list via getting genre id through genre name
 * Then gets Audio ids filtered by genre id
 * Last builds list of Audios from Audio ids
 * @return list of [Audio] based on Genre
 */
suspend fun ContentResolver.getGenreAudios(
    name: String,
    sQuery: String? = null,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    // find the genre id using parameter name
    val genreId = query2(
        uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
        projection = arrayOf(MediaStore.Audio.Genres._ID),
        selection = "${MediaStore.Audio.Genres.NAME} == ?",
        args = arrayOf(name),
        limit = 1
    ) { c ->
        if (c.count == 0) return emptyList()
        c.moveToPosition(0)
        val temp = c.getLong(0)
        c.close()
        temp
    }

    // calculate the audio ids with the genre id, and it into a String
    val audioIds = query2(
        MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
        arrayOf(MediaStore.Audio.Genres.Members.AUDIO_ID),
    ) { c ->
        if (c.count == 0) return emptyList()
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

    Log.i(TAG, "Get Genre Audios - selection: $selection + args $args")
    return getBucketAudios(selection, args, order, ascending, offset, limit)
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
    Log.i(TAG, "Get Genre Audios By Genre ID - selection: $selection + args $args")
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * Find genre via genre id
 * @return [Genre]
 */
suspend fun ContentResolver.findGenre(id: Long): Genre {
    return query2(
        MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
        GENRE_PROJECTION,
        "${MediaStore.Audio.Genres._ID} == ?",
        arrayOf("$id"),
        limit = 1,
    ) {
        it.moveToFirst()
        val temp = it.toGenre()
        it.close()
        temp
    }
}

/**
 * Find count of audios in genre by genre id
 * @return [Int] audio count
 */
//suspend fun ContentResolver.getGenreWithAudioCount(id: Long): Genre {
//    val gen = findGenre(id)
//    val count = query2(
//        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//        arrayOf(MediaStore.Audio.Media._ID),
//        "${MediaStore.Audio.Media.GENRE_ID} == ?",
//        arrayOf("$id"),
//    ) {
//        it.moveToFirst()
//        it.count
//    }
//    return Genre(id, gen.name, count)
//}

suspend fun ContentResolver.getGenreAudioCount(id: Long): Int {
    return query2(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Audio.Media._ID),
        "${MediaStore.Audio.Media.GENRE_ID} == ?",
        arrayOf("$id"),
    ) {
        it.moveToFirst()
        it.count
    }
}

/**
 * External function for content resolver to retrieve genres
 */
fun ContentResolver.genres(
    sQuery: String? = null,
    order: String = MediaStore.Audio.Genres.NAME,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI).map {
    getGenres(sQuery, order, ascending)
}

/**
 * External function for content resolver to retrieve genre audios
 */
fun ContentResolver.genreAudios(
    name: String,
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).map {
    getGenreAudios(name, sQuery, order, ascending)
}


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
    return query2(
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
        query2(parent, projection, "${MediaStore.MediaColumns._ID} IN ($ids)") { c ->
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