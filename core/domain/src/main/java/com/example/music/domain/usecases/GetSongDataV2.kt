package com.example.music.domain.usecases

import com.example.music.domain.player.model.PlayerSong
import com.example.music.domain.player.model.audioToPlayerSong
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Song Data V2"

class GetSongDataV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    // use to build PlayerSong for SongPlayer + media controller
    operator fun invoke(songId: Long): Flow<PlayerSong> {
        domainLogger.info { "$TAG - 1 song - start\n" +
                "songId: $songId" }

        return resolver.getAudioFlow(songId)
            .map {
                it.audioToPlayerSong()
            }
    }

    // use to build list of PlayerSongs
    operator fun invoke(songIds: List<Long>): Flow<List<PlayerSong>> {
        domainLogger.info { "$TAG - multi songs - start\n" +
                "songs size: ${songIds.size}" }
        return resolver.getAudiosFlow(songIds)
            .map { songList ->
                songList.map { song ->
                    song.audioToPlayerSong()
                }
            }
    }
}