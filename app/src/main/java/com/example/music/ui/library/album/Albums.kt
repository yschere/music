package com.example.music.ui.library.album

import android.util.Log
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.music.R
import com.example.music.domain.model.AlbumInfo
import com.example.music.ui.shared.AlbumListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.util.fullWidthItem

private const val TAG = "Library Albums"

/**
 * Overloaded version of lazy list for albumItems
 */
fun LazyListScope.albumItems(
    albums: List<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    onAlbumMoreOptionsClick: (AlbumInfo) -> Unit,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit
) {
    Log.i(TAG, "Lazy List START")

    // Item Count, Sort btn, Multi-Select btn row
    item {
        ItemCountAndSortSelectButtons(
            id = R.plurals.albums,
            itemCount = albums.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    // Album List
    items(
        items = albums
    ) { album ->
        AlbumListItem(
            album = album,
            navigateToAlbumDetails = { navigateToAlbumDetails(album) },
            onMoreOptionsClick = { onAlbumMoreOptionsClick(album) },
            cardOrRow = false,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

/**
 * Overloaded version of lazy grid for albumItems
 */
fun LazyGridScope.albumItems(
    albums: List<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    onAlbumMoreOptionsClick: (AlbumInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem {
        ItemCountAndSortSelectButtons(
            id = R.plurals.albums,
            itemCount = albums.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
        )
    }

    // Album List
    items(
        items = albums,
        span = { GridItemSpan(1) }
    ){ album ->
        AlbumListItem(
            album = album,
            navigateToAlbumDetails = { navigateToAlbumDetails(album) },
            onMoreOptionsClick = { onAlbumMoreOptionsClick(album) },
            cardOrRow = true,
        )
    }
}
