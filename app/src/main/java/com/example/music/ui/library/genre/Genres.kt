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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.shared.LibrarySortSelectionBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope

private const val TAG = "Library Genres"

/**
 * Genre Items Lazy List Scope Generator.
 * Provides header item with a count of the genres given, and
 * generates a column of genres, with each genre item shown as a row.
 */
fun LazyListScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    onGenreMoreOptionsClick: (GenreInfo) -> Unit,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Log.i(TAG, "Lazy List START")
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = """\s[a-z]""".toRegex().replace(
                    quantityStringResource(R.plurals.genres, genres.size, genres.size)
                ) {
                    it.value.uppercase()
                },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp).weight(1f, true)
            )
        }

        // Sort btn
        IconButton(onClick = onSortClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(R.string.icon_sort),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // Multi-Select btn
        IconButton(onClick = onSelectClick) {
            Icon(
                imageVector = Icons.Filled.Checklist,
                contentDescription = stringResource(R.string.icon_multi_select),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }

    items(genres) { genre ->
        GenreListItem(
            genre = genre,
            navigateToGenreDetails = navigateToGenreDetails,
            onMoreOptionsClick = { onGenreMoreOptionsClick(genre) },
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

/**
 * Genre Items Lazy Grid Scope Generator.
 * Provides header item with a count of the genres given, and
 * generates a column of genres, with each genre item shown as a row.
 */
fun LazyGridScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    onGenreMoreOptionsClick: (GenreInfo) -> Unit,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Log.i(TAG, "Lazy Grid START")
    // section1: header
    fullWidthItem {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = """\s[a-z]""".toRegex().replace(
                    quantityStringResource(R.plurals.genres, genres.size, genres.size)
                ) {
                    it.value.uppercase()
                },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp).weight(1f,true)
            )

            // Sort btn
            IconButton(onClick = onSortClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // Multi-Select btn
            IconButton(onClick = onSelectClick) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }

    items(
        genres,
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

@Composable
private fun GenreListItem(
    genre: GenreInfo,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer, //MaterialTheme.colorScheme.background,
            onClick = { navigateToGenreDetails(genre) },
        ) {
            GenreListItemRow(
                genre = genre,
                onMoreOptionsClick = onMoreOptionsClick,
                modifier = modifier//.padding(4.dp),
            )
        }
    }
}

@Composable
private fun GenreListItemRow(
    genre: GenreInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {

        GenreListItemIcon(
            genre = genre.name,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Column(modifier.weight(1f)) {
            Text(
                text = genre.name,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                Text(
                    text = quantityStringResource(R.plurals.songs, genre.songCount, genre.songCount),
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }

        // More Options btn
        IconButton(onClick = onMoreOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * Composable for drawing the Genre Item Icon to contain the first initial of a genre's name
 */
@Composable
private fun GenreListItemIcon(
    genre: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Text(
            text = genre[0].toString(),
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxSize().padding(vertical = 15.dp),
        )
    }
}

@Preview
@Composable
fun PreviewGenreItem() {
    MusicTheme {
        GenreListItem(
            genre = PreviewGenres[0],
            navigateToGenreDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier,
        )
    }
}
