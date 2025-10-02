package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Library Albums"

class GetLibraryAlbums @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    suspend operator fun invoke( sortOption: String, isAscending: Boolean ): List<AlbumInfo> {
        val albumsList: List<Album>
        Log.i(TAG, "START - sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            "TITLE" -> {
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.ALBUM,
                    ascending = isAscending
                )
            }

            "ARTIST" -> {
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.ARTIST,
                    ascending = isAscending
                )
            }

            "SONG_COUNT" -> {
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                    ascending = isAscending
                )
            }

            "YEAR" -> {
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.LAST_YEAR,
                    ascending = isAscending
                )
            }

            else -> {
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.ALBUM,
                    ascending = isAscending
                )
            }
        }

        Log.i(TAG, "********** Library Albums count: ${albumsList.size} **********")
        return albumsList.map { album ->
            Log.i(TAG, "**** Album: ${album.id} + ${album.title} ****")
            album.asExternalModel()
        }
    }
}