package com.example.music.ui.library.playlist

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.music.R
import com.example.music.domain.model.PlaylistInfo
import com.example.music.ui.shared.ItemCountAndPlusSortSelectButtons
import com.example.music.ui.shared.PlaylistItem
import com.example.music.util.fullWidthItem

private const val TAG = "Library Playlists"

/**
 * Overloaded version of lazy list for playlistItems
 */
fun LazyListScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    onPlaylistMoreOptionsClick: (PlaylistInfo) -> Unit,
    onPlusClick: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy List START")

    item {
        ItemCountAndPlusSortSelectButtons(
            id = R.plurals.playlists,
            itemCount = playlists.size,
            createOrAdd = true, // create playlists btn
            onPlusClick = onPlusClick,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    items(
        items = playlists
    ) { playlist ->
        PlaylistItem(
            playlist = playlist,
            navigateToPlaylistDetails = { navigateToPlaylistDetails(playlist) },
            onMoreOptionsClick = { onPlaylistMoreOptionsClick(playlist) },
            cardOrRow = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy grid for playlistItems
 */
fun LazyGridScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    onPlaylistMoreOptionsClick: (PlaylistInfo) -> Unit,
    onPlusClick: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Plus btn, Sort btn, Multi-Select btn row
    fullWidthItem {
        ItemCountAndPlusSortSelectButtons(
            id = R.plurals.playlists,
            itemCount = playlists.size,
            createOrAdd = true, // create playlists btn
            onPlusClick = onPlusClick,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    // Playlist List
    items(
        items = playlists,
        span = { GridItemSpan(1) }
    ) { playlist ->
        PlaylistItem(
            playlist = playlist,
            navigateToPlaylistDetails = { navigateToPlaylistDetails(playlist) },
            onMoreOptionsClick = { onPlaylistMoreOptionsClick(playlist) },
            cardOrRow = true,
        )
    }
}
