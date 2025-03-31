package com.example.music.domain.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Stable
import androidx.core.app.ActivityCompat
import androidx.media3.common.MediaItem
import com.example.music.domain.util.MediaRepo.Companion.toAlbumArtUri
//import com.example.music.data.store.MediaRetriever
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
                // TODO: Consider adding support for group by.
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
 * Domain model Audio for Resolver - should maybe translate to SongInfo or PlayerSong decently?
 */
@Stable
data class Audio(
    @JvmField val id: Long,
    @JvmField val title: String,
    @JvmField val mimeType: String,
    @JvmField val path: String,
    @JvmField val dateAdded: Long,
    @JvmField val dateModified: Long,
    @JvmField val size: Long,

    @JvmField val artist: String,
    @JvmField val artistId: Long,
    @JvmField val album: String,
    @JvmField val albumId: Long,
    @JvmField val albumArtist: String,
    @JvmField val composer: String,
    @JvmField val genre: String,
    @JvmField val genreId: Long,
    @JvmField val year: Int,
    @JvmField val trackNumber: Int,
    @JvmField val duration: Int,
    @JvmField val bitrate: Int,

    @JvmField val audioId: Long,
    @JvmField val discNumber: Int,
    @JvmField val srcTrackNumber: Int,
    @JvmField val cdTrackNumber: Int,
)

/**
 * Transform Cursor to type Audio
 */
private fun Cursor.toAudio(): Audio {
    domainLogger.info {
        "Cursor to Audio: \n" +
            "id: ${getLong(0)} \n" +
            "title: ${getString(1)}\n" +
            "year: ${getInt(15)}\n" +
            "testing if id is same as audioId: ${getLong(0)==getLong(19)}"
    }
    return Audio(
        id = getLong(0),
        title = getString(1) ?: MediaStore.UNKNOWN_STRING,
        mimeType = getString(2) ?: MediaStore.UNKNOWN_STRING,
        path = getString(3) ?: MediaStore.UNKNOWN_STRING,
        dateAdded = getLong(4) * 1000, //normally return in ms, so multiply by 100 to turn into seconds
        dateModified = getLong(5) * 1000, //normally return in ms, so multiply by 100 to turn into seconds
        size = getLong(6),

        artist = getString(7) ?: MediaStore.UNKNOWN_STRING,
        artistId = getLong(8),
        album = getString(9) ?: MediaStore.UNKNOWN_STRING,
        albumId = getLong(10),
        albumArtist = getString(11) ?: MediaStore.UNKNOWN_STRING,
        composer = getString(12) ?: MediaStore.UNKNOWN_STRING,
        genre = getString(13) ?: MediaStore.UNKNOWN_STRING,
        genreId = getLong(14),
        year = getInt(15),
        trackNumber = getInt(16),
        duration = getInt(17),
        bitrate = getInt(18),

        audioId = getLong(19),
        discNumber = getInt(20),
        srcTrackNumber = getInt(21),
        cdTrackNumber = getInt(22),
    )
}

/**
 * Projection of Audio model in MediaRetriever columns
 */
private val AUDIO_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.Media._ID, // 0
        MediaStore.Audio.Media.TITLE, // 2
        MediaStore.Audio.Media.MIME_TYPE, // 3
        MediaStore.Audio.Media.DATA, // 4
        MediaStore.Audio.Media.DATE_ADDED, // 5
        MediaStore.Audio.Media.DATE_MODIFIED, // 6
        MediaStore.Audio.Media.SIZE, // 7

        MediaStore.Audio.AudioColumns.ARTIST, // 9
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 10
        MediaStore.Audio.AudioColumns.ALBUM, // 11
        MediaStore.Audio.AudioColumns.ALBUM_ID, // 12
        MediaStore.Audio.AudioColumns.ALBUM_ARTIST, // 13
        MediaStore.Audio.AudioColumns.COMPOSER, // 14
        MediaStore.Audio.AudioColumns.GENRE, // 15
        MediaStore.Audio.AudioColumns.GENRE_ID, // 16
        MediaStore.Audio.AudioColumns.YEAR, // 17
        MediaStore.Audio.AudioColumns.TRACK, // 18
        MediaStore.Audio.AudioColumns.DURATION, // 19
        MediaStore.Audio.AudioColumns.BITRATE, // 20

        //adding for testing what these columns return as
        MediaStore.Audio.AudioColumns._ID, // 21
        MediaStore.Audio.AudioColumns.DISC_NUMBER, // 22
        MediaStore.Audio.AudioColumns.NUM_TRACKS, // 23
        MediaStore.MediaColumns.CD_TRACK_NUMBER, // 24
    )

