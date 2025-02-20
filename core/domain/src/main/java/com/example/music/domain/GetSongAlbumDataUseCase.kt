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
 * Use case to retrieve Flow[AlbumInfo] for given song [SongInfo]
 * @property albumRepo [AlbumRepo] The repository for accessing Album data
 * NOTE: Because the albumId for song can be null,
 * it's possible for AlbumInfo to return as null.
 * So in this case, it will return flow of empty AlbumInfo
 */
class GetSongAlbumDataUseCase @Inject constructor(
    private val albumRepo: AlbumRepo,
){
    /**
     * Invoke with single Song to retrieve single [AlbumInfo]
     * @param song [SongInfo] to return flow of [AlbumInfo]
     */
    operator fun invoke(song: SongInfo): Flow<AlbumInfo> {
        domainLogger.info { "GetSongAlbumDataUseCase start:\n" +
                " song.id: ${song.id};\n" +
                " song.albumId: ${song.albumId ?: "null"};"}

        return if (song.albumId != null) {
            albumRepo.getAlbumWithExtraInfo(song.albumId).map {
                domainLogger.info { "AlbumWithExtraInfo: \n" +
                        " Album: ${it.album};\n" +
                        " Album songCount: ${it.songCount};"}
                it.asExternalModel()
            }
        } else {
            domainLogger.info { "AlbumInfo is empty" }
            flowOf(AlbumInfo())
        }
    }
}
