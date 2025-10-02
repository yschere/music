package com.example.music.ui.library.artist

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.music.R
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.ArtistInfo
import com.example.music.ui.shared.ArtistListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.util.StickyHeader
import com.example.music.util.fullWidthItem
import com.example.music.util.stickyHeader

private const val TAG = "Library Artists"

/**
 * A method of defining artist items in a lazy list scope for the Library Screen
 * @param artists defines list of artists to display
 * @param navigateToArtistDetails defines the actions for
 * navigating to the artist's details screen when the item is clicked
 * @param onArtistMoreOptionsClick defines the actions for opening the
 * MoreOptions menu modal when the MoreOptions icon is clicked
 * @param onSortClick defines the actions for clicking on the Sort icon btn
 * @param onSelectClick defines the actions for clicking on the Multi-Select icon btn
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
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Artist List
    items(items = artists) { artist ->
        ArtistListItem(
            artist = artist,
            navigateToArtistDetails = { navigateToArtistDetails(artist) },
            onMoreOptionsClick = { onArtistMoreOptionsClick(artist) },
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

/**
 * A method of defining artist items in a lazy vertical grid scope for the Library Screen
 * @param artists defines list of artists to display
 * @param navigateToArtistDetails defines the actions for
 * navigating to the artist's details screen when the item is clicked
 * @param onArtistMoreOptionsClick defines the actions for opening the
 * MoreOptions menu modal when the MoreOptions icon is clicked
 * @param onSortClick defines the actions for clicking on the Sort icon btn
 * @param onSelectClick defines the actions for clicking on the Multi-Select icon btn
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
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
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
 * A method of defining artist items in a lazy vertical grid scope for the Library Screen
 * with sticky headers
 * @param mappedArtists defines a mapping of the list of artists to display to their header character
 * @param artistCount defines the amount of artists in the library
 * @param state defines the current state of the lazy grid
 * @param navigateToArtistDetails defines the actions for
 * navigating to the artist's details screen when the item is clicked
 * @param onArtistMoreOptionsClick defines the actions for opening the
 * MoreOptions menu modal when the MoreOptions icon is clicked
 * @param onSortClick defines the actions for clicking on the Sort icon btn
 * @param onSelectClick defines the actions for clicking on the Multi-Select icon btn
 */
fun LazyGridScope.artistItemsWithHeaders(
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
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
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
