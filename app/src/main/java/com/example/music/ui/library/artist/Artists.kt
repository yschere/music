package com.example.music.ui.library.artist

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.music.R
import com.example.music.domain.model.ArtistInfo
import com.example.music.ui.shared.ArtistListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.util.StickyHeader
import com.example.music.util.fullWidthItem
import com.example.music.util.stickyHeader

private const val TAG = "Library Artists"

/**
 * Overloaded version of lazy list for artistItems
 */
fun LazyListScope.artistItems(
    artists: List<ArtistInfo>,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    onArtistMoreOptionsClick: (ArtistInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {}
) {
    Log.i(TAG, "Lazy List START")

    // Item Count, Sort btn, Multi-Select btn row
    item {
        ItemCountAndSortSelectButtons(
            id = R.plurals.artists,
            itemCount = artists.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    // Artist List
    items(
        items = artists
    ) { artist ->
        ArtistListItem(
            artist = artist,
            navigateToArtistDetails = { navigateToArtistDetails(artist) },
            onMoreOptionsClick = { onArtistMoreOptionsClick(artist) },
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

/**
 * Overloaded version of lazy grid for artistItems - default grid style
 */
fun LazyGridScope.artistItems(
    artists: List<ArtistInfo>,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    onArtistMoreOptionsClick: (ArtistInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem{
        ItemCountAndSortSelectButtons(
            id = R.plurals.artists,
            itemCount = artists.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    // Artist List
    items(
        items = artists,
        span = { GridItemSpan(maxLineSpan) }
    ) { artist ->
        ArtistListItem(
            artist = artist,
            navigateToArtistDetails = { navigateToArtistDetails(artist) },
            onMoreOptionsClick = { onArtistMoreOptionsClick(artist) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy list for artistItems - sticky headers version
 */
fun LazyGridScope.artistItems(
    mappedArtists: Map<Char,List<ArtistInfo>>,
    artistCount: Int,
    state: LazyGridState,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    onArtistMoreOptionsClick: (ArtistInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid with Sticky Headers START")

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem{
        ItemCountAndSortSelectButtons(
            id = R.plurals.artists,
            itemCount = artistCount,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    mappedArtists.forEach { (letter, artists) ->
        //sticky header would go here, need to get some replacement for it
        stickyHeader(
            state = state,
            key = letter,
        ) {
            StickyHeader(letter.toString())
        }
        items(
            items = artists,
            span = { GridItemSpan(maxLineSpan) }
        ) { artist ->
            ArtistListItem(
                artist = artist,
                navigateToArtistDetails = { navigateToArtistDetails(artist) },
                onMoreOptionsClick = { onArtistMoreOptionsClick(artist) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
