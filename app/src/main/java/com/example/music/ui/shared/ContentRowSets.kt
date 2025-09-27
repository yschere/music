package com.example.music.ui.shared

import androidx.annotation.PluralsRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.util.AddToPlaylistBtn
import com.example.music.util.CreatePlaylistBtn
import com.example.music.util.MultiSelectBtn
import com.example.music.util.SortBtn
import com.example.music.util.quantityStringResource

/**
 * Content section: Item Count and Sort, Select buttons
 */
@Composable
fun ItemCountAndSortSelectButtons(
    @PluralsRes id: Int,
    itemCount: Int,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(id, itemCount, itemCount)
            ) {
                it.value.uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f, true)
        )
        SortBtn(onClick = onSortClick)
        MultiSelectBtn(onClick = onSelectClick)
    }
}

/**
 * Content section: Item Count and Plus, Sort, Select buttons
 * This is specifically for Library.Playlists and PlaylistDetails screens where createOrAdd
 * defines the intended flow for onPlusClick.
 * On Library.Playlists, onPlusClick is for create playlists flow, so createOrAdd should be TRUE.
 * On PlaylistDetails, onPlusClick is for add songs to playlist flow, so createOrAdd should be FALSE.
 */
@Composable
fun ItemCountAndPlusSortSelectButtons(
    @PluralsRes id: Int,
    itemCount: Int,
    createOrAdd: Boolean,
    onPlusClick: () -> Unit,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(id, itemCount, itemCount)
            ) {
                it.value.uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f, true)
        )

        if (createOrAdd) {
            CreatePlaylistBtn(onClick = onPlusClick)
        } else {
            AddToPlaylistBtn(onClick = onPlusClick)
        }
        SortBtn(onClick = onSortClick)
        MultiSelectBtn(onClick = onSelectClick)
    }
}

/**
 * Content section: Play Items and Shuffle Items buttons
 */
@Composable
fun PlayShuffleButtons(
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = CONTENT_PADDING)
    ) {
        // Play btn
        Button(
            onClick = onPlayClick,
            modifier = Modifier
                .padding(horizontal = CONTENT_PADDING)
                .weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.icon_play)
            )
            Text("PLAY")
        }

        // Shuffle btn
        Button(
            onClick = onShuffleClick,
            modifier = Modifier
                .padding(horizontal = CONTENT_PADDING)
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