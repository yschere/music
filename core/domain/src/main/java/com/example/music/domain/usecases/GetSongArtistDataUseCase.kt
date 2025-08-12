package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.ArtistRepo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.music.domain.util.domainLogger
import javax.inject.Inject

/** Changelog:
 *
 * 7/22-23/2025 - Simplified return statement since SongInfo does not have nullable artistId
 */

private const val TAG = "Get Song Artist Data Use Case"

/**
 * Use case to retrieve Flow of [ArtistInfo] for given song [SongInfo]
 * @property artistRepo [ArtistRepo] The repository for accessing Artist data
 * NOTE: Because the artistId for song can be null,
 * it's possible for ArtistInfo to return as null.
 * So in this case, it will return flow of empty ArtistInfo
 */
class GetSongArtistDataUseCase @Inject constructor(
    private val artistRepo: ArtistRepo,
){
    /**
     * Invoke with single Song to retrieve single [ArtistInfo]
     * @param song [SongInfo] to return flow of [ArtistInfo]
     */
    operator fun invoke(song: SongInfo): Flow<ArtistInfo> {
        Log.i(TAG, "GetSongArtistDataUseCase start:\n" +
                " song.id: ${song.id};\n" +
                " song.artistId: ${song.artistId};")

        return artistRepo.getArtistWithExtraInfo(song.artistId).map {
            Log.i( TAG, "ArtistWithExtraInfo: \n" +
                    " Artist: ${it.artist};\n" +
                    " Artist songCount: ${it.songCount};\n" +
                    " Artist albumCount: ${it.albumCount};")
            it.asExternalModel()
        }
    }
}
