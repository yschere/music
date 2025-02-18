package com.example.music.domain

import com.example.music.data.repository.AlbumRepo
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Retrieves Flow<[AlbumInfo]> for given
 * @param song [SongInfo]
 * because the albumId from song can be null,
 * it's possible for AlbumInfo to be null. So in this case,
 * it will return flow of empty AlbumInfo
 */
class GetSongAlbumDataUseCase @Inject constructor(
    private val albumRepo: AlbumRepo,
){
    operator fun invoke(song: SongInfo): Flow<AlbumInfo> {
        domainLogger.info { "GetSongAlbumDataUseCase start" }
        domainLogger.info { "song.id: ${song.id}; song.albumId: ${song.albumId}"}

        return if (song.albumId != null) {
            albumRepo.getAlbumWithExtraInfo(song.albumId).map { it.asExternalModel() }
        } else {
            flowOf(AlbumInfo())
        }
    }
}
