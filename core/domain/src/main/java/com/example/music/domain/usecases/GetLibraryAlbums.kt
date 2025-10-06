package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.model.Album
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.repository.AlbumSortList
import com.example.music.data.util.FLAG
import javax.inject.Inject

private const val TAG = "Get Library Albums"

class GetLibraryAlbums @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    suspend operator fun invoke(
        sortOption: String,
        isAscending: Boolean
    ): List<AlbumInfo> {
        var albumsList: List<Album>
        Log.i(TAG, "START --- sortOption: $sortOption - isAscending: $isAscending")

        when (sortOption) {
            AlbumSortList[0] -> { //"Title"
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.ALBUM,
                    ascending = isAscending
                ).sortedBy { it.title.lowercase() }
                if (!isAscending) albumsList = albumsList.reversed()
            }

            AlbumSortList[1] -> { //"Artist"
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.ARTIST,
                    ascending = isAscending
                ).sortedWith(
                    compareBy<Album> { it.artist.lowercase() }
                        .thenBy { it.title.lowercase() }
                )
                if (!isAscending) albumsList = albumsList.reversed()
            }

            AlbumSortList[2] -> { //"Song Count"
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                    ascending = isAscending
                ).sortedWith(
                    compareBy<Album> { it.numTracks }
                        .thenBy { it.title.lowercase() }
                )
                if (!isAscending) albumsList = albumsList.reversed()
            }

            AlbumSortList[3] -> { //"Year"
                albumsList = mediaRepo.getAllAlbums(
                    order = MediaStore.Audio.Albums.ALBUM,
                    ascending = isAscending
                ).sortedWith(
                    compareBy<Album, Int?>(nullsLast(), { it.lastYear })
                        .thenBy { it.title.lowercase() }
                )
                if (!isAscending) albumsList = albumsList
                    .sortedWith(
                        compareByDescending<Album, Int?> (nullsFirst(),{ it.lastYear })
                            .thenByDescending { it.title.lowercase() }
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