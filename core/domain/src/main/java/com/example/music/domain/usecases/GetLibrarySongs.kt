package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.repository.SongSortList
import com.example.music.data.util.FLAG
import javax.inject.Inject

private const val TAG = "Get Library Songs"

class GetLibrarySongs @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    suspend operator fun invoke(
        sortColumn: String,
        isAscending: Boolean
    ): List<SongInfo> {
        var songsList: List<Audio>
        Log.i(TAG, "START --- sortColumn: $sortColumn - isAscending: $isAscending")

        when (sortColumn) {
            SongSortList[0] -> { // "Title"
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.TITLE,
                    ascending = isAscending,
                ).sortedBy { it.title.lowercase() }
                if (!isAscending) songsList = songsList.reversed()
            }

            SongSortList[1] -> { // "Artist"
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.ARTIST,
                    ascending = isAscending,
                ).sortedWith(
                    compareBy<Audio> { it.artist.lowercase() }
                        .thenBy { it.title.lowercase() }
                )
                if (!isAscending) songsList = songsList.reversed()
            }

            SongSortList[2] -> { // "Album"
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.ALBUM,
                    ascending = isAscending,
                ).sortedWith(
                    compareBy<Audio> { it.album.lowercase() }
                        .thenBy { it.title.lowercase() }
                )
                if (!isAscending) songsList = songsList.reversed()
            }

            SongSortList[3] -> { // "Date Added"
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.DATE_ADDED,
                    ascending = isAscending,
                )
            }

            SongSortList[4] -> { // "Date Modified"
                songsList = mediaRepo.getAllAudios(
                    order = MediaStore.Audio.Media.DATE_MODIFIED,
                    ascending = isAscending,
                )
            }

            SongSortList[5] -> { // "Duration"
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
            if (FLAG) Log.i(TAG, "**** Song: ${song.id} + ${song.title} + ${song.artist} + ${song.album} ****")
            song.asExternalModel()
                //.copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
        }
    }
}