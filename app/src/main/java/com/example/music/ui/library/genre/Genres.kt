package com.example.music.ui.library.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
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

/**
 * Genre Items Lazy List Scope Generator.
 * Provides header item with a count of the genres given, and
 * generates a column of genres, with each genre item shown as a row.
 */
/*fun LazyListScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.genres, genres.size, genres.size)
            ) {
                it.value[1].uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(genres) { item ->
        GenreListItem(
            genre = item,
            navigateToGenreDetails = navigateToGenreDetails,
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}*/

/**
 * Genre Items Lazy Grid Scope Generator.
 * Provides header item with a count of the genres given, and
 * generates a column of genres, with each genre item shown as a row.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LazyGridScope.genreItems(
    genres: List<GenreInfo>,
    coroutineScope: CoroutineScope,
    navigateToGenreDetails: (GenreInfo) -> Unit,
) {
    // section1: header
    fullWidthItem {
        // ******** var  for modal remember here
        var showBottomSheet by remember { mutableStateOf(false) }
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
            //Spacer(Modifier.weight(1f,true))

            // sort icon
            IconButton(onClick= { showBottomSheet = true } ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // multi-select icon
            IconButton(onClick={/* filter */}) {
                Icon(
                    imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if(showBottomSheet) {
            LibrarySortSelectionBottomModal(
                onDismissRequest = { showBottomSheet = false },
                coroutineScope = coroutineScope,
                libraryCategory = LibraryCategory.Genres,
            )
        }
    }

    items(
        genres,
        span = { GridItemSpan(maxLineSpan) }
    ) { item ->
        GenreListItem(
            genre = item,
            navigateToGenreDetails = navigateToGenreDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GenreListItem(
    genre: GenreInfo,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) { //outermost layer with padding of 4 for separation between other song list items
        Surface(
            //second most layer, contains onclick action and background color
            shape = MaterialTheme.shapes.large,
            //color = MaterialTheme.colorScheme.background,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { navigateToGenreDetails(genre) }, //this is how navigateToPlayer should be used for each song ListItem, as the passed in onClick event
        ) {
            GenreListItemRow( //design content of song list item
                genre = genre,
                modifier = modifier//.padding(4.dp),
            )
        }
    }
}


@Composable
private fun GenreListItemRow(
    genre: GenreInfo,
    modifier: Modifier = Modifier,
) {
    Row( //third layer, contains layout logic and information for content
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {

        GenreListItemIcon(
            genre = genre.name, //placeholder
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
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                /*if (genre.albumCount != null) { //if showArtistName is true
                    Text(
                        text = quantityStringResource(R.plurals.albums, genre.albumCount!!, genre.albumCount!!),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }*/
                Text(
                    text = quantityStringResource(R.plurals.songs, genre.songCount, genre.songCount),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }

        IconButton( //more options button
            //modifier = Modifier.padding(0.dp),
            onClick = { /* TODO */ }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

//TODO: rename this so its more representative
// stands for the initial letter of the item's name/title
@Composable
private fun GenreListItemIcon(
    genre: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))){
        Text(
            text = genre[0].toString(), //TODO: FOUND, one place where song property is needed that PlayerSong does not need. original code: song.albumTrackNumber from SongInfo with album context, still the same in SongListItem(songinfo, albumInfo)
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
            navigateToGenreDetails = {}
        )
    }
}
