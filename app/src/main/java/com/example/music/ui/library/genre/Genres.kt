package com.example.music.ui.library.genre

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.model.GenreInfo
import com.example.music.ui.shared.GenreListItem
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

private const val TAG = "Library Genres"

/**
 * Overloaded version of lazy list for genreItems
 */
fun LazyListScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    onGenreMoreOptionsClick: (GenreInfo) -> Unit,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Log.i(TAG, "Lazy List START")

    // Item Count, Sort btn, Multi-Select btn row
    item {
        ItemCountAndSortSelectButtons(
            id = R.plurals.genres,
            itemCount = genres.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    // Genre List
    items(
        items = genres
    ) { genre ->
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
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem {
        ItemCountAndSortSelectButtons(
            id = R.plurals.genres,
            itemCount = genres.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
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

@CompLightPreview
@Composable
fun GenreItemPreview() {
    MusicTheme {
        GenreListItem(
            genre = PreviewGenres[0],
            navigateToGenreDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier,
        )
    }
}
