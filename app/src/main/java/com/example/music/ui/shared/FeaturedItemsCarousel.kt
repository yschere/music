package com.example.music.ui.shared

import androidx.compose.foundation.background
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
import com.example.music.model.AlbumInfo
import com.example.music.model.PlaylistInfo
import com.example.music.ui.theme.MusicTheme
import kotlinx.collections.immutable.PersistentList


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
                    itemImage = 1,//album.artwork!!,
                    itemTitle = album.title,
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
    //logger.info { "Featured Playlist Item function start" }
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
                //logger.info { "Horizontal Pager - playlists layout for playlist ${playlist.id}" }
                FeaturedCarouselItem(
                    itemImage = 1,//album.artwork!!,
                    itemTitle = playlist.name,
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
private fun FeaturedCarouselItem(
    itemTitle: String,
    //itemImage: String,
    itemImage: Int,
    modifier: Modifier = Modifier,
) {
    //logger.info { "Featured Carousel Item function start" }
    Column(modifier) {
        Box(
            Modifier
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
        }
        Row(Modifier.fillMaxSize()) {
            Text(
                text = itemTitle,
//            style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp)/*.align(Alignment.CenterVertically)*/.weight(1f,false)
//            modifier = Modifier
//                .padding(top = 8.dp)
//                .align(Alignment.CenterHorizontally)
            )
            IconButton(
                onClick = { /* TODO */ },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.cd_more),
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
            itemImage = 1,//album.artwork!!,
            itemTitle = "Help",
            //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
            modifier = Modifier
                .size(FEATURED_ITEM_IMAGE_SIZE_DP, FEATURED_ITEM_IMAGE_SIZE_DP + 30.dp)
                .fillMaxSize()
        )
    }
}