package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
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
    private val mediaRepo: MediaRepo
) {
    // use to build SongInfo from an Audio id
    operator fun invoke(songId: Long): Flow<SongInfo> {
        Log.i(TAG, "Fetching Data for single song - start\n" +
                "songId: $songId")

        return mediaRepo.getAudioFlow(songId)
            .map {
                Log.i(TAG, "Found file data for song $songId\n" +
                        "Title: ${it.title}\n" +
                        "Artist: ${it.artist}\n" +
                        "Album: ${it.album}")
                it.asExternalModel()
                    .copy(artworkBitmap = mediaRepo.loadThumbnail(it.uri))
            }
    }

    // use to build list of SongInfo from list of Audio ids
    operator fun invoke(songIds: List<Long>): Flow<List<SongInfo>> {
        Log.i(TAG, "Fetching data for multiple songs - start\n" +
                "songs size: ${songIds.size}")
        return mediaRepo.getAudiosFlow(songIds)
            .map { songList ->
                Log.i(TAG, "Found file data for multiple songs --- ")
                songList.map { song ->
                    Log.i(TAG, "Song: ${song.id} - ${song.title}")
                    song.asExternalModel()
                        .copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
                }
            }
    }
}