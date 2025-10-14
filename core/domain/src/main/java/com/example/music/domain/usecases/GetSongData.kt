package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Song Data"

class GetSongData @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    // use to build SongInfo from an Audio id
    suspend operator fun invoke(songId: Long): SongInfo {
        Log.i(TAG, "Fetching Data for single song - START\n" +
            "songId: $songId")
        val audio = mediaRepo.getAudioFlow(songId).first()
        if (FLAG) Log.i(TAG, "Found file data for song $songId\n" +
            "Title: ${audio.title}\n" +
            "Artist: ${audio.artist}\n" +
            "Album: ${audio.album}")
        return audio.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
    }

    // use to build list of SongInfo from list of Audio ids
    operator fun invoke(songIds: List<Long>): Flow<List<SongInfo>> {
        Log.i(TAG, "Fetching data for multiple songs - START\n" +
            "songs size: ${songIds.size}")
        return mediaRepo.getAudiosFlow(songIds)
            .map { songList ->
                songList.map { song ->
                    if (FLAG) Log.i(TAG, "Found file data for song ${song.id}\n" +
                        "Title: ${song.title}\n" +
                        "Artist: ${song.artist}\n" +
                        "Album: ${song.album}")
                    song.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
                }
            }
    }
}