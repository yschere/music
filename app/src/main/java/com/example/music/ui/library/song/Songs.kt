package com.example.music.ui.library.song

import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.SongListItem
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

private const val TAG = "Library Songs"

/**
 * Overloaded version of lazy list for songItems
 */
fun LazyListScope.songItems(
    songs: List<SongInfo>,
    navigateToPlayer: (SongInfo) -> Unit,
    onSongMoreOptionsClick: (SongInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy List START")
    //section 1: header
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = """\s[a-z]""".toRegex().replace(
                    quantityStringResource(R.plurals.songs, songs.size, songs.size)
                ) {
                    it.value.uppercase()
                },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp).weight(1f, true)
            )
            //Spacer(Modifier.weight(1f,true))

            // sort icon
            IconButton(onClick = onSortClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // multi-select icon
            IconButton(onClick = onSelectClick) {
                Icon(
                    imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }

    // section 1.5: play and shuffle buttons
    item {
        PlayShuffleButtons(
            onPlayClick = onPlayClick,
            onShuffleClick = onShuffleClick,
        )
    }

    items(
        items = songs,
    ) { song ->
        SongListItem(
            song = song,
            onClick = { navigateToPlayer(song) },
            onMoreOptionsClick = { onSongMoreOptionsClick(song) },
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy grid for songItems
 */
fun LazyGridScope.songItems(
    songs: List<SongInfo>,
    navigateToPlayer: (SongInfo) -> Unit,
    onSongMoreOptionsClick: (SongInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")
    //section 1: header
    fullWidthItem {
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
            IconButton(onClick = onSortClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // multi-select icon
            IconButton(onClick = onSelectClick) {
                Icon(
                    imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }

    // section 1.5: play and shuffle buttons
    fullWidthItem {
        PlayShuffleButtons(
            onPlayClick = onPlayClick,
            onShuffleClick = onShuffleClick,
        )
    }

    items(
        items = songs,
        span = { GridItemSpan(maxLineSpan) }
    ) { song ->
        SongListItem(
            song = song,
            onClick = { navigateToPlayer(song) },
            onMoreOptionsClick = { onSongMoreOptionsClick(song) },
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            modifier = Modifier.fillMaxWidth()
        )
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
        // play btn
        Button(
            onClick = onPlayClick,
            //did have colors set, colors = buttonColors( container -> primary, content -> background )
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
            onClick = onShuffleClick,
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