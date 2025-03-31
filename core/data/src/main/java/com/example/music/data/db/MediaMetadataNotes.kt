package com.example.music.zs.mediastore

import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import androidx.media3.common.MediaMetadata
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.util.getColumnIndex
import java.time.Duration
import java.time.OffsetDateTime

/*
public static final Uri EXTERNAL_CONTENT_URI
Added in API level 1
The content:// style URI for the "primary" external storage volume.
 */
/*
data class AudioFiles (
    @PrimaryKey @ColumnInfo(name = "id") val id: Long, //MediaColumns._ID
    @ColumnInfo(name = "title") var title: String = "", //MediaColumns.TITLE
    @ColumnInfo(name = "artist_id") var artistId: Long? = null,
    @ColumnInfo(name = "artist_name") var artistName: String? = null, //MediaColumns.ARTIST
    @ColumnInfo(name = "album_artist") val albumArtistName: String? = null, //MediaColumns.ALBUM_ARTIST
    @ColumnInfo(name = "album_id") var albumId: Long? = null, //MediaColumns.ALBUM_ID
    @ColumnInfo(name = "album_title") var albumTitle: String? = null, //MediaColumns.ALBUM
    @ColumnInfo(name = "genre_id") var genreId: Long? = null,
    @ColumnInfo(name = "genre_name") var genreName: String? = null, //MediaColumns.GENRE
    @ColumnInfo(name = "year") var year: Int? = null, //MediaColumns.YEAR
    @ColumnInfo(name = "album_track_number") var albumTrackNumber: Int? = null, //MediaColumns.TRACK or CD_TRACK_NUMBER
    @ColumnInfo(name = "disc_number") var discNumber: Int, //MediaColumns.DISC_NUMBER
    @ColumnInfo(name = "lyrics") var lyrics: String? = null,
    @ColumnInfo(name = "album_artwork") var artwork: String? = "",
    @ColumnInfo(name = "composer") var composer: String? = null, //MediaColumns.COMPOSER
    @ColumnInfo(name = "date_added") var dateAdded: OffsetDateTime = OffsetDateTime.now(), //MediaColumns.DATE_ADDED (in seconds since 1970)
    @ColumnInfo(name = "date_modified") var dateModified: OffsetDateTime = OffsetDateTime.now(), //MediaColumns.DATE_MODIFIED (in seconds since 1970)
    @ColumnInfo(name = "duration") var duration: Duration = Duration.ZERO, //MediaColumns.DURATION
    @ColumnInfo(name = "absolute_path") var absolutePath: String? = null, //mediaColumns.DATA (data stream of file)
    @ColumnInfo(name = "relative_path") var relativePath: String? = null, //MediaColumns.RELATIVE_PATH -> MediaStore.QUERY_ARG_RELATED_URI
    @ColumnInfo(name = "bitrate") var bitrate: String, //MediaColumns.BITRATE
    @ColumnInfo(name = "size") var fileSize: Long, //MediaColumns.SIZE
    @ColumnInfo(name = "xmp_document_id") var documentId: String, //DOCUMENT_ID
    @ColumnInfo(name = "xmp_metadata") var xmpMetadata: String, //MediaColumns.XMP
    @ColumnInfo(name = "is_drm") var isDRM: Boolean, //IS_DRM
    @ColumnInfo(name = "is_pending") var isPending: Boolean, //IS_PENDING -> MediaStore.QUERY_ARG_MATCH_PENDING
    @ColumnInfo(name = "is_trashed") var isTrashed: Boolean, //IS_TRASHED -> MediaStore.QUERY_ARG_MATCH_TRASHED
    @ColumnInfo(name = "mime_type") var mimeType: String, //MIME_TYPE
    @ColumnInfo(name = "num_tracks") var numTracks: Int, //NUM_TRACKS
)

//when a media attribute is unknown/cannot be retrieved, use MediaStore.UNKNOWN_STRING as default
//to ignore a file for reading: MEDIA_IGNORE_FILENAME
//way to collect an image from a Uri:
// static fun createSource( cr: ContentResolver, uri: android.net.Uri): ImageDecoder.Source
// or
// static fun createSource( assets: AssetManager, fileName: String): ImageDecoder.Source
// or
// static fun createSource( res: Resources, resId: Int): ImageDecoder.Source
// or
// static fun createSource( data: ByteArray ): ImageDecoder.Source

//way to change ImageDecoder.Source to drawable:
// static fun decodeDrawable( src: ImageDecoder.Source ): Drawable

//way to set output size of image/bitmap/drawable:
// fun setTargetSize( width: Int, height: Int ): Unit
*/
class MediaMetadataNotes () {
    //val metadataRetriever = MediaMetadata.Builder()
}

//seems like there is also MediaMetadataRetriever.METADATA equivalents with MediaStore so hope that the rest can be collected thru that
/* //MediaMetadataRetriever metadata key list
    METADATA_KEY_ALBUM : album title
    METADATA_KEY_ALBUMARTIST : artists/performers
    METADATA_KEY_ARTIST : artist
    METADATA_KEY_BITRATE : avg bitrate (bits/sec)
    METADATA_KEY_CD_TRACK_NUMBER : #string describing order of audio src on original recording
    METADATA_KEY_COMPOSER : composer
    METADATA_KEY_DATE : date when created or modified
    METADATA_KEY_DISC_NUMBER : numeric string that describes # set that src comes from
    METADATA_KEY_DURATION : playback duration in ms
    METADATA_KEY_GENRE : content type/genre
    METADATA_KEY_HAS_IMAGE : if key exists, src has still image content
    METADATA_KEY_IMAGE_COUNT : if media has still images, this returns the number of images
    METADATA_KEY_IMAGE_PRIMARY : if media has still images, returns index of primary image
    METADATA_KEY_MIMETYPE : retrieve mimetype of src
    METADATA_KEY_NUM_TRACKS : number of tracks, ie audio, video, text within src, ie mp4 or 3gpp files
    METADATA_KEY_TITLE : src title
    METADATA_KEY_YEAR : year the src was created/modified
 */

