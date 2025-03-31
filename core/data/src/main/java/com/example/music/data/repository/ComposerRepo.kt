package com.example.music.data.repository

import com.example.music.data.database.dao.ComposersDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Composer
import com.example.music.data.database.model.ComposerWithExtraInfo
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Interface for [ComposersDao] abstract functions
 */
interface ComposerRepo {

    fun getAllComposers(): Flow<List<Composer>>

    fun getComposerById(id: Long): Flow<Composer>

    fun getComposerByName(name: String): Flow<Composer>

    fun getComposerWithExtraInfo(id: Long): Flow<ComposerWithExtraInfo>

    fun sortComposersByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ComposerWithExtraInfo>>
    fun sortComposersByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ComposerWithExtraInfo>>

    fun sortComposersBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ComposerWithExtraInfo>>
    fun sortComposersBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ComposerWithExtraInfo>>

    fun getSongsByComposerId(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInComposerByTitleAsc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>
    fun sortSongsInComposerByTitleDesc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInComposerByDateLastPlayedAsc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>
    fun sortSongsInComposerByDateLastPlayedDesc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun searchComposersByName(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ComposerWithExtraInfo>>

    suspend fun addComposer(composer: Composer)

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean
}

/**
* A data repository for [Composer] instances.
*/
class ComposerRepoImpl(
    private val composerDao: ComposersDao,
    private val songDao: SongsDao,
) : ComposerRepo {

    override fun getAllComposers(): Flow<List<Composer>> =
        composerDao.getAllComposers()

    override fun getComposerById(id: Long): Flow<Composer> =
        composerDao.getComposerById(id)

    override fun getComposerByName(name: String): Flow<Composer> =
        composerDao.getComposerByName(name)

    override fun getComposerWithExtraInfo(id: Long): Flow<ComposerWithExtraInfo> =
        composerDao.getComposerWithExtraInfo(id)

    override fun sortComposersByNameAsc(limit: Int): Flow<List<ComposerWithExtraInfo>> =
        composerDao.sortComposersByNameAsc(limit)

    override fun sortComposersByNameDesc(limit: Int): Flow<List<ComposerWithExtraInfo>> =
        composerDao.sortComposersByNameDesc(limit)

    override fun sortComposersBySongCountAsc(limit: Int): Flow<List<ComposerWithExtraInfo>> =
        composerDao.sortComposersBySongCountAsc(limit)

    override fun sortComposersBySongCountDesc(limit: Int): Flow<List<ComposerWithExtraInfo>> =
        composerDao.sortComposersBySongCountDesc(limit)

    override fun getSongsByComposerId(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.getSongsByComposerId(composerId, limit)

    override fun sortSongsInComposerByTitleAsc(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInComposerByTitleAsc(composerId, limit)

    override fun sortSongsInComposerByTitleDesc(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInComposerByTitleDesc(composerId, limit)

    override fun sortSongsInComposerByDateLastPlayedAsc(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInComposerByDateLastPlayedAsc(composerId, limit)

    override fun sortSongsInComposerByDateLastPlayedDesc(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInComposerByDateLastPlayedDesc(composerId, limit)

    override fun searchComposersByName(
        query: String,
        limit: Int
    ): Flow<List<ComposerWithExtraInfo>> = composerDao.searchComposersByName(query, limit)

    /**
     * Add a new [Composer] to this store.
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addComposer(composer: Composer) {
        composerDao.insert(composer)
    }

    override suspend fun count(): Int = composerDao.count()

    override suspend fun isEmpty(): Boolean = composerDao.count() == 0
}
