package com.example.music.domain.usecases

import com.example.music.data.repository.ArtistRepo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import com.example.music.domain.util.domainLogger
import javax.inject.Inject

/**
 * Use case to retrieve Flow[ArtistInfo] for given song [SongInfo]
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
        domainLogger.info { "GetSongArtistDataUseCase start:\n" +
                " song.id: ${song.id};\n" +
                " song.artistId: ${song.artistId ?: "null"};"}

        return if (song.artistId != null) {
            artistRepo.getArtistWithExtraInfo(song.artistId).map {
                domainLogger.info { "ArtistWithExtraInfo: \n" +
                        " Artist: ${it.artist};\n" +
                        " Artist songCount: ${it.songCount};\n" +
                        " Artist albumCount: ${it.albumCount};"}
                it.asExternalModel()
            }
        } else {
            domainLogger.info { "ArtistInfo is empty" }
            flowOf(ArtistInfo())
        }
    }
}
