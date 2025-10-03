package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Library Songs"

class GetLibrarySongs @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<SongInfo> {
        val songsList: List<Audio>
        Log.i(TAG, "START - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            "TITLE" -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.TITLE,
                    ascending = isAscending,
                )
            }

            "ARTIST" -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.ARTIST,
                    ascending = isAscending,
                )
            }

            "ALBUM" -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.ALBUM,
                    ascending = isAscending,
                )
            }

            "DATE_ADDED" -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.DATE_ADDED,
                    ascending = isAscending,
                )
            }

            "DATE_LAST_PLAYED" -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.DATE_MODIFIED,
                    ascending = isAscending,
                )
            }

            "DURATION" -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.DURATION,
                    ascending = isAscending,
                )
            }

            else -> {
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.TITLE,
                    ascending = isAscending,
                )
            }
        }

        Log.i(TAG,"********** Library Songs count: ${songsList.size} **********")
        return songsList.map { song ->
            Log.i(TAG, "**** Song: ${song.id} + ${song.title} + ${song.artist} + ${song.album} ****")
            song.asExternalModel()
                //.copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
        }
    }
}