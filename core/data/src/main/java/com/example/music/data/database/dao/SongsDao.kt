package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Song] related operations.
 */
@Dao
abstract class SongsDao : BaseDao<Song> {

    @Query(
        """
        SELECT * FROM songs
        """
    )
    abstract fun getAllSongs(): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE id = :id
        """
    )
    abstract fun getSongById(id: Long): Flow<Song>

    @Query(
        """
        SELECT * FROM songs
        WHERE title = :title
        """
    )
    abstract fun getSongByTitle(title: String): Flow<Song>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN albums ON songs.album_id = albums.id
        WHERE songs.id = :songId
        """
    )
    abstract fun getSongAndAlbumBySongId(songId: Long): Flow<SongToAlbum> //equivalent of episodeAndPodcast
    //TODO: update when SongToAlbum fixed

    @Query(
        """
        SELECT * FROM songs
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByTitleAsc(limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByTitleDesc(limit: Int): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        JOIN artists ON songs.artist_id = artists.id
        ORDER BY artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByArtistAsc(limit: Int): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        JOIN artists ON songs.artist_id = artists.id
        ORDER BY artists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByArtistDesc(limit: Int): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        JOIN albums ON songs.album_id = albums.id
        ORDER BY albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByAlbumAsc(limit: Int): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        JOIN albums ON songs.album_id = albums.id
        ORDER BY albums.title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByAlbumDesc(limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        ORDER BY date_added ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateAddedAsc(limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        ORDER BY date_added DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateAddedDesc(limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        ORDER BY date_last_played ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateLastPlayedAsc(limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateLastPlayedDesc(limit: Int): Flow<List<Song>>

    //variant of getSongsByArtistId that returns mapping of artist to list of songs
//    @Transaction
//    @Query(
//        """
//        SELECT songs.* FROM songs
//        INNER JOIN (SELECT * FROM artists) as artists on artists.id = songs.artist_id
//        WHERE artists.id = :artistId
//        ORDER BY artists.name ASC
//        LIMIT :limit
//        """
//    )
//    abstract fun getSongsByArtistIdV2(artistId: Long, limit: Int): Map<Artist, List<Song>>

    //query to retrieve songs for a specified artist using param artistId
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id = :artistId
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistId(artistId: Long, limit: Int): Flow<List<Song>>

    //retrieve list of songs and albums within list of albumIds
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id IN (:artistIds)
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistIds(artistIds: List<Long>, limit: Int): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id = :artistId
        ORDER BY title ASC
        """
    )
    abstract fun sortSongsInArtistBySongTitleAsc(artistId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id = :artistId
        ORDER BY title DESC
        """
    )
    abstract fun sortSongsInArtistBySongTitleDesc(artistId: Long): Flow<List<Song>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM songs
        INNER JOIN ( SELECT albums.id, albums.title FROM albums ) AS albums ON songs.album_id = albums.id
        WHERE songs.artist_id = :artistId
        ORDER BY albums.title ASC
        """
    )
    abstract fun sortSongsInArtistByAlbumTitleAsc(artistId: Long): Flow<List<Song>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM songs
        INNER JOIN ( SELECT albums.id, albums.title FROM albums ) AS albums ON songs.album_id = albums.id
        WHERE songs.artist_id = :artistId
        ORDER BY albums.title DESC
        """
    )
    abstract fun sortSongsInArtistByAlbumTitleDesc(artistId: Long): Flow<List<Song>>

//    //variant of getSongsByArtistId that returns mapping of artist to list of songs
//    @Transaction
//    @Query(
//        """
//        SELECT songs.*, albums.title, albums.album_artist_id, albums.track_total, albums.disc_number, albums.disc_total, albums.artwork FROM songs
//        JOIN albums on albums.id = songs.album_id
//        WHERE albums.id = :albumId
//        LIMIT :limit
//        """
//    )
//    abstract fun getSongsByAlbumIdV2(albumId: Long, limit: Int): Map<Album, List<Song>>

    //retrieve list of songs by their albumId
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        LIMIT :limit
        """
    )
    abstract fun getSongsByAlbumId(albumId: Long, limit: Int): Flow<List<Song>> //equivalent for episodesForPodcastUri

    //return list of songs and album using specified albumId
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN (
            SELECT id as albumId, title, track_total, disc_number, disc_total FROM albums
        ) ON songs.album_id = albumId
        WHERE songs.album_id = :albumId
        ORDER BY songs.title ASC
        """
    )
    abstract fun getSongsAndAlbumByAlbumId(albumId: Long): Flow<List<SongToAlbum>>
    //TODO: fix this when SongToAlbum fixed

    //retrieve list of songs and albums within list of albumIds
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id IN (:albumIds)
        LIMIT :limit
        """
    )
    abstract fun getSongsAndAlbumsByAlbumIds(albumIds: List<Long>, limit: Int): Flow<List<SongToAlbum>>
    //equivalent for episodesForPodcasts
    // TODO: fix this when SongToAlbum fixed

    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        ORDER BY title ASC
        """
    )
    abstract fun sortSongsInAlbumBySongTitleAsc(albumId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        ORDER BY title DESC
        """
    )
    abstract fun sortSongsInAlbumBySongTitleDesc(albumId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE songs.album_id = :albumId
        ORDER BY songs.album_track_number ASC
        """
    )
    abstract fun sortSongsInAlbumByTrackNumberAsc(albumId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE songs.album_id = :albumId
        ORDER BY songs.album_track_number DESC
        """
    )
    abstract fun sortSongsInAlbumByTrackNumberDesc(albumId: Long): Flow<List<Song>>

    //query to retrieve songs for a specified composer using param composerId
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        LIMIT :limit
        """
    )
    abstract fun getSongsByComposerId(composerId: Long, limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInComposerByTitleAsc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInComposerByTitleDesc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    //query to retrieve songs for a specified genre using param genreId
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        LIMIT :limit
        """
    )
    abstract fun getSongsByGenreId(genreId: Long, limit: Int): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByTitleAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByTitleDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY date_last_played ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /* @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id IN (:genreIds)
        GROUP BY genre_id
        ORDER BY date_last_played ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenresByDateLastPlayedAsc(genreIds: List<Long>, limit: Int): Flow<List<Song>> */

    /* @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id IN (:genreIds)
        GROUP BY genre_id
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenresByDateLastPlayedDesc(genreIds: List<Long>, limit: Int): Flow<List<Song>> */

    /* @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT songs.*, albums.title/*, albums.album_artist_id, albums.year, albums.track_total, albums.disc_number, albums.disc_total, albums.artwork*/ FROM songs
        JOIN albums on albums.id = songs.album_id
        WHERE songs.genre_id = :genreId
        ORDER BY songs.date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsAndAlbumsInGenreByDateLastPlayed( //equivalent of categoriesDao.episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>> */
    //TODO: fix SongToAlbum so that it is actually either all columns of song and album,
    // or is both objects as a whole

    //return songs count
    @Query("SELECT COUNT(*) FROM songs")
    abstract suspend fun count(): Int
}
