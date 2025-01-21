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

package com.example.music.ui.album

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.data.database.model.SongToAlbum
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.testing.getArtistData
import com.example.music.domain.testing.getAlbumData
import com.example.music.domain.testing.getSongData
import com.example.music.domain.testing.getGenreData
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.SongListItem
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.launch
import com.example.music.designsys.theme.MusicTypography
import com.example.music.designsys.theme.blueDarkSet
import com.example.music.designsys.theme.blueLightSet
import com.example.music.model.AlbumToSongInfo
import com.example.music.model.ArtistInfo
import com.example.music.ui.theme.blueDarkColorSet
import com.example.music.ui.theme.blueLightColorSet
import com.example.music.ui.theme.MusicTheme

@Composable
fun AlbumDetailsScreen(
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
    navigateToPlayer: (SongInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is AlbumUiState.Loading -> {
            AlbumDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        } // screen to show when ui state is in loading
        is AlbumUiState.Ready -> {
            AlbumDetailsScreen(
                album = s.album,
                songs = s.songs,
                onQueueSong = viewModel::onQueueSong,
                navigateToPlayer = navigateToPlayer,
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = modifier,
            )
        } // screen to show when ui state is ready to display
    }
}

@Composable
private fun AlbumDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

@Composable
fun AlbumDetailsScreen(
    album: AlbumInfo,
    songs: List<SongInfo>,
    onQueueSong: (PlayerSong) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) { //base level screen data / coroutine setter / screen component(s) caller
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue) //used to hold the little popup text that appears after an onClick event

    //base layer structure component
    Scaffold(
        modifier = modifier.fillMaxSize(), //says to use max size of screen?
        topBar = { // lambda function? that contains if check for topbar showing back button??
            if (showBackButton) {
                AlbumDetailsTopAppBar(
                    navigateBack = navigateBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        snackbarHost = { // setting the snackbar hoststate to the scaffold
            SnackbarHost(hostState = snackbarHostState)
        },
        //contentColor = MaterialTheme.colorScheme.background,
        //containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) { contentPadding -> //not sure why content padding into content function done in this way
        AlbumDetailsContent(
            album = album,
            songs = songs,
            onQueueSong = {
                coroutineScope.launch { //use the onQueueSong btn onClick to trigger snackbar
                    snackbarHostState.showSnackbar(snackBarText)
                }
                onQueueSong(it)
            },
            navigateToPlayer = navigateToPlayer,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
fun AlbumDetailsContent(
    album: AlbumInfo,
    songs: List<SongInfo>,
    onQueueSong: (PlayerSong) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) { //determines content of details screen

//    Column { ///this iteration has the first header overlapping with the TopAppBar for some reason
//        AlbumDetailsHeaderItem( //header item uses album data to show album info
//            album = album,
//            modifier = Modifier.fillMaxWidth()
//        )
//        AlbumDetailsHeader(
//            album = album,
//            songSize = songs.size,
//            modifier = Modifier.fillMaxWidth()
//        )
//        LazyColumn {
//            items(songs, key = { it.id }) { song -> // for each song in list:
//                SongListItem( //call the SongListItem function to display each one, include the data needed to display item in full,
//                    //and should likely share context from where this call being made incase specific data needs to be shown / not shown
//                    song = song,
//                    album = album,
//                    onClick = navigateToPlayer,
//                    onQueueSong = onQueueSong,
//                    modifier = Modifier.fillMaxWidth(),
//                    showAlbumImage = false,
//                    showSummary = false
//                )
//            }
//        }
//    }


    LazyVerticalGrid( //uses lazy vertical grid to store header and items list below it
        columns = GridCells.Adaptive(362.dp),
        modifier.fillMaxSize()
    ) {
        fullWidthItem { //not sure why fullWidthItem specified
            //AlbumDetailsHeaderItem( //header item uses album data to show album info
                //album = album,
                //modifier = Modifier.fillMaxWidth()
            //)
            AlbumDetailsHeader(
                album = album,
                songSize = songs.size,
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(songs, key = { it.id }) { song -> // for each song in list:
            SongListItem( //call the SongListItem function to display each one, include the data needed to display item in full,
                //and should likely share context from where this call being made incase specific data needs to be shown / not shown
                song = song,
                album = album,
                onClick = navigateToPlayer,
                onQueueSong = onQueueSong,
                modifier = Modifier.fillMaxWidth(),
                isListEditable = false,
                showArtistName = true,
                showAlbumImage = true,
                showAlbumTitle = false,
                showDuration = true,
            )
        }
    }
}

@Composable
fun AlbumDetailsHeaderItem(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(Keyline1)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min(maxImageSize, 148.dp)
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()//.background(MaterialTheme.colorScheme.secondary)
            ) {
                AlbumImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large),
                        //.background(MaterialTheme.colorScheme.onPrimary),
                    //albumImage = album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                    albumImage = 1,
                    contentDescription = album.title
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = album.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    AlbumDetailsHeaderItemButtons(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
fun AlbumDetailsHeader(
    album: AlbumInfo,
    songSize: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(16.dp)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min( maxImageSize, 148.dp )
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.bpicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large)
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = album.title,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = getArtistData(album.albumArtistId!!).name,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (songSize == 1) "$songSize song" else "$songSize songs",
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlbumDetailsHeaderLargeAlbumCover(
    album: AlbumInfo,
    modifier: Modifier = Modifier,
) { //used to show album image as screen header
    Box(modifier = modifier.padding(Keyline1)) {
        Row(modifier.fillMaxWidth()) {
            AlbumImage(
                modifier = Modifier
                    //.size(this.maxWidth / 2)
                    .clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.onPrimary),
                //albumImage = album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                albumImage = 1,
                contentDescription = album.title
            )
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.1f), // container's background color
                    titleContentColor = MaterialTheme.colorScheme.primary // title words color
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {},
//                        colors = IconButtonColors(
//                            MaterialTheme.colorScheme.primary,
//                            MaterialTheme.colorScheme.secondaryContainer,
//                            MaterialTheme.colorScheme.primary,
//                            MaterialTheme.colorScheme.background
//                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "backNavIcon")
                    }
                },
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = album.title
                    )
                }
            )
        },
        bottomBar = {},
//        contentColor = MaterialTheme.colorScheme.primary,
//        containerColor = MaterialTheme.colorScheme.background,

        ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.bpicon),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

}

@Composable
fun AlbumDetailsHeaderItemButtons(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.padding(top = 16.dp)) {
        Button( //following/follow button
            onClick = onClick,
//            colors = ButtonDefaults.buttonColors(
//                //options are containerColor, contentColor, disabledContainerColor, disabledContentColor
//                //elevatedButtonColors() = MaterialTheme.colorScheme.defaultElevatedButtonColors has same set of options, seems like all buttons have these 4 properties
//                containerColor = if (isSystemInDarkTheme())
//                    blueDarkColorSet.tertiaryContainer //TODO
//                    //MaterialTheme.colorScheme.tertiary
//                else
//                    blueLightColorSet.primary //TODO
//                    //MaterialTheme.colorScheme.secondary
//            ),
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = if (false)
                    Icons.Default.Check
                else
                    Icons.Default.Add,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                //tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.cd_more)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsTopAppBar(
    navigateBack: () -> Unit,
    //should include album more options btn action here,
    //pretty sure that button also needs a context driven options set
    modifier: Modifier = Modifier
) {
    TopAppBar( // calls experimental material3 TopAppBar to display top bar
        //properties include: title, modifier, navigationIcon, actions, expandedHeight, windowInsets, colors, scrollBehavior
        title = { },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(id = R.string.cd_back)
                )
            }
        },
        //actions for this screen will either be the search button AND the moreOptions, or just moreOptions, or just search btn
        actions = { //this is intended to be for the icon buttons that will appear Right Aligned
            IconButton(
                onClick = { /* TODO */ },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "More Options"
                )
            }
        },
        modifier = modifier
    )
}

//@Preview (name = "light mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview (name = "dark mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumDetailsHeaderItemPreview() {
    MusicTheme {
        AlbumDetailsHeaderItem(
            album = PreviewAlbums[0],
        )
    }
}

//@Preview
//@Composable
//fun HeaderAlbumCoverPreview() {
//    MusicTheme {
//        AlbumDetailsHeaderLargeAlbumCover(
//            album = PreviewAlbums[0]
//        )
//    }
//}

@Preview
//@Preview (name = "light mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview (name = "dark mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumDetailsScreenPreview() {
    MusicTheme {
        AlbumDetailsScreen(
            album = PreviewAlbums[0],
            songs = PreviewSongs,
            onQueueSong = { },
            navigateToPlayer = { },
            navigateBack = { },
            showBackButton = true,
        )
    }
}


/*
what would it take to make the larger album image details screen?

scaffold:

    header would be the album image scaled to the full width?

    details would be split into content header and content song list

        content header contains the album name, album artist, # songs, artist image(?), more options btn

        content list contains the song list items

            need that to share the context of it being from album details
            so it should show the song.albumTrackNumber, showArtistName = false, showDuration = true, showListEdit = false, showAlbum = false



 */