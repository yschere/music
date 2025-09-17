package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Song Data V2"

class GetSongDataV2 @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    // use to build SongInfo from an Audio id
    suspend operator fun invoke(songId: Long): SongInfo {
        Log.i(TAG, "Fetching Data for single song - start\n" +
                "songId: $songId")
        val audio = mediaRepo.getAudioFlow(songId).first()
        Log.i(TAG, "Found file data for song $songId\n" +
                "Title: ${audio.title}\n" +
                "Artist: ${audio.artist}\n" +
                "Album: ${audio.album}")
        return audio.asExternalModel()
            .copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
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