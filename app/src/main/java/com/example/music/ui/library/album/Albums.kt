package com.example.music.ui.library.album

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.model.AlbumInfo
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.shared.LibrarySortSelectionBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope

/**
 * Album Items Lazy List Scope Generator.
 * Provides header item with a count of the albums given, and
 * generates a column of albums, with each album shown as a row.
 */
fun LazyListScope.albumItems(
    albums: List<AlbumInfo>,
    coroutineScope: CoroutineScope,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.albums, albums.size, albums.size)
            ) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.albums, albums.size, albums.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(albums) { item ->
        AlbumItemRow(
            album = item,
            coroutineScope = coroutineScope,
            navigateToAlbumDetails = navigateToAlbumDetails,
        )
    }
}

/**
 * Album Items Lazy Grid Scope Generator.
 * Provides header item with a count of the albums given, and
 * generates a grid of albums, with each album item shown as a card.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LazyGridScope.albumItems(
    albums: List<AlbumInfo>,
    coroutineScope: CoroutineScope,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    //modifier: Modifier = Modifier,
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
                    quantityStringResource(R.plurals.albums, albums.size, albums.size)
                ) {
                    it.value.uppercase()
                },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp).weight(1f,true)
            )
            //Spacer(Modifier.weight(1f,true))

            // sort icon
            IconButton(onClick={showBottomSheet = true}) {
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
                libraryCategory = LibraryCategory.Albums,
            )
        }
    }

    items(
        albums,
        span = { GridItemSpan(1) }
    ){ item ->
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            modifier = Modifier,
            onClick = { navigateToAlbumDetails(item) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                AlbumItemBoxHeader(item)
                AlbumItemBoxFooter(item)
            }
        }
    }
}

/**
 * Create a composable view of a Album in a row form
 */
@Composable
private fun AlbumItemRow(
    album: AlbumInfo,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.padding(4.dp)){
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { navigateToAlbumDetails(album) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                AlbumImage(
                    albumImage = album.artworkUri,//albumImageId,
                    contentDescription = album.title,
                    modifier = modifier
                        .size(56.dp)
                        //.fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                )

                Column(modifier.weight(1f)){
                    Text(
                        text = album.title,
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                IconButton( // more options button
                    //modifier = Modifier.padding(0.dp),
                    onClick = { /*
                        coroutineScope.launch {
                            AlbumMoreOptionsBottomModal(
                                onDismissRequest = {},
                                coroutineScope = coroutineScope,
                                album = album,
                                navigateToAlbumDetails = navigateToAlbumDetails,
                            )
                        }*/
                    }, // pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
                ) {
                    Icon( // more options icon
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.icon_more),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

private val FEATURED_ALBUM_IMAGE_SIZE_DP = 160.dp

@Composable
private fun AlbumItemBoxFooter(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.requiredWidth(FEATURED_ALBUM_IMAGE_SIZE_DP)
    ) {
        Text(
            text = album.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(4.dp).weight(1f,true)
        )

        //Spacer(Modifier.weight(1f))

        IconButton(
            onClick = {  },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun AlbumItemBoxHeader(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomStart
    ){
        AlbumImage(
            albumImage = album.artworkUri,//albumImageId,
            contentDescription = album.title,
            modifier = modifier
                .size(FEATURED_ALBUM_IMAGE_SIZE_DP)
                .clip(MaterialTheme.shapes.medium),
        )

        Text(
            text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .border(1.dp,color = Color.Transparent, shape = MusicShapes.small)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MusicShapes.small
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun TopAlbumRowItem(
    albumName: String,
    albumImage: Uri,
    //isFollowed: Boolean,
    modifier: Modifier = Modifier,
    //onToggleFollowClicked: () -> Unit,
) {
    Column(
        modifier.semantics(mergeDescendants = true) {}
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = albumImage,
                contentDescription = albumName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )
        }

        Text(
            text = albumName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

//@Preview
@Composable
fun AlbumItemPreviewRow() {
    MusicTheme {
        AlbumItemRow(
            album = PreviewAlbums[0],
            navigateToAlbumDetails = {},
            coroutineScope = rememberCoroutineScope(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumItemPreviewBox() {
    MusicTheme {
        Column {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.Transparent,
                modifier = Modifier,
                onClick = {}
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AlbumItemBoxHeader(PreviewAlbums[0])
                    AlbumItemBoxFooter(PreviewAlbums[0])
                }
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.Transparent,
                modifier = Modifier,
                onClick = {}
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AlbumItemBoxHeader(PreviewAlbums[3])
                    AlbumItemBoxFooter(PreviewAlbums[3])
                }
            }
        }
    }
}