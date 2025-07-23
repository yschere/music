package com.example.music.ui.library.song

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.PlayerSong
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.shared.LibrarySortSelectionBottomModal
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 */

/**
 * Overloaded version of lazy list for songItems
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.songItems(
    songs: List<SongInfo>,
    coroutineScope: CoroutineScope,
    navigateToPlayer: (SongInfo) -> Unit,
) {

    //section 1: header
    item {
        // ******** var  for modal remember here
        var showBottomSheet by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = """\s[a-z]""".toRegex()
                    .replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
                        it.value.uppercase()
                    },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp).weight(1f, true)
            )
            //Spacer(Modifier.weight(1f,true))

            // sort icon
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // multi-select icon
            IconButton(onClick = {/* filter */ }) {
                Icon(
                    imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if (showBottomSheet) {
            LibrarySortSelectionBottomModal(
                onDismissRequest = { showBottomSheet = false },
                coroutineScope = coroutineScope,
                libraryCategory = LibraryCategory.Songs,
            )
        }
    }

    item {
        Row {
            // shuffle btn
            Button(
                onClick = {
                    /*coroutineScope.launch {
                        sheetState.hide()
                        showThemeSheet = false
                    }*/
                },
                colors = buttonColors(
                    //containerColor = MaterialTheme.colorScheme.primary,
                    //contentColor = MaterialTheme.colorScheme.background,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .padding(10.dp).weight(0.5f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = stringResource(R.string.icon_shuffle)
                )
                Text("SHUFFLE")
            }

            // play btn
            Button(
                onClick = {
                    /*coroutineScope.launch {
                        sheetState.hide()
                        showThemeSheet = false
                    }*/
                },
                colors = buttonColors(
                    //containerColor = MaterialTheme.colorScheme.primaryContainer,
                    //contentColor = MaterialTheme.colorScheme.background,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .padding(10.dp).weight(0.5f)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.icon_play)
                )
                Text("PLAY")
            }
        }
    }

    items(
        items = songs,
        //span = { GridItemSpan(maxLineSpan) }
    ) { song ->
        SongListItem(
            song = song,
            onClick = { navigateToPlayer(song) },
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            modifier = Modifier.fillMaxWidth()
        )
    }

    /* // original lazy list version
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
                it.value.uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(songs) { item ->
        SongListItem(
            song = item,
            onClick = { navigateToPlayer(item) },
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }*/
}

/**
 * Overloaded version of lazy grid for songItems
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LazyGridScope.songItems(
    songs: List<SongInfo>,
    coroutineScope: CoroutineScope,
    navigateToPlayer: (SongInfo) -> Unit,
) {

    //section 1: header
    fullWidthItem {
        // ******** var  for modal remember here
        var showBottomSheet by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = """\s[a-z]""".toRegex()
                    .replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
                        it.value.uppercase()
                    },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp).weight(1f,true)
            )
            //Spacer(Modifier.weight(1f,true))

            // sort icon
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // multi-select icon
            IconButton(onClick = {/* filter */ }) {
                Icon(
                    imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if(showBottomSheet) {
            LibrarySortSelectionBottomModal(
                onDismissRequest = { showBottomSheet = false },
                coroutineScope = coroutineScope,
                libraryCategory = LibraryCategory.Songs,
            )
        }
    }

    fullWidthItem {
        PlayShuffleButtons(
            onPlayClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
            onShuffleClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
        )
    }

    items(
        items = songs,
        span = { GridItemSpan(maxLineSpan) }
    ) { song ->
        //Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
        SongListItem(
            song = song,
            onClick = { navigateToPlayer(song) },
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            modifier = Modifier.fillMaxWidth()
        )
        //}
    }
}

/**
 * Content section 1.5: play and shuffle buttons
 */
@Composable
private fun PlayShuffleButtons(
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.padding(bottom = 8.dp)) {
        //Row(Modifier.padding(bottom = 8.dp)) { // original version for screens that don't have carousel / don't need to remove horizontal padding on lazyVerticalGrid
        // play btn
        Button(
            onClick = onPlayClick, //what is the thing that would jump start this step process. would it go thru the viewModel??
            //step 1: regardless of shuffle being on or off, set shuffle to off
            //step 2: prepare the mediaPlayer with the new queue of items in order from playlist
            //step 3: set the player to play the first item in queue
            //step 4: navigateToPlayer(first item)
            //step 5: start playing
            /*coroutineScope.launch {
                sheetState.hide()
                showThemeSheet = false
            }*/
            //did have colors set, colors = buttonColors( container -> primary, content -> background ) // coroutineScope.launch { sheetState.hide() showThemeSheet = false },
            shape = MusicShapes.small,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.icon_play)
            )
            Text("PLAY")
        }

        // shuffle btn
        Button(
            onClick = onShuffleClick, //what is the thing that would jump start this step process
            //step 1: regardless of shuffle being on or off, set shuffle to on
            //step 2?: confirm the shuffle type
            //step 3: prepare the mediaPlayer with the new queue of items shuffled from playlist
            //step 4: set the player to play the first item in queue
            //step 5: navigateToPlayer(first item)
            //step 6: start playing
            //needs to take the songs in the playlist, shuffle the
            /*coroutineScope.launch {
                sheetState.hide()
                showThemeSheet = false
            }*/
            //did have colors set, colors = buttonColors( container -> primary, content -> background )
            shape = MusicShapes.small,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.icon_shuffle)
            )
            Text("SHUFFLE")
        }
    }
}