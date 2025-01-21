/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.music.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.repository.AlbumStore
import com.example.music.data.repository.GenreStore
import com.example.music.data.repository.SongStore
import com.example.music.domain.AlbumGenreFilterUseCase
import com.example.music.domain.FilterableGenresUseCase
import com.example.music.domain.testing.PreviewAlbumSongs
import com.example.music.model.AlbumGenreFilterResult
import com.example.music.model.AlbumInfo
import com.example.music.model.FilterableGenresModel
import com.example.music.model.GenreInfo
import com.example.music.model.LibraryInfo
import com.example.music.model.asAlbumToSongInfo
import com.example.music.model.asExternalModel
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//is this where all the possible views for home screen goes?
//this is where all the components to create the HomeScreen view are stored/collected
//TODO: list our the components needed for HomeScreen, and attach needed stores, repositories, models, useCases
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val albumGenreFilterUseCase: AlbumGenreFilterUseCase,
    private val filterableGenresUseCase: FilterableGenresUseCase,
    //private val playlistStore: PlaylistStore,
    private val albumStore: AlbumStore,
    //private val artistStore: ArtistStore,
    private val genreStore: GenreStore,
    private val songStore: SongStore,
    private val songPlayer: SongPlayer
) : ViewModel() {
    //TODO: mutable flow state objects that home screen needs should be determined
    // before trying to edit this any further. Not sure what views I want to have
    // on the Home screen just yet.


    // Holds our currently selected album, playlist, genre in the library
    //private val selectedLibraryPodcast = MutableStateFlow<PodcastInfo?>(null)
    private val selectedLibraryAlbum = MutableStateFlow<AlbumInfo?>(null)
    //private val selectedLibraryPlaylist = MutableStateFlow<PlaylistInfo?>(null)
    private val selectedLibraryGenre = MutableStateFlow<GenreInfo?>(null)

    // Holds our currently selected home category
    private val selectedHomeCategory = MutableStateFlow(HomeCategory.Discover) //where tf is home category from

    // Holds the currently available home categories
    private val homeCategories = MutableStateFlow(HomeCategory.entries)

    // Holds our currently selected category
    //private val _selectedCategory = MutableStateFlow<CategoryInfo?>(null)
    private val _selectedGenre = MutableStateFlow<GenreInfo?>(null)
    //private val _selectedAlbum = MutableStateFlow<AlbumInfo?>(null)

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

//    private val subscribedPodcasts = podcastStore.followedPodcastsSortedByLastEpisode(limit = 10)
//        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    //using subscribedPodcasts as base for determining this variable's necessity
    //private val genreId = if (_selectedGenre.value) _selectedGenre.value!!.id else 0

    //TODO: need way to adjust this from genre to album
    private val latestAlbums = albumStore.albumsSortedByLastPlayedSong()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
//    private val albumsInGenre = genreStore.albumsInGenreSortedByLastPlayedSong(genreId)
//        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val state: StateFlow<HomeScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            com.example.music.util.combine(
                homeCategories,
                selectedHomeCategory,
                latestAlbums, //using in place of subscribedPodcasts -- Podcasts to allow for
                // something for selectedHomeCategory.value
                // to compare against in if statement
                refreshing,
                _selectedGenre.flatMapLatest { selectedGenre ->
                    filterableGenresUseCase(selectedGenre)
                },
                _selectedGenre.flatMapLatest {
                    albumGenreFilterUseCase(it)
                },
                latestAlbums.flatMapLatest { albums ->
                    songStore.songsInAlbums(
                        albumIds = albums.map { it.album.id },
                        limit = 10
                    )
                }
            ) {
                homeCategories,
                homeCategory,
                albums,
                refreshing,
                filterableGenres,
                albumGenreFilterResult,
                librarySongs ->

                _selectedGenre.value = filterableGenres.selectedGenre

                // Override selected home category to show 'DISCOVER' if there are no
                // featured podcasts
                selectedHomeCategory.value =
                    if (albums.isEmpty()) HomeCategory.Discover else homeCategory

                HomeScreenUiState(
                    isLoading = refreshing,
                    homeCategories = homeCategories,
                    selectedHomeCategory = homeCategory,
                    featuredAlbums = albums.map { it.asExternalModel() }.toPersistentList(),
                    filterableGenresModel = filterableGenres,
                    albumGenreFilterResult = albumGenreFilterResult,
                    library = librarySongs.asLibrary()
                )
            }.catch { throwable ->
                emit(
                    HomeScreenUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                //podcastsRepository.updatePodcasts(force)
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.GenreSelected -> onGenreSelected(action.genre)
            is HomeAction.HomeCategorySelected -> onHomeCategorySelected(action.homeCategory)
            //is HomeAction.LibraryPlaylistSelected -> onLibraryPlaylistSelected(action.playlist)
            is HomeAction.LibraryAlbumSelected -> onLibraryAlbumSelected(action.album)
            is HomeAction.LibraryGenreSelected -> onLibraryGenreSelected(action.genre)
            is HomeAction.QueueSong -> onQueueSong(action.song)
        }
    }

    private fun onGenreSelected(genre: GenreInfo) {
        _selectedGenre.value = genre
    }

    private fun onHomeCategorySelected(homeCategory: HomeCategory) {
        selectedHomeCategory.value = homeCategory
    }

    private fun onLibraryAlbumSelected(album: AlbumInfo) {
        selectedLibraryAlbum.value = album
    }

    private fun onLibraryGenreSelected(genre: GenreInfo) {
        selectedLibraryGenre.value = genre
    }

//    private fun onLibraryPlaylistSelected(playlist: PlaylistInfo) {
//        selectedLibraryPlaylist.value = playlist
//    }

    private fun onQueueSong(song: PlayerSong) {
        songPlayer.addToQueue(song)
    }
}

//TODO: what does this one need to be if the List<SongInfo> cannot be SongInfo?
// Do I have to make something else?
// Can SongPlayerData or PlaylistToSongInfo be used to compensate?
// Turned it into SongToAlbum so it can contain both song and album data
private fun List<SongToAlbum>.asLibrary(): LibraryInfo =
    LibraryInfo(
        songs = this.map { it.asAlbumToSongInfo() }
    )

/**
 * Enumerated list of Home Screen tab options as Home Screen Categories.
 * --DIFFERENT FROM PODCAST CATEGORY/GENRE--
 * The second half of the Home Screen main pane generates tabs based on these home categories
 * Your Library ... not sure what this is supposed to show yet
 * Discover shows chips of genres in library (currently pulling form domainTesting/PreviewData.kt)
 *  And within the selected genre, shows list of albums within that genre (currently pulling from domainTesting/PreviewData.kt)
 */
enum class HomeCategory {
    Library, Discover//, PlaylistView
}

@Immutable
sealed interface HomeAction {
    data class GenreSelected(val genre: GenreInfo) : HomeAction
    data class HomeCategorySelected(val homeCategory: HomeCategory) : HomeAction
    data class LibraryAlbumSelected(val album: AlbumInfo) : HomeAction
    data class LibraryGenreSelected(val genre: GenreInfo) : HomeAction
    //data class LibraryPlaylistSelected(val playlist: PlaylistInfo) : HomeAction
    data class QueueSong(val song: PlayerSong) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredAlbums: PersistentList<AlbumInfo> = persistentListOf(),
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val filterableGenresModel: FilterableGenresModel = FilterableGenresModel(),
    val albumGenreFilterResult: AlbumGenreFilterResult = AlbumGenreFilterResult(),
    val library: LibraryInfo = LibraryInfo(),
)
