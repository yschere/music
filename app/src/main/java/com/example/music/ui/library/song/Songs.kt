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

/**
 * Overloaded version of lazy list for songItems
 */
fun LazyListScope.songItems(
    songs: List<SongInfo>,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit
) {
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

    items(playerSongs) { item ->
        SongListItem(
            song = item, //TODO: PlayerSong support
            onClick = { navigateToPlayerSong(item) },
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

/**
 * Overloaded version of lazy grid for songItems
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LazyGridScope.songItems(
    songs: List<SongInfo>,
    coroutineScope: CoroutineScope,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit
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
        items = playerSongs,
        span = { GridItemSpan(maxLineSpan) }
    ) { item ->
        SongListItem(
            song = item, //TODO: PlayerSong support
            onClick = { navigateToPlayerSong(item) },
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy grid for songItems
 */
 @OptIn(ExperimentalMaterial3Api::class)
fun LazyGridScope.songItems(
    //songs: List<SongInfo>,
    coroutineScope: CoroutineScope,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit
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
                    .replace(quantityStringResource(R.plurals.songs, playerSongs.size, playerSongs.size)) {
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
        items = playerSongs,
        span = { GridItemSpan(maxLineSpan) }
    ) { item ->
        SongListItem(
            song = item, //TODO: PlayerSong support
            onClick = { navigateToPlayerSong(item) },
            //onMoreOptions = {},
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
