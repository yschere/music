package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.AlbumRepo
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Changelog:
 *
 * 7/22-23/2025 - Simplified return statement since SongInfo does not have nullable albumId
 */

private const val TAG = "Get Song Album Data Use Case"

/**
 * Use case to retrieve Flow of [AlbumInfo] for given song [SongInfo]
 * @property albumRepo The repository for accessing Album data
 *
 * NOTE: Because the albumId for song can be null,
 * it's possible for AlbumInfo to return as null.
 * So in this case, it will return flow of empty AlbumInfo
 */
class GetSongAlbumDataUseCase @Inject constructor(
    private val albumRepo: AlbumRepo,
){
    operator fun invoke(song: SongInfo): Flow<AlbumInfo> {
        Log.i(TAG, "GetSongAlbumDataUseCase start:\n" +
                " song.id: ${song.id};\n" +
                " song.albumId: ${song.albumId};")

        return albumRepo.getAlbumWithExtraInfo(song.albumId).map {
            Log.i(TAG, "AlbumWithExtraInfo: \n" +
                    " Album: ${it.album};\n" +
                    " Album songCount: ${it.songCount};")
            it.asExternalModel()
        }
    }
}