/* //MediaMetadataRetriever methods / functions
    close() : release any acquired rsc
    extractMetadata( keyCode: Int ) : call after setDataSource()
    getImageAtIndex ( imageIndex: Int) : open bitmap?
    getPrimaryIndex() : open bitmap?
    release() : releases any acquired rsc
    setDataSource( context: Context!, uri: Uri! ) : open Unit, set data source as content uri
    setDataSource( path: String! ) : open Unit, set data src to path
 */

/* // other MediaMetadata properties
    METADATA_KEY_ALBUM_ART
    METADATA_KEY_ALBUM_ART_URI
    METADATA_KEY_ART
    METADATA_KEY_DISPLAY_DESCRIPTION
    METADATA_KEY_MEDIA_ID
    METADATA_KEY_MEDIA_URI
    METADATA_KEY_RATING
    METADATA_KEY_TRACK_NUMBER
 */

/* // other MediaMetadata methods / functions
    containsKey( key: String! )
    describeContents()
    equals(other: Any?)
    getBitmap( key: String! )
    getDescription()
    getLong( key: String! )
    getString( key: String! )
    getText( key: String! )
    hashCode()
    keySet()
    size()
 */

/* //MediaMetadata Builder -- used to build mediaMeta objects. keys must use appropriate data types
    Builder() : empty builder
    Builder( source: MediaMetadata! ) : create builder using source for initial values
 */

/* //builder methods, key is the metadata key
    build() : MediaMetadata! -- create instance
    putBitmap( key: String!, value: Bitmap! ) : MediaMetadata.Builder! -- put bitmap into metadata
    putLong( key: String!, value: Bitmap! ) : MediaMetadata.Builder! -- put long into metadata
    putRating( key: String!, value: Bitmap! ) : MediaMetadata.Builder! -- put rating into metadata
    putString( key: String!, value: Bitmap! ) : MediaMetadata.Builder! -- put string into metadata
    putText( key: String!, value: Bitmap! ) : MediaMetadata.Builder! -- put charSequence into metadata
 */
/*
data class ArtistFileData (
    @PrimaryKey @ColumnInfo(name = "id") val id: String, //MediaStore.Audio.Albums._ID ////unique id of the row
    @ColumnInfo(name = "album_id") val albumId: String, //MediaStore.Audio.ArtistColumns.ARTIST_KEY
    @ColumnInfo(name = "artist_name") val name: String, //MediaStore.Audio.ArtistColumns.ARTIST
    @ColumnInfo(name = "album_count") val albumCount: String, //MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS
    @ColumnInfo(name = "song_count") val songCount: String, //MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS
)
*/
/*
data class AlbumFileData (
    @PrimaryKey @ColumnInfo(name = "id") val id: Int, //MediaStore.Audio.AlbumColumns.ALBUM_ID
    @ColumnInfo(name = "title") val title: String, //MediaStore.Audio.AlbumColumns.ALBUM
    @ColumnInfo(name = "artwork") val artwork: String? = null, //MediaStore.Audio.AlbumColumns.ALBUM_ART
    @ColumnInfo(name = "artist") val artist: String? = null, //MediaStore.Audio.AlbumColumns.ARTIST
    @ColumnInfo(name = "song_count") val songCount: Int, //MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS
    @ColumnInfo(name = "year") val year: Int, //MediaStore.Audio.AlbumColumns.LAST_YEAR
)*/
/*
private val AUDIO_PROJECTION
    get() = arrayOf(
        MediaStore.Audio.AudioColumns._ID, //0
        MediaStore.Audio.AudioColumns.TITLE, //1
        MediaStore.Audio.AudioColumns.ARTIST_ID, //2
        MediaStore.Audio.AudioColumns.ARTIST, //3
        MediaStore.Audio.AudioColumns.ALBUM_ARTIST, //4
        MediaStore.Audio.AudioColumns.ALBUM_ID, //5
        MediaStore.Audio.AudioColumns.ALBUM, //6
        MediaStore.Audio.AudioColumns.COMPOSER, //7
        MediaStore.Audio.AudioColumns.GENRE_ID, //8
        MediaStore.Audio.AudioColumns.GENRE, //9
        MediaStore.Audio.AudioColumns.YEAR, //10
        MediaStore.Audio.AudioColumns.TRACK, //11
        MediaStore.Audio.AudioColumns.NUM_TRACKS, //12
        MediaStore.Audio.AudioColumns.DISC_NUMBER, //13

        MediaStore.Audio.AudioColumns.DURATION, //14
        MediaStore.Audio.AudioColumns.DATE_ADDED, //15
        MediaStore.Audio.AudioColumns.DATE_MODIFIED, //16
        MediaStore.Audio.AudioColumns.DATA, //17
        MediaStore.Audio.AudioColumns.RELATIVE_PATH, //18
        MediaStore.Audio.AudioColumns.DOCUMENT_ID, //19
        MediaStore.Audio.AudioColumns.BITRATE, //20
        MediaStore.Audio.AudioColumns.SIZE, //21
        MediaStore.Audio.AudioColumns.MIME_TYPE, //22
        MediaStore.Audio.AudioColumns.IS_DRM, //23
        MediaStore.Audio.AudioColumns.INSTANCE_ID, //24
        MediaStore.Audio.AudioColumns.XMP, //25
    )
*/