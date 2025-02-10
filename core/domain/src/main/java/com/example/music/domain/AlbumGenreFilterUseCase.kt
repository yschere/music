package com.example.music.domain

import com.example.music.data.repository.GenreRepo
import com.example.music.model.AlbumGenreFilterResult
import com.example.music.model.GenreInfo
import com.example.music.model.asAlbumToSongInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 *  A use case which returns top podcasts and matching episodes in a given Genre.
 */
/**
    TODO: Rework this for returning songs, artists, albums, genres(?) on a given category
 */
class AlbumGenreFilterUseCase @Inject constructor(
    private val genreRepo: GenreRepo
) {
    operator fun invoke(genre: GenreInfo?): Flow<AlbumGenreFilterResult> {
        if (genre == null) {
            return flowOf(AlbumGenreFilterResult())
        }

        val recentAlbumsFlow = genreRepo.sortAlbumsInGenreByTitleAsc(
            genre.id,
            limit = 10
        )

        val songsFlow = genreRepo.songsAndAlbumsInGenre(
            genre.id,
            limit = 20
        )

        // Combine our flows and collect them into the view state StateFlow
        return combine(recentAlbumsFlow, songsFlow) { topAlbums, songs ->
            AlbumGenreFilterResult(
                topAlbums = topAlbums.map { it.asExternalModel() },
                songs = songs.map { it.asAlbumToSongInfo() }
            )
        }
    }
}
