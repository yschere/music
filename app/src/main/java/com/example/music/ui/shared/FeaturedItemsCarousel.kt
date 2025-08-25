package com.example.music.ui.shared

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.quantityStringResource
import kotlinx.collections.immutable.PersistentList

private const val TAG = "Featured Albums Carousel"
private val FEATURED_ITEM_IMAGE_SIZE_DP = 160.dp

/**
 * Composable for Featured Albums Carousel. Displays a horizontal pager for the items given
 * @param pagerState [PagerState]
 * @param items [PersistentList] of [AlbumInfo]
 * @param navigateToAlbumDetails [AlbumInfo] -> [Unit]
 * @param modifier [Modifier]
 */
@Composable
fun FeaturedAlbumsCarousel(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    onMoreOptionsClick: (Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            val horizontalPadding = (this.maxWidth - FEATURED_ITEM_IMAGE_SIZE_DP) / 2
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 16.dp,
                ),
                pageSpacing = 24.dp,
                pageSize = PageSize.Fixed(FEATURED_ITEM_IMAGE_SIZE_DP)
            ) { page ->
                val album = items[page]
                FeaturedCarouselItem(
                    itemTitle = album.title,
                    itemImage = album.artworkUri,//album.artwork!!,
                    itemSize = album.songCount,
                    onMoreOptionsClick = { onMoreOptionsClick(album) },
                    //onClick = AlbumMoreOptionsBottomModal(album),
                    //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            navigateToAlbumDetails(album)
                        }
                )
            }
        }
    }
}

/**
 * Composable for Featured Playlists Carousel. Displays a horizontal pager for the items given
 * @param pagerState [PagerState]
 * @param items [PersistentList] of [PlaylistInfo]
 * @param navigateToPlaylistDetails ([PlaylistInfo]) -> [Unit]
 * @param modifier [Modifier]
 */
@Composable
fun FeaturedPlaylistsCarousel(
    pagerState: PagerState,
    items: PersistentList<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    //Log.i(TAG, "Featured Playlist Item function start")
    Column(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            val horizontalPadding = (this.maxWidth - FEATURED_ITEM_IMAGE_SIZE_DP) / 2
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 16.dp,
                ),
                pageSpacing = 24.dp,
                pageSize = PageSize.Fixed(FEATURED_ITEM_IMAGE_SIZE_DP)
            ) { page ->
                val playlist = items[page]
                //Log.i(TAG, "Horizontal Pager - playlists layout for playlist ${playlist.id}")
                FeaturedCarouselItem(
                    itemTitle = playlist.name,
                    itemImage = Uri.parse(""), // FixMe: needs Playlist Image generation
                    itemSize = playlist.songCount,
                    onMoreOptionsClick = {},
                    //onMoreOptionsClick = { onMoreOptionsClick(playlist) },
                    //onClick = PlaylistMoreOptionsBottomModal(playlist),
                    //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            navigateToPlaylistDetails(playlist)
                        }
                )
            }
        }
    }
}

@Composable
fun FeaturedCarouselItem(
    itemTitle: String = "",
    itemImage: Uri,
    itemSize: Int = 0,
    onMoreOptionsClick: (Any) -> Unit,
    //onClick: () -> Unit, //pass in either Album or Playlist MoreOptionsModal action here
    modifier: Modifier = Modifier,
) {
    //Log.i(TAG, "Featured Carousel Item function start")
    Column(modifier) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .size(FEATURED_ITEM_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = itemImage,
                contentDescription = itemTitle,
                modifier = Modifier
                    .size(FEATURED_ITEM_IMAGE_SIZE_DP)
                    .clip(MaterialTheme.shapes.medium),
            )
            Text(
                text = quantityStringResource(R.plurals.songs, itemSize, itemSize),
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = itemTitle,
                //style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp).weight(1f,true)
                //modifier = Modifier
                    //.padding(top = 8.dp)
            )

            // more options btn
            IconButton(
                onClick = { onMoreOptionsClick },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.icon_more),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCard() {
    MusicTheme {
        FeaturedCarouselItem(
            itemTitle = PreviewAlbums[0].title,
            itemImage = Uri.parse(""),//album.artwork!!,
            onMoreOptionsClick = {},
            //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
            modifier = Modifier
                .size(FEATURED_ITEM_IMAGE_SIZE_DP, FEATURED_ITEM_IMAGE_SIZE_DP + 48.dp)
                .fillMaxSize()
        )
    }
}