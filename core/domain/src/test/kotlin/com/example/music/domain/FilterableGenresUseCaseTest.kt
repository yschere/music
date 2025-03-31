package com.example.music.domain

import com.example.music.data.database.model.Genre
//import com.example.music.data.testing.repository.TestGenreRepo
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FilterableGenresUseCaseTest {

//    private val genresRepo = TestGenreRepo()
    private val testGenres = listOf(
        Genre(0, "Alternative"),
        Genre(1, "Soundtrack"),
        Genre(2, "Pop"),
        Genre(3, "JPop")
    )

//    val useCase = FilterableGenresUseCase(
//        genreRepo = genresRepo
//    )

    @Before
    fun setUp() {
//        genresRepo.setGenres(testGenres)
    }

    @Test
    fun whenNoSelectedGenre_onEmptySelectedGenreInvoked() = runTest {
//        val filterableGenres = useCase(null).first()
//        assertEquals(
//            filterableGenres.genres[0],
//            filterableGenres.selectedGenre
//        )
    }

    @Test
    fun whenSelectedGenre_correctFilterableGenreIsSelected() = runTest {
        val selectedGenre = testGenres[2]
//        val filterableGenres = useCase(selectedGenre.asExternalModel()).first()
//        assertEquals(
//            selectedGenre.asExternalModel(),
//            filterableGenres.selectedGenre
//        )
    }
}
