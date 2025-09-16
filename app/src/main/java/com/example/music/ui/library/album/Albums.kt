package com.example.music.ui.library.album

import android.util.Log
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.model.AlbumInfo
import com.example.music.ui.shared.AlbumItemCard
import com.example.music.ui.shared.AlbumListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

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

@Preview
@Composable
fun AlbumItemPreviewRow() {
    MusicTheme {
        AlbumListItem(
            album = PreviewAlbums[0],
            navigateToAlbumDetails = {},
            onMoreOptionsClick = {},
            cardOrRow = false,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumItemPreviewCard() {
    MusicTheme {
        Row {
            AlbumListItem(
                album = PreviewAlbums[0],
                navigateToAlbumDetails = {},
                onMoreOptionsClick = {},
                cardOrRow = true,
            )

            AlbumListItem(
                album = PreviewAlbums[3],
                navigateToAlbumDetails = {},
                onMoreOptionsClick = {},
                cardOrRow = true,
            )
        }
    }
}