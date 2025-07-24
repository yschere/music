package com.example.music.domain.usecases

import com.example.music.data.repository.AlbumRepo
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.music.domain.util.domainLogger
import javax.inject.Inject

/** Changelog:
 *
 * 7/22-23/2025 - Simplified return statement since SongInfo does not have nullable albumId
 */

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
                " song.albumId: ${song.albumId};"}

        return albumRepo.getAlbumWithExtraInfo(song.albumId).map {
            domainLogger.info { "AlbumWithExtraInfo: \n" +
                    " Album: ${it.album};\n" +
                    " Album songCount: ${it.songCount};"}
            it.asExternalModel()
        }
    }
}
