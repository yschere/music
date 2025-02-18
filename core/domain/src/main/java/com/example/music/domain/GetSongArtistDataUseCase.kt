package com.example.music.domain

import com.example.music.data.repository.ArtistRepo
import com.example.music.model.ArtistInfo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Retrieves Flow<[ArtistInfo]> for given
 * @param song [SongInfo]
 * because the artistId from song can be null,
 * it's possible for ArtistInfo to be null. So in this case,
 * it will return flow of empty ArtistInfo
 */
class GetSongArtistDataUseCase @Inject constructor(
    private val artistRepo: ArtistRepo,
){
    operator fun invoke(song: SongInfo): Flow<ArtistInfo> {
        domainLogger.info { "GetSongArtistDataUseCase start" }
        domainLogger.info { "song.id: ${song.id}; song.artistId: ${song.artistId}"}

        return if (song.artistId != null) {
            domainLogger.info { "supposedly song.artistId isn't null if it gets in here! so artistID is: ${song.artistId}" }
            val art = artistRepo.getArtistWithExtraInfo(song.artistId)
            domainLogger.info { "********\n" +
                    "DOES THIS EVER COME BACK FROM THE WAR?\n" +
                    "***********" }
            art.map {
                domainLogger.info { "ArtistWithExtraInfo: \n Artist: ${it.artist}; \n Artist songCount: ${it.songCount};\n Artist albumCount: ${it.albumCount};"}
                it.asExternalModel()
            }
        } else {
            flowOf(ArtistInfo())
        }
    }
}
