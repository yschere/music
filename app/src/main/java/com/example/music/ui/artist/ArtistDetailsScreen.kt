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

package com.example.music.ui.artist

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.AlbumListItem
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.SongListItem
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.launch

// Stateful version of Artist Details Screen
@Composable
fun ArtistDetailsScreen(
    viewModel: ArtistDetailsViewModel = hiltViewModel(),
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is ArtistUiState.Loading -> {
            ArtistDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
        is ArtistUiState.Ready -> {
            ArtistDetailsScreen(
                artist = s.artist,
                albums = s.albums,
                songs = s.songs,
                boxOrRow = true, // TODO change how this works when doing real call and not preview
                //onQueueSong = viewModel::onQueueSong,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ArtistDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

// Stateless version of Artist Details Screen
@Composable
fun ArtistDetailsScreen(
    artist: ArtistInfo,
    albums: List<AlbumInfo>,
    songs: List<SongInfo>,
    boxOrRow: Boolean,
    //onQueueSong: (PlayerSong) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean, //FOUND THE NECESSITY FOR THIS --when the screen is part of a supporting pane scaffold and its used as the supporting pane, it gets passed the value of navigator.isMainPaneHidden()
    //IS THIS HOW THE APP IS ABLE TO TRANSITION WHEN SOMEONE SCROLLS DOWN???
    //SO THAT WHEN THE SCREEN GOES BEYOND THE INITIAL MAYBE MAIN PAIN, THEN THE SHOW BACK BUTTON GETS SET TO TRUE WHICH INVOKES THE REDRAW OF THE SUPPORTING PANE AS THE NEW TOPAPPBAR???
    //BROOOOOOO I'M GETTING IT NOWWWWWW
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (showBackButton) {
                ArtistDetailsTopAppBar(
                    navigateBack = navigateBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        Column(modifier.padding(contentPadding)) {
            //needed to create a column around artist details content so that the header
            // would not get hidden underneath the scaffold's top bar
            ArtistDetailsContent(
                artist = artist,
                albums = albums,
                songs = songs,
                boxOrRow = boxOrRow,
//            onQueueSong = {
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar(snackBarText)
//                }
//                onQueueSong(it)
//            },
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun ArtistDetailsContent(
    artist: ArtistInfo,
    albums: List<AlbumInfo>,
    songs: List<SongInfo>,
    boxOrRow: Boolean,
    //onQueueSong: (PlayerSong) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    /*
        set TRUE -- for box version of album list item, use grid system
        set FALSE -- for row version of album list item, use 1 column lazy vertical grid
    */
    if (boxOrRow) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            //contentPadding = PaddingValues(12.dp),
            //modifier.fillMaxSize()
        ) {
            fullWidthItem {
                ArtistDetailsHeaderItem(
                    artist = artist,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(albums, key = { it.id }) { album ->
                AlbumListItem( //TODO create new AlbumListItem object in shared
                    album = album,
                    onClick = {},
                    //onClick = navigateToAlbumDetails,
                    //onQueueSong = onQueueSong,
                    boxOrRow = boxOrRow,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

    } else {
        //want this for row version for now
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier.fillMaxSize()
        ) {
            fullWidthItem {
                ArtistDetailsHeaderItem(
                    artist = artist,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(albums, key = { it.id }) { album ->
                AlbumListItem( //TODO create new AlbumListItem object in shared
                    album = album,
                    onClick = {},
                    //onClick = navigateToAlbumDetails,
                    //onQueueSong = onQueueSong,
                    boxOrRow = boxOrRow,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun ArtistDetailsHeaderItem(
    artist: ArtistInfo,
    modifier: Modifier = Modifier
) {
    //TODO choose if want 1 image or multi image view for artist header
    // and for the 1 image, should it be the 1st album, or an image for externally of the artist?
    BoxWithConstraints(
        modifier = modifier
        //modifier = modifier.padding(Keyline1)
    ) {
        //val widthConstraint = this.maxWidth
        val maxImageSize = this.maxWidth / 2
        //val imageSize = min(maxImageSize, 148.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            AlbumImage(
//                modifier = Modifier
//                    .size(widthConstraint, 200.dp)
//                    .fillMaxSize()
//                    .clip(MaterialTheme.shapes.large),
//                albumImage = 0,//album.artwork!!,//album.imageUrl or album.artwork when that is fixed
//                contentDescription = "artist Image"
//            )
            Text(
                text = artist.name,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailsTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.cd_back)
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "more options"
                )
            }
        },
        modifier = modifier
    )
}

//@Preview
@Composable
fun ArtistDetailsHeaderItemPreview() {
    ArtistDetailsHeaderItem(
        artist = PreviewArtists[0],
    )
}

@Preview
@Composable
fun ArtistDetailsScreenPreview() {
    //want to map specific album so navigateToAlbumDetails will point to there
    // might not really be a thing for preview to handle though ...
    // which might be why navigateBack and navigateToPlayer were always empty Units
    ArtistDetailsScreen(
        artist = PreviewArtists[0],
        albums = PreviewAlbums,
        songs = PreviewSongs,
        //onQueueSong = { },
        navigateToAlbumDetails = {},
        navigateToPlayer = {},
        navigateBack = {},
        showBackButton = true,
        boxOrRow = true //TODO: set row or box here
    )
}
