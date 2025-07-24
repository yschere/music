package com.example.music.domain.usecases

import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/** logger tag for this class */
private const val TAG = "Get Song Data V2"

class GetSongDataV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    // use to build SongInfo from an Audio id
    operator fun invoke(songId: Long): Flow<SongInfo> {
        domainLogger.info { "$TAG - 1 song - start\n" +
                "songId: $songId" }

        return resolver.getAudioFlow(songId)
            .map {
                it.asExternalModel()
            }
    }

    // use to build list of SongInfo from list of Audio ids
    operator fun invoke(songIds: List<Long>): Flow<List<SongInfo>> {
        domainLogger.info { "$TAG - multi songs - start\n" +
                "songs size: ${songIds.size}" }
        return resolver.getAudiosFlow(songIds)
            .map { songList ->
                songList.map { song ->
                    song.asExternalModel()
                }
            }
    }
}