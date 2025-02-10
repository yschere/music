package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Album] related operations.
 */
@Dao
abstract class AlbumsDao : BaseDao<Album> {

    //return ids and titles of all albums
    @Query(
        """
        SELECT * FROM albums
        """
    )
    abstract fun getAllAlbums(): List<Album>
    //TODO: SUGGESTION: place in ViewModel or Repository to update UI with albums

    //return album info based on param album Id
    @Query(
        """
        SELECT * FROM albums WHERE id = :id
        """
    )
    abstract fun getAlbumById(id: Long): Flow<Album>
    //equivalent of PodcastsDao.podcastWithUri

    //return albums by album artist id
    @Transaction
    @Query(
        """
        SELECT albums.* FROM albums
        WHERE albums.album_artist_id = :albumArtistId
        LIMIT :limit
        """
    )
    abstract fun getAlbumsByAlbumArtistId(albumArtistId: Long, limit: Int): Flow<List<Album>>

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums 
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(songs.date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        WHERE albums.id = :albumId
        ORDER BY datetime(date_last_played) DESC
        """
    )
    abstract fun getAlbumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo> //equivalent of PodcastsDao.podcastWithExtraInfo

    @Transaction
    @Query(
        """
        SELECT albums.* from albums
        INNER JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByAlbumArtistAsc(limit: Int): Flow<List<Album>>

    @Transaction
    @Query(
        """
        SELECT albums.* from albums
        INNER JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY artists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByAlbumArtistDesc(limit: Int): Flow<List<Album>>

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        ORDER BY datetime(date_last_played) ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByDateLastPlayedAsc(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByDateLastPlayedDesc( //equivalent of PodcastsDao.podcastsSortedByLastEpisode
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        ORDER BY song_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsBySongCountAsc(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        ORDER BY song_count DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsBySongCountDesc(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    @Query(
        """
        SELECT * FROM albums
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByTitleAsc(limit: Int): Flow<List<Album>>

    @Query(
        """
        SELECT * FROM albums
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByTitleDesc(limit: Int): Flow<List<Album>>

    @Transaction
    @Query(
        """
        SELECT albums.*
        FROM albums
        INNER JOIN (
            SELECT songs.id, songs.album_id, songs.genre_id
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByTitleAsc( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<Album>>

    @Transaction
    @Query(
        """
        SELECT albums.*
        FROM albums
        INNER JOIN (
            SELECT songs.id, songs.album_id, songs.genre_id
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY albums.title DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByTitleDesc( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<Album>>

    //return albums joined to the genresSortedByAlbumCount
    /* //sortAlbumsInGenreByDateLastPlayedAsc not needed atm
    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT songs.album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY datetime(date_last_played) ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> */

    //return albums joined to the genresSortedByAlbumCount
    /* //sortAlbumsInGenreByDateLastPlayedDesc not needed atm
    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT songs.album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByDateLastPlayedDesc( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> */

    //search album titles by param query
    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) as songs ON albums.id = songs.album_id
        WHERE albums.title LIKE '%' || :query || '%'
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun searchAlbumByTitle(query: String, limit: Int): Flow<List<AlbumWithExtraInfo>> //equivalent of PodcastsDao.searchPodcastByTitle

    /* //TODO: not sure if searchAlbumByTitleAndGenre is needed
    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            WHERE genre_id IN (:genreIdList)
            GROUP BY album_id
        ) as songs ON albums.id = songs.album_id
        WHERE albums.title LIKE '%' || :query || '%'
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun searchAlbumByTitleAndGenre(  //equivalent of PodcastsDao.searchPodcastByTitleAndCategory
        query: String,
        genreIdList: List<Long>,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> */

    //return count of albums
    @Query("SELECT COUNT(*) FROM albums")
    abstract suspend fun count(): Int

}
