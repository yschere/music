package com.example.music.ui.library.genre

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
import com.example.music.domain.model.GenreInfo
import com.example.music.ui.shared.GenreListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.util.fullWidthItem

private const val TAG = "Library Genres"

/**
 * Overloaded version of lazy list for genreItems
 */
fun LazyListScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    onGenreMoreOptionsClick: (GenreInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy List START")

    // Item Count, Sort btn, Multi-Select btn row
    item {
        ItemCountAndSortSelectButtons(
            id = R.plurals.genres,
            itemCount = genres.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Genre List
    items(items = genres) { genre ->
        GenreListItem(
            genre = genre,
            navigateToGenreDetails = { navigateToGenreDetails(genre) },
            onMoreOptionsClick = { onGenreMoreOptionsClick(genre) },
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy grid for genreItems
 */
fun LazyGridScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    onGenreMoreOptionsClick: (GenreInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem {
        ItemCountAndSortSelectButtons(
            id = R.plurals.genres,
            itemCount = genres.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Genre List
    items(
        items = genres,
        span = { GridItemSpan(maxLineSpan) }
    ) { genre ->
        GenreListItem(
            genre = genre,
            navigateToGenreDetails =  { navigateToGenreDetails(genre) },
            onMoreOptionsClick = { onGenreMoreOptionsClick(genre) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
