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

package com.example.music.data.testing.repository

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.repository.PlaylistStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

/**
 * A [PlaylistStore] used for testing.
 */
class TestPlaylistStore : PlaylistStore {

    private val playlistFlow = MutableStateFlow<List<Playlist>>(emptyList())
    //private val playlistCountFlow = MutableStateFlow<List<PlaylistWithSongCount>>(emptyList())
    private val playlistExtraFlow = MutableStateFlow<List<PlaylistWithExtraInfo>>(emptyList())

    //private val sortedPlaylistFlow = MutableStateFlow<List<PlaylistExtraInfo>>(emptyList())
    //need private val for all PlaylistStore properties
    //need override functions for all the PlaylistStore methods

//    private val podcastsInCategoryFlow =
//        MutableStateFlow<Map<Long, List<PodcastWithExtraInfo>>>(emptyMap())
//    private val episodesFromPodcasts =
//        MutableStateFlow<Map<Long, List<EpisodeToPodcast>>>(emptyMap())

    override fun getPlaylistById(id: Long): Playlist? = null

    override fun observePlaylist(name: String): Flow<Playlist> = flowOf()

    override fun mostRecentPlaylists(limit: Int): Flow<List<Playlist>> =
        playlistFlow

    override fun sortPlaylistsBySongCount(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistExtraFlow

    override fun sortPlaylistsByLastPlayed(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistExtraFlow

//    override fun podcastsInCategorySortedByPodcastCount(
//        categoryId: Long,
//        limit: Int
//    ): Flow<List<PodcastWithExtraInfo>> = podcastsInCategoryFlow.map {
//        it[categoryId]?.take(limit) ?: emptyList()
//    }
//
//    override fun episodesFromPodcastsInCategory(
//        categoryId: Long,
//        limit: Int
//    ): Flow<List<EpisodeToPodcast>> = episodesFromPodcasts.map {
//        it[categoryId]?.take(limit) ?: emptyList()
//    }
//
//    override suspend fun addCategory(category: Category): Long = -1
//
//    override suspend fun addPodcastToCategory(podcastUri: String, categoryId: Long) {}
//
//    override fun getCategory(name: String): Flow<Category?> = flowOf()
//
//    /**
//     * Test-only API for setting the list of categories backed by this [TestPlaylistStore].
//     */
//    fun setCategories(categories: List<Category>) {
//        categoryFlow.value = categories
//    }
//
//    /**
//     * Test-only API for setting the list of podcasts in a category backed by this
//     * [TestPlaylistStore].
//     */
//    fun setPodcastsInCategory(categoryId: Long, podcastsInCategory: List<PodcastWithExtraInfo>) {
//        podcastsInCategoryFlow.update {
//            it + Pair(categoryId, podcastsInCategory)
//        }
//    }
//
//    /**
//     * Test-only API for setting the list of podcasts in a category backed by this
//     * [TestPlaylistStore].
//     */
//    fun setEpisodesFromPodcast(categoryId: Long, podcastsInCategory: List<EpisodeToPodcast>) {
//        episodesFromPodcasts.update {
//            it + Pair(categoryId, podcastsInCategory)
//        }
//    }

    override suspend fun addPlaylist(playlist: Playlist) =
        playlistFlow.update { it + playlist }

    override suspend fun isEmpty(): Boolean =
        playlistFlow.first().isEmpty()
}
