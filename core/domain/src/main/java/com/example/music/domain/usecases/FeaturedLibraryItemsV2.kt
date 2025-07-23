package com.example.music.domain.usecases

import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.MediaRepo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.example.music.domain.util.domainLogger

private const val TAG = "FeaturedLibraryItemsV2"

class FeaturedLibraryItemsV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterV2> {
        domainLogger.info { "$TAG - using MediaStore - start" }

        //val albumItems = resolver.getAllAlbums()
        val albumItems = resolver.mostRecentAlbums(5)
        domainLogger.info { "$TAG - album item == $albumItems" }

        //val mediaItems = resolver.getAllSongs()
        val mediaItems = resolver.mostRecentSongs(10)
        domainLogger.info { "$TAG - media item == $mediaItems" }

        // val recentPlaylistsFlow = playlistRepo date last played desc limit 5
        // val recentSongsFlow == songRepo date created desc limit 10

        return combine(
            mediaItems,
            albumItems
        ) { mediaIds, albumIds ->
            domainLogger.info { mediaIds }
            domainLogger.info { albumIds }
            FeaturedLibraryItemsFilterV2(
                recentAlbums = albumIds.map { albumId ->
                    domainLogger.info { "AlbumID - PLEASE IS THERE SOMETHING IN HERE: $albumId" }
//                    albumId.asExternalModel()
                    resolver.getAlbum(albumId).asExternalModel()
                },
                recentlyAddedSongs = mediaIds.map { mediaId ->
                    domainLogger.info { "SongID - PLEASE IS THERE SOMETHING IN HERE: $mediaId" }
//                    mediaId.asExternalModel()
                    resolver.getAudio(mediaId).asExternalModel()
                },
            )
        }
    }
}

data class FeaturedLibraryItemsFilterV2 (
    val recentAlbums: List<AlbumInfo> = emptyList(),
    val recentlyAddedSongs: List<SongInfo> = emptyList()
)