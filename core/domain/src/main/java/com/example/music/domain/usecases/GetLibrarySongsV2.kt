package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

/** logger info for this class */
private const val TAG = "Get Library Songs V2"

class GetLibrarySongsV2 @Inject constructor(
    private val resolver: MediaRepo
) {
    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<SongInfo> {
        val songsList: List<Audio>
        Log.i(TAG, "Start - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {

            "ARTIST" -> {
                songsList = resolver.getAllSongs(
                    order = MediaStore.Audio.Media.ARTIST,
                    ascending = isAscending,
                )
            }

            "ALBUM" -> {
                songsList = resolver.getAllSongs(
                    order = MediaStore.Audio.Media.ALBUM,
                    ascending = isAscending,
                )
            }

            "DATE_ADDED" -> {
                songsList = resolver.getAllSongs(
                    order = MediaStore.Audio.Media.DATE_ADDED,
                    ascending = isAscending,
                )
            }

            "DATE_LAST_PLAYED" -> {
                songsList = resolver.getAllSongs(
                    order = MediaStore.Audio.Media.DATE_MODIFIED,
                    ascending = isAscending,
                )
            }

            else -> {
                songsList = resolver.getAllSongs(
                    order = MediaStore.Audio.Media.TITLE,
                    ascending = isAscending,
                )
            }
        }

        Log.i(TAG,"********** Library Songs count: ${songsList.size} **********")
        return songsList.map { song ->
            Log.i(TAG, "**** Song: ${song.id} + ${song.title} + ${song.artist} + ${song.album} ****")
            song.asExternalModel()
        }
    }
}