private const val DEFAULT_AUDIO_SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
private const val DEFAULT_AUDIO_ORDER = MediaStore.Audio.Media.TITLE

/**
 * @return list of [Audio] from the [MediaStore].
 */
//@RequiresPermission(anyOf = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
suspend fun ContentResolver.getAudios(
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    val id = MediaStore.Audio.Media._ID
    val title = MediaStore.Audio.Media.TITLE
    val artist = MediaStore.Audio.Media.ARTIST
    val album = MediaStore.Audio.Media.ALBUM

    return query2(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection = AUDIO_PROJECTION,
        selection = DEFAULT_AUDIO_SELECTION +
            if (sQuery != null) " AND $id || $title || $artist || $album LIKE ?"
            else "", /// ......... IS FILTER THE INSERT FOR A SEARCH QUERY???? HAS THAT BEEN IT???!?!?!
        args =
            if (sQuery != null) arrayOf("%$sQuery%")
            else null,
        order = order,
        ascending = ascending,
        offset = offset,
        limit = limit,
        transform = { c ->
            domainLogger.info { "$TAG - Get Audios returned: ${c.count} rows + ${c.columnNames}" }
            val temp = List(c.count) {
                c.moveToPosition(it)
                c.toAudio()
            }
            c.close()
            domainLogger.info {
                "HOPEFULLY GETTING A VALUE HERE: \n" +
                        "Audio count returned: ${temp.count()}" +
                        "ID: ${temp[0].id} \n" +
                        "Title: ${temp[0].title} \n" +
                        "Artist: ${temp[0].artist} \n" +
                        "Album: ${temp[0].album} \n" +
                        "Genre: ${temp[0].genre} \n" +
                        "CD TrackNumber: ${temp[0].cdTrackNumber} \n" +
                        "Date Added: ${temp[0].dateAdded} \n" +
                        "Date Modified: ${temp[0].dateModified} \n" +
                        "Disc Number: ${temp[0].discNumber} \n" +
                        "Src TrackNumber: ${temp[0].srcTrackNumber} \n" +
                        "File Path: ${temp[0].path} \n" +
                        "File Size: ${temp[0].size} \n" +
                        "Year: ${temp[0].year} \n"
            }
            temp
        },
    )
}

/**
 * @return list of [Audio] from given bucket/directory
 */
private suspend inline fun ContentResolver.getBucketAudios(
    selection: String,
    args: Array<String>,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> {
    query2(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection = AUDIO_PROJECTION,
        selection = selection,
        args = args,
        order = order,
        ascending = ascending,
        offset = offset,
        limit = limit,
        transform = { c ->
            domainLogger.info { "$TAG - Get Bucket Audios returned: ${c.count}rows + ${c.columnNames}" }
            val result = List(c.count) {
                c.moveToPosition(it)
                c.toAudio()
            }
            c.close()
            return result
        },
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
        "${MediaStore.Audio.Media._ID} == ?",
        arrayOf("$id"),
//        transform = { c ->
//            c.toAudio()
//        }
    ) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toAudio()
        val temp = it.toAudio()
        it.close()
        domainLogger.info {
            "HOPEFULLY GETTING A VALUE HERE: \n" +
                    "ID: ${temp.id} \n" +
                    "Title: ${temp.title} \n" +
                    "Artist: ${temp.artist} \n" +
                    "Album: ${temp.album} \n" +
                    "Genre: ${temp.genre} \n" +
                    "CD TrackNumber: ${temp.cdTrackNumber} \n" +
                    "Date Added: ${temp.dateAdded} \n" +
                    "Date Modified: ${temp.dateModified} \n" +
                    "Disc Number: ${temp.discNumber} \n" +
                    "Src TrackNumber: ${temp.srcTrackNumber} \n" +
                    "File Path: ${temp.path} \n" +
                    "File Size: ${temp.size} \n" +
                    "Year: ${temp.year} \n"
        }
        temp
    }

/**
 * Search for [Audio] on [MediaStore.Audio.Media.DATA], limit 1
 * @param path [String]
 * @return [Cursor] transformed to [Audio]
 */
suspend fun ContentResolver.findAudio(path: String): Audio =
    query2(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        AUDIO_PROJECTION,
        "${MediaStore.Audio.Media.DATA} == ?",
        arrayOf(path),
        limit = 1
    ) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toAudio()
        val temp = it.toAudio()
        it.close()
        temp
    }

/**
 * Search for [Audio] on uri, limit 1
 * @param uri [Uri]
 * @return [Cursor] transformed to [Audio]
 */
private suspend fun ContentResolver.findAudio(uri: Uri): Audio =
    query2(uri, AUDIO_PROJECTION, limit = 1) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toAudio()
        val temp = it.toAudio()
        it.close()
        temp
    }

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
 * Domain model Artist for Resolver
 */
@Stable
data class Artist(
    @JvmField val id: Long,
    @JvmField val name: String,
    //@JvmField val numTracks: Int,
    //@JvmField val numAlbums: Int,
    @JvmField val sort: String,
)

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
private const val DEFAULT_ARTIST_SELECT = "${MediaStore.Audio.Artists._ID} != null"

/**
 * Transform Cursor to type Artist
 */
private fun Cursor.toArtist(): Artist {
    domainLogger.info {
        "Cursor to Artist: \n" +
            "id: ${getLong(0)} \n" +
            "name: ${getString(1) ?: MediaStore.UNKNOWN_STRING}"
    }
    return Artist(
        id = getLong(0),
        name = getString(1) ?: MediaStore.UNKNOWN_STRING,
        //numTracks = getInt(2),
        //numAlbums = getInt(2),
        sort = getString(4) ?: MediaStore.UNKNOWN_STRING,
    )
}

/**
 * @return list of [Artist] from the [MediaStore].
 */
suspend fun ContentResolver.getArtists(
    sQuery: String? = null,
    order: String = MediaStore.Audio.Artists.ARTIST,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Artist> = query2(
    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    projection = ARTIST_PROJECTION,
    selection =
        if (sQuery == null) DUMMY_SELECTION
        else "${MediaStore.Audio.Artists.ARTIST} LIKE ?",
    args =
        if (sQuery == null) null
        else arrayOf("%$sQuery%"),
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        domainLogger.info { "$TAG - Artist count returned: ${c.count}rows + ${c.columnNames}" }
        List(c.count) {
            c.moveToPosition(it)
            c.toArtist()
        }
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
    val like = if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?" else ""
    val selection = "${MediaStore.Audio.Media.ARTIST} == ?" + like
    val args = if (sQuery != null) arrayOf(name, "%$sQuery%") else arrayOf(name)
    domainLogger.info { "$TAG - Get Artist Audios - selection: $selection + args: $args" }
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
    domainLogger.info { "$TAG - Get Artist Audios By ID - selection: $selection + args: $args" }
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

/**
 * Search for [Artist] on name, limit 1
 * @param name [String]
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtist(name: String): Artist {
    return query2(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        ARTIST_PROJECTION,
        "${MediaStore.Audio.Artists.ARTIST} == ?",
        arrayOf(name),
        limit = 1,
    ) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toArtist()
        val temp = it.toArtist()
        it.close()
        temp
    }
}

/**
 * Search for [Artist] on _id, limit 1
 * @param id [Long]
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtist(id: Long): Artist {
    return query2(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        ARTIST_PROJECTION,
        "${MediaStore.Audio.Artists._ID} == ?",
        arrayOf("$id"),
        limit = 1,
    ) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toArtist()
        val temp = it.toArtist()
        it.close()
        temp
    }
}

/**
 * Trying to find artist info based on album Id, limit 1
 * @param albumId [Long]
 * @return [Cursor] transformed to [Artist]?
 */
suspend fun ContentResolver.findArtistByAlbumId(albumId: Long): Artist {
    return query2(
        uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        projection = ARTIST_PROJECTION,
        selection = "${MediaStore.Audio.Albums.ALBUM_ID} == ?",
        args = arrayOf("$albumId"),
        limit = 1,
    ) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toArtist()
        val temp = it.toArtist()
        it.close()
        temp
    }
}

/**
 * External function for content resolver to retrieve artists
 */
fun ContentResolver.artists(
    sQuery: String? = null,
    order: String = MediaStore.Audio.Artists.ARTIST,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI).map {
    getArtists(sQuery, order, ascending)
}

/**
 * External function for content resolver to retrieve artist audios
 */
fun ContentResolver.artist(
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
 * Domain model Album for Resolver
 */
@Stable
data class Album(
    @JvmField val id: Long,
    @JvmField val title: String,
    @JvmField val albumId: Long,
    @JvmField val artist: String,
    @JvmField val artistId: Long,
    //@JvmField val firstYear: Int,
    //@JvmField val lastYear: Int,
    //@JvmField val numTracks: Int,
    //@JvmField val numTracksByArtist: Int,
    @JvmField val sort: String,
)

/**
 * default query selection for media store albums
 */
private const val DEFAULT_ALBUM_SELECT = "${MediaStore.Audio.Albums.ALBUM_ID} != null"

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
        //MediaStore.Audio.Albums.LAST_YEAR,
        //MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        MediaStore.Audio.Albums.DEFAULT_SORT_ORDER,
        //MediaStore.Audio.Albums.ALBUM_ART,//says to use ContentResolver.loadThumbnail?
    )

/**
 * Transform Cursor to type Album
 */
private fun Cursor.toAlbum(): Album {
    domainLogger.info {
        "Cursor to Album: \n" +
            "id: ${getLong(0)} \n" +
            "title: ${getString(1) ?: MediaStore.UNKNOWN_STRING} \n" +
            "albumId: ${getLong(2)}\n" +
            "testing if id is same as albumId: ${getLong(0) == getLong(2)}"
    }
    return Album(
        id = getLong(0),
        title = getString(1) ?: MediaStore.UNKNOWN_STRING,
        albumId = getLong(2),
        artist = getString(3) ?: MediaStore.UNKNOWN_STRING,
        artistId = getLong(4),
        //lastYear = getInt(5),
        //numTracks = getInt(5),
        //numTracksByArtist = getInt(5),
        sort = getString(5) ?: MediaStore.UNKNOWN_STRING,
    )
}

/**
 * @return list of [Album] from the [MediaStore].
 */
suspend fun ContentResolver.getAlbums(
    sQuery: String? = null,
    order: String = MediaStore.Audio.Albums.ALBUM,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Album> {
    return query2(
        uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        projection = ALBUM_PROJECTION,
        selection =
            if (sQuery == null) DUMMY_SELECTION
            else "${MediaStore.Audio.Albums.ALBUM} LIKE ?",
        args =
            if (sQuery == null) null
            else arrayOf("%$sQuery%"),
        order = order,
        ascending = ascending,
        offset = offset,
        limit = limit,
        transform = { c ->
            domainLogger.info { "$TAG - Album count returned: ${c.count}" }
            val temp = List(c.count) {
                c.moveToPosition(it)
                c.toAlbum()
            }.distinctBy { it.id }
            c.close()
            temp
        },
    ) ?: emptyList()
}

/**
 * @return list of [Audio] based on Album
 */
suspend fun ContentResolver.getAlbumAudios(
    title: String, //song title? song title
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
    val args = if (sQuery != null) arrayOf(title, "%$sQuery%") else arrayOf(title)
    return getBucketAudios(selection, args, order, ascending, offset, limit)
}

suspend fun ContentResolver.getAlbumAudiosById(
    id: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> = getBucketAudios(
    selection = "${MediaStore.Audio.Media.ALBUM_ID} == ?",
    args = arrayOf("$id"),
    order,
    ascending,
    offset,
    limit
)

/**
 * Search for [Album] on [MediaStore.Audio.Media._ID]
 * @param id [Long]
 * @return [Cursor] transformed to [Album]
 */
suspend fun ContentResolver.findAlbum(id: Long): Album {
    return query2(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        ALBUM_PROJECTION,
        "${MediaStore.Audio.Albums.ALBUM_ID} == ?",
        arrayOf("$id"),
//        transform = { c ->
//            c.toAlbum()
//        }
    ) {
        domainLogger.info { "Find Album cursor: $it \n" +
                "${it.columnNames} and ${it.columnCount}" }
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toAlbum()
        val temp = it.toAlbum()
        it.close()
        temp
    }
}

/**
 * Search for [Album] on title, limit 1
 * @param title [String]
 * @return [Cursor] transformed to [Album]
 */
suspend fun ContentResolver.findAlbum(title: String): Album {
    return query2(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        ALBUM_PROJECTION,
        "${MediaStore.Audio.Albums.ALBUM} == ?",
        arrayOf(title),
    ) {
        it.moveToFirst()
        //if (!it.moveToFirst()) return@query2 null else it.toAlbum()
        val temp = it.toAlbum()
        it.close()
        temp
    }
}

/**
 * External function for content resolver to retrieve albums
 */
fun ContentResolver.albums(
    sQuery: String? = null,
    order: String = MediaStore.Audio.Albums.ALBUM,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI).map {
    getAlbums(sQuery, order, ascending)
}

/**
 * External function for content resolver to retrieve album audios
 */
fun ContentResolver.album(
    title: String,
    sQuery: String? = null,
    order: String = DEFAULT_AUDIO_ORDER,
    ascending: Boolean = true,
) = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).map {
    getAlbumAudios(title, sQuery, order, ascending)
}


/***********************************************************************************************
 *
 * **********  GENRES SECTION ***********
 *
 **********************************************************************************************/


/**
 * Domain model Genre for Resolver
 */
@Stable
data class Genre(
    @JvmField val id: Long,
    @JvmField val name: String,
    @JvmField val sort: String,
    //@JvmField val numTracks: Int = -1
)

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
 * Transform Cursor to type Genre
 */
private fun Cursor.toGenre(): Genre {
    domainLogger.info {
        "Cursor to Genre: \n" +
            "id: ${getLong(0)} \n" +
            "name: ${getString(1) ?: MediaStore.UNKNOWN_STRING}"
    }
    return Genre(
        id = getLong(0),
        name = getString(1) ?: MediaStore.UNKNOWN_STRING,
        sort = getString(2) ?: MediaStore.UNKNOWN_STRING,
    )
}

/**
 * @return list of [Genre] from the [MediaStore].
 */
suspend fun ContentResolver.getGenres(
    sQuery: String? = null,
    order: String = MediaStore.Audio.Genres.NAME,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Genre> = query2(
    uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    projection = GENRE_PROJECTION,
    selection =
        if (sQuery == null) DUMMY_SELECTION
        else "${MediaStore.Audio.Genres.NAME} LIKE ?",
    args =
        if (sQuery == null) null
        else arrayOf("%$sQuery%"),
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit,
    transform = { c ->
        val temp = List(c.count) {
            c.moveToPosition(it)
            c.toGenre()
        }
        c.close()
        temp
    },
) ?: emptyList()

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
    //maybe for api 30 we can use directly the genre name.
    // find the id.
    val id = query2(
        MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Audio.Genres._ID),
        "${MediaStore.Audio.Genres.NAME} == ?",
        arrayOf(name),
        limit = 1
    ) {
        if (it.count == 0) return emptyList()
        it.moveToPosition(0)
        it.getLong(0)
    } ?: return emptyList()

    // calculate the ids.
    val list = query2(
        MediaStore.Audio.Genres.Members.getContentUri("external", id),
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
    } ?: return emptyList()

    val like = if (sQuery != null) " AND ${MediaStore.Audio.Media.TITLE} LIKE ?" else ""
    return query2(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        AUDIO_PROJECTION,
        DEFAULT_AUDIO_SELECTION + " AND ${MediaStore.Audio.Media._ID} IN ($list)" + like,
        if (sQuery != null) arrayOf("%$sQuery%") else null,
        order,
        ascending,
        offset,
        limit
    ) { c ->
        List(c.count) {
            c.moveToPosition(it)
            val temp = c.toAudio()
            c.close()
            temp
        }
    } ?: emptyList()
}

/**
 * @return list of [Audio] based on Genre
 */
suspend fun ContentResolver.getGenreAudios(
    id: Long,
    order: String = MediaStore.Audio.Media.TITLE,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): List<Audio> = query2(
    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    projection = AUDIO_PROJECTION,
    selection = "${MediaStore.Audio.Media.GENRE_ID} == ?",
    args = arrayOf("$id"),
    order = order,
    ascending = ascending,
    offset = offset,
    limit = limit
) { c ->
    List(c.count) {
        c.moveToPosition(it)
        c.toAudio()
    }
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
fun ContentResolver.genre(
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
 * Returns the content URI for this audio file, using the [MediaStore.Audio.Media.EXTERNAL_CONTENT_URI]
 * and appending the file's unique ID.
 *
 * @return the content URI for the audio file
 */
val Audio.uri
    get() = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

/**
 * Returns the content URI for the album art image of this audio file's album, using the
 * [MediaStore.Images.Media.EXTERNAL_CONTENT_URI] and appending the album ID to the end of the URI.
 *
 * @return the content URI for the album art image of this audio file's album
 */
val Audio.albumUri
    get() = toAlbumArtUri(albumId)

/**
 * Returns the content URI for the album art image of this album, using the [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]
 * and appending the album's unique ID to the end of the URI.
 *
 * @return the content URI for the album art image of this album
 */
val Album.uri
    get() = toAlbumArtUri(id)

/**
 * Returns the content URI for this audio file as a string, using the [uri] property of the audio file.
 *
 * @return the content URI for this audio file as a string
 */
val Audio.key
    get() = uri.toString()

/**
 * Returns a [MediaItem] object that represents this audio file as a playable media item.
 *
 * @return the [MediaItem] object that represents this audio file
 */
val Audio.toMediaItem // the other one had its own class MediaItem that it constructed here
    get() = MediaItem.Builder()
        .setMediaId("$id")
        .setUri(uri)
//.setMediaMetadata() uri, name, artist, "$id", albumUri
//inline val Audio.toMediaItem
//    get() = MediaItem(uri, name, artist, "$id", albumUri)

fun toAlbumArtUri(id: Long): Uri = ContentUris.withAppendedId(Uri.parse(ALBUM_ART_URI), id)
private val ALBUM_ART_URI: String = "content://media/external/audio/albumart"


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