package com.example.music.domain.usecases

import com.example.music.data.repository.ArtistRepo
import com.example.music.data.repository.GenreRepo
import com.example.music.data.repository.SongRepo
import com.example.music.domain.model.ArtistGenreFilterResult
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.asExternalModel
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
class ArtistGenreFilterUseCase @Inject constructor(
    private val genreRepo: GenreRepo,
    private val artistRepo: ArtistRepo,
    private val songRepo: SongRepo,
) {
    operator fun invoke(genre: GenreInfo?): Flow<ArtistGenreFilterResult> {
        if (genre == null) {
            return flowOf(ArtistGenreFilterResult())
        }

        val artistsInGenreFlow = artistRepo.sortArtistsBySongCountDesc(
            limit = 10
        )

        val songsInGenreFlow = songRepo.getSongsByGenreId(
            genre.id,
            limit = 20
        )

        // Combine our flows and collect them into the view state StateFlow
        return combine(artistsInGenreFlow, songsInGenreFlow) { topArtists, songs ->
            ArtistGenreFilterResult(
                topArtists = topArtists.map { it.asExternalModel() },
                songs = songs.map { it.asExternalModel() }
            )
        }
    }
}
