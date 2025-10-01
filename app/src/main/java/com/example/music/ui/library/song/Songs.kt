package com.example.music.ui.library.song

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.music.R
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.util.fullWidthItem

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

    // Item Count, Sort btn, Multi-Select btn row
    item {
        ItemCountAndSortSelectButtons(
            id = R.plurals.songs,
            itemCount = songs.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Play btn, Shuffle btn row
    item {
        PlayShuffleButtons(
            onPlayClick = onPlayClick,
            onShuffleClick = onShuffleClick,
        )
    }

    // Song List
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

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem {
        ItemCountAndSortSelectButtons(
            id = R.plurals.songs,
            itemCount = songs.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Play btn, Shuffle btn row
    fullWidthItem {
        PlayShuffleButtons(
            onPlayClick = onPlayClick,
            onShuffleClick = onShuffleClick,
        )
    }

    // Song List
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
