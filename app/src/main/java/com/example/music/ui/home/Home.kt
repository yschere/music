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

@file:OptIn(ExperimentalFoundationApi::class)

package com.example.music.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.domain.testing.PreviewAlbumSongs
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewGenres
import com.example.music.model.AlbumGenreFilterResult
import com.example.music.model.AlbumInfo
import com.example.music.model.FilterableGenresModel
import com.example.music.model.LibraryInfo
import com.example.music.model.SongInfo
import com.example.music.ui.album.AlbumDetailsScreen
import com.example.music.ui.album.AlbumDetailsViewModel
import com.example.music.ui.home.discover.discoverItems
import com.example.music.ui.home.library.libraryItems
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.quantityStringResource
import com.example.music.util.radialGradientScrim
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.slf4j.logger
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import org.apache.log4j.BasicConfigurator
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isMainPaneHidden(): Boolean {
    return scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
}

/**
 * Copied from `calculatePaneScaffoldDirective()` in [PaneScaffoldDirective], with modifications to
 * only show 1 pane horizontally if either width or height size class is compact.
 */
//@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun calculateScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val verticalSpacerSize: Dp
    if (windowAdaptiveInfo.windowSizeClass.isCompact) {
        // Window width or height is compact. Limit to 1 pane horizontally.
        maxHorizontalPartitions = 1
        verticalSpacerSize = 0.dp
    } else {
        when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            WindowWidthSizeClass.MEDIUM -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            else -> {
                maxHorizontalPartitions = 2
                verticalSpacerSize = 24.dp
            }
        }
    }
    val maxVerticalPartitions: Int
    val horizontalSpacerSize: Dp

    if (windowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        horizontalSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        horizontalSpacerSize = 0.dp
    }

    val defaultPanePreferredWidth = 360.dp

    return PaneScaffoldDirective(
        maxHorizontalPartitions,
        verticalSpacerSize,
        maxVerticalPartitions,
        horizontalSpacerSize,
        defaultPanePreferredWidth,
        getExcludedVerticalBounds(windowAdaptiveInfo.windowPosture, verticalHingePolicy)
    )
}

/**
 * Copied from `getExcludedVerticalBounds()` in [PaneScaffoldDirective] since it is private.
 */
//@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> {
    return when (hingePolicy) {
        HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
        HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
        HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
        else -> emptyList()
    }
}

private val logger = KotlinLogging.logger{}

/**
 * Composable for the Main Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (SongInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    BasicConfigurator.configure()
    val homeScreenUiState by viewModel.state.collectAsStateWithLifecycle()
    val uiState = homeScreenUiState
    Box {
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToPlayer = navigateToPlayer,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            HomeScreenError(onRetry = viewModel::refresh)
        }
    }
}

//keep as is
@Composable
private fun HomeScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(id = R.string.an_error_has_occurred),
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry_label))
            }
        }
    }
}

//keep as is
//@Preview
@Composable
fun HomeScreenErrorPreview() {
    MusicTheme {
        HomeScreenError(onRetry = {})
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun HomeScreenReady(
    uiState: HomeScreenUiState,
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (SongInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator<String>(
        scaffoldDirective = calculateScaffoldDirective(currentWindowAdaptiveInfo())
    )
    BackHandler(enabled = navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    Surface {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                HomeScreen(
                    //for now so I can see anything load in emulator, gonna put some preview values
                    windowSizeClass = windowSizeClass,
                    isLoading = uiState.isLoading,
                    //for now so I can see anything load in emulator
                    featuredAlbums = PreviewAlbums.toPersistentList(),
                    homeCategories = HomeCategory.entries,
                    selectedHomeCategory = HomeCategory.Library,
                    filterableGenresModel = FilterableGenresModel(
                        genres = PreviewGenres,
                        selectedGenre = PreviewGenres.firstOrNull()
                    ),
                    albumGenreFilterResult = AlbumGenreFilterResult(
                        topAlbums = PreviewAlbums,
                        songs = PreviewAlbumSongs
                    ),
                    library = LibraryInfo(PreviewAlbumSongs),
                    onHomeAction = {},
                    navigateToAlbumDetails = {},
                    /*
                    featuredAlbums = uiState.featuredAlbums, //featuredPlaylists = uiState.featuredPlaylists,
                    homeCategories = uiState.homeCategories,
                    selectedHomeCategory = uiState.selectedHomeCategory,
                    filterableGenresModel = uiState.filterableGenresModel,
                    albumGenreFilterResult = uiState.albumGenreFilterResult,
                    library = uiState.library,
                    onHomeAction = viewModel::onHomeAction,
                    navigateToAlbumDetails = { //was navigateToPodcastDetails,
                        // then navigateToPlaylistDetails but uiState
                        // doesn't share playlistInfo, but it can
                        // share genre, album, song infos
                        // could make this navigateToAlbumDetails. would need an albumInfo that
                        // can encapsulate the type of properties needed that Podcast has
                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.id.toString())
                    },
                     */
                    navigateToPlayer = navigateToPlayer,
                    modifier = Modifier.fillMaxSize()
                )
            },
            //TODO: when navigateTo___Details determined, need to update this. it's based on PodcastDetailsViewModel
            supportingPane = {
                val albumId = navigator.currentDestination?.content
                if (!albumId.isNullOrEmpty()) {
                    val albumDetailsViewModel = hiltViewModel<AlbumDetailsViewModel, AlbumDetailsViewModel.AlbumDetailsViewModelFactory>(
                            key = albumId
                        ) { it.create(albumId.toLong()) }
                    //TODO: change the podcastDetails section to handle playlist Details view
                    //TODO: or to handle album/artist Details
                    AlbumDetailsScreen(
                        viewModel = albumDetailsViewModel,
                        navigateToPlayer = navigateToPlayer,
                        navigateBack = {
                            if (navigator.canNavigateBack()) {
                                navigator.navigateBack()
                            }
                        },
                        showBackButton = navigator.isMainPaneHidden(),
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable for Home Screen's Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    var queryText by remember {
        mutableStateOf("")
    }
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = queryText,
                    onQueryChange = { queryText = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    enabled = true,
                    placeholder = {
                        Text(stringResource(id = R.string.cd_search))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.cd_account)
                        )
                    },
                    interactionSource = null,
                    modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier
                )
            },
            expanded = false,
            onExpandedChange = {}
        ) {}
    }
}

/**
 * Composable for Home Screen's Background.
 */
@Composable
private fun HomeScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(MaterialTheme.colorScheme.primary)//.copy(alpha = 0.9f))
        )
        content()
    }
}

/**
 * Composable for Home Screen and its properties needed to render the
 * components of the page.
 */
@Composable
private fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    featuredAlbums: PersistentList<AlbumInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableGenresModel: FilterableGenresModel,
    albumGenreFilterResult: AlbumGenreFilterResult,
    library: LibraryInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    // Effect that changes the home category selection when there are no subscribed podcasts
    LaunchedEffect(key1 = featuredAlbums) {
        if (featuredAlbums.isEmpty()) {
            onHomeAction(HomeAction.HomeCategorySelected(HomeCategory.Discover))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    HomeScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                Column {
                    HomeAppBar(
                        isExpanded = windowSizeClass.isCompact,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.Transparent
        ) { contentPadding ->
            // Main Content
            val snackBarText = stringResource(id = R.string.song_added_to_your_queue)
            val showHomeCategoryTabs = featuredAlbums.isNotEmpty() && homeCategories.isNotEmpty()
            HomeContent(
                showHomeCategoryTabs = showHomeCategoryTabs,
                featuredAlbums = featuredAlbums,
                selectedHomeCategory = selectedHomeCategory,
                homeCategories = homeCategories,
                filterableGenresModel = filterableGenresModel,
                albumGenreFilterResult = albumGenreFilterResult,
                library = library,
                modifier = Modifier.padding(contentPadding),
                onHomeAction = { action ->
                    if (action is HomeAction.QueueSong) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(snackBarText)
                        }
                    }
                    onHomeAction(action)
                },
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
            )
        }
    }
}

@Composable
private fun HomeContent(
    showHomeCategoryTabs: Boolean,
    featuredAlbums: PersistentList<AlbumInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableGenresModel: FilterableGenresModel,
    albumGenreFilterResult: AlbumGenreFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
) {
    logger.info { "Home Content function start" }
    val pagerState = rememberPagerState { featuredAlbums.size }
    LaunchedEffect(pagerState, featuredAlbums) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                val album = featuredAlbums.getOrNull(it)
                //onHomeAction(HomeAction.LibraryAlbumSelected(album))
                album?.let { it1 -> HomeAction.LibraryAlbumSelected(it1) }
                    ?.let { it2 -> onHomeAction(it2) }
                //TODO: fix HomeAction's LibraryPodcastSelected fun
            }
    }

    HomeContentGrid(
        showHomeCategoryTabs = showHomeCategoryTabs,
        pagerState = pagerState,
        featuredAlbums = featuredAlbums,
        selectedHomeCategory = selectedHomeCategory,
        homeCategories = homeCategories,
        filterableGenresModel = filterableGenresModel,
        albumGenreFilterResult = albumGenreFilterResult,
        library = library,
        modifier = modifier,
        onHomeAction = onHomeAction,
        navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToPlayer = navigateToPlayer,
    )
}

@Composable
private fun HomeContentGrid(
    showHomeCategoryTabs: Boolean,
    pagerState: PagerState,
    featuredAlbums: PersistentList<AlbumInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableGenresModel: FilterableGenresModel,
    albumGenreFilterResult: AlbumGenreFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(362.dp),
        modifier = modifier.fillMaxSize()
    ) {
        if (featuredAlbums.isNotEmpty()) {
            fullWidthItem {
                //TODO: adjust FollowedPodcastItem and all FollowedPodcast / subscribedPodcast viewmodels to support Genres
                //FollowedPodcastItem(
                FeaturedAlbumItem(
                    pagerState = pagerState,
                    items = featuredAlbums,
                    //onPodcastUnfollowed = { onHomeAction(HomeAction.PodcastUnfollowed(it)) },
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        if (showHomeCategoryTabs) {
            fullWidthItem {
                Row {
                    HomeCategoryTabs(
                        homeCategories = homeCategories,
                        selectedHomeCategory = selectedHomeCategory,
                        showHorizontalLine = false,
                        onHomeCategorySelected = { onHomeAction(HomeAction.HomeCategorySelected(it)) },
                        modifier = Modifier.width(240.dp)
                    )
                }
            }
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                libraryItems(
                    library = library,
                    navigateToPlayer = navigateToPlayer,
                    onQueueSong = { onHomeAction(HomeAction.QueueSong(it)) }
                )
            }

            HomeCategory.Discover -> {
                discoverItems(
                    filterableGenresModel = filterableGenresModel,
                    albumGenreFilterResult = albumGenreFilterResult,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToPlayer = navigateToPlayer,
                    onGenreSelected = { onHomeAction(HomeAction.GenreSelected(it)) },
//                    onTogglePodcastFollowed = {
//                        onHomeAction(HomeAction.TogglePodcastFollowed(it))
//                    },
                    onQueueSong = { onHomeAction(HomeAction.QueueSong(it)) },
                )
            }
        }
    }
}

@Composable
private fun FeaturedAlbumItem(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        FeaturedAlbums(
            pagerState = pagerState,
            items = items,
            navigateToAlbumDetails = navigateToAlbumDetails,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeCategoryTabs(
    homeCategories: List<HomeCategory>,
    selectedHomeCategory: HomeCategory,
    onHomeCategorySelected: (HomeCategory) -> Unit,
    showHorizontalLine: Boolean,
    modifier: Modifier = Modifier,
) {
    if (homeCategories.isEmpty()) {
        return
    }

    val selectedIndex = homeCategories.indexOfFirst { it == selectedHomeCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        modifier = modifier,
        divider = {
            if (showHorizontalLine) {
                HorizontalDivider()
            }
        }
    ) {
        homeCategories.forEachIndexed { index, homeCategory ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onHomeCategorySelected(homeCategory) },
                text = {
                    Text(
                        text = when (homeCategory) {
                            HomeCategory.Library -> stringResource(R.string.home_library)
                            HomeCategory.Discover -> stringResource(R.string.home_discover)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

@Composable
private fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

private val FEATURED_ALBUM_IMAGE_SIZE_DP = 160.dp

@Composable
private fun FeaturedAlbums(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: Using BoxWithConstraints is not quite performant since it requires 2 passes to compute
    // the content padding. This should be revisited once a carousel component is available.
    // Alternatively, version 1.7.0-alpha05 of Compose Foundation supports `snapPosition`
    // which solves this problem and avoids this calculation altogether. Once 1.7.0 is
    // stable, this implementation can be updated.
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding = (this.maxWidth - FEATURED_ALBUM_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_ALBUM_IMAGE_SIZE_DP)
        ) { page ->
            val album = items[page]
            FeaturedAlbumCarouselItem(
                albumImage = 1,//album.artwork!!,
                albumTitle = album.title,
                //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navigateToAlbumDetails(album)
                    }
            )
        }
    }
}

@Composable
private fun FeaturedAlbumCarouselItem(
    albumTitle: String,
    //albumImage: String,
    albumImage: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Box(
            Modifier
                .size(FEATURED_ALBUM_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = albumImage,
                contentDescription = albumTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        Text(
            text = albumTitle,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }

        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
private fun HomeAppBarPreview() {
    MusicTheme {
        HomeAppBar(
            isExpanded = false,
        )
    }
}

private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

//@DevicePreviews
@Preview
@Composable
private fun PreviewHome() {
    MusicTheme {
        HomeScreen(
            windowSizeClass = CompactWindowSizeClass,
            isLoading = false,
            featuredAlbums = PreviewAlbums.toPersistentList(),
            homeCategories = HomeCategory.entries,
            selectedHomeCategory = HomeCategory.Library,
            filterableGenresModel = FilterableGenresModel(
                genres = PreviewGenres,
                selectedGenre = PreviewGenres.firstOrNull()
            ),
            albumGenreFilterResult = AlbumGenreFilterResult(
                topAlbums = PreviewAlbums,
                songs = PreviewAlbumSongs
            ),
            library = LibraryInfo(PreviewAlbumSongs),
            onHomeAction = {},
            navigateToAlbumDetails = {},
            navigateToPlayer = {},
        )
    }
}

//@Preview
@Composable
private fun PreviewPodcastCard() {
    MusicTheme {
        FeaturedAlbumCarouselItem(
            modifier = Modifier.size(128.dp),
            albumTitle = "Listof",
            albumImage = 0,
            //onUnfollowedClick = {}
        )
    }
}
