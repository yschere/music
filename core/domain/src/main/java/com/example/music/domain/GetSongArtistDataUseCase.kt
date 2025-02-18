package com.example.music.domain

import com.example.music.data.repository.ArtistRepo
import com.example.music.model.ArtistInfo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.apache.log4j.BasicConfigurator
import javax.inject.Inject

private val logger = KotlinLogging.logger{}

/**
 * Retrieves Flow<[ArtistInfo]> for given
 * @param song [SongInfo]
 * because the artistId from song can be null,
 * it's possible for ArtistInfo to be null. So in this case,
 * it will return flow of empty ArtistInfo
 */
class GetArtistDataUseCase @Inject constructor(
    private val artistRepo: ArtistRepo,
){
    operator fun invoke(song: SongInfo): Flow<ArtistInfo> {
        BasicConfigurator.configure()
        logger.info { "GetArtistDataUseCase start" }
        logger.info { "song.id: ${song.id}; song.artistId: ${song.artistId}"}

        return if (song.artistId != null) {
            artistRepo.getArtistById(song.artistId).map { it.asExternalModel() }
        } else {
            flowOf(ArtistInfo())
        }
    }
}
