/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.domain

import com.example.music.data.database.model.Genre
import com.example.music.data.testing.repository.TestGenreStore
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterableGenresUseCaseTest {

    private val genresStore = TestGenreStore()
    private val testGenres = listOf(
        Genre(0, "Alternative"),
        Genre(1, "Soundtrack"),
        Genre(2, "Pop"),
        Genre(3, "JPop")
    )

    val useCase = FilterableGenresUseCase(
        genreStore = genresStore
    )

    @Before
    fun setUp() {
        genresStore.setGenres(testGenres)
    }

    @Test
    fun whenNoSelectedGenre_onEmptySelectedGenreInvoked() = runTest {
        val filterableGenres = useCase(null).first()
        assertEquals(
            filterableGenres.genres[0],
            filterableGenres.selectedGenre
        )
    }

    @Test
    fun whenSelectedGenre_correctFilterableGenreIsSelected() = runTest {
        val selectedGenre = testGenres[2]
        val filterableGenres = useCase(selectedGenre.asExternalModel()).first()
        assertEquals(
            selectedGenre.asExternalModel(),
            filterableGenres.selectedGenre
        )
    }
}
