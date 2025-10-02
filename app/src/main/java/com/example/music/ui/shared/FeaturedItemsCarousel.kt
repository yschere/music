package com.example.music.ui.shared

import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.ITEM_IMAGE_CARD_SIZE
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.designsys.theme.SUBTITLE_HEIGHT
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.quantityStringResource
import kotlinx.collections.immutable.PersistentList

private const val TAG = "Featured Items Carousel"

/**
 * Composable for Featured Albums Carousel. Displays a horizontal pager for the albums given
 * @param pagerState defines the state of the pager
 * @param items the list of items to define per page
 * @param onClick actions to perform when clicking on a pager item
 * @param onMoreOptionsClick actions to perform when clicking on a pager item's MoreOptions btn
 * @param modifier defines any modifiers to apply to carousel
 */
@Composable
fun FeaturedAlbumsCarousel(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    onClick: (Long) -> Unit,
    onMoreOptionsClick: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Featured Albums Carousel START")
    Column(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Log.i(TAG, "Generating Horizontal pager")
            val horizontalPadding = (this.maxWidth - ITEM_IMAGE_CARD_SIZE) / 2
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 16.dp,
                ),
                pageSpacing = 24.dp,
                pageSize = PageSize.Fixed(ITEM_IMAGE_CARD_SIZE)
            ) { page ->
                Log.i(TAG, "Generating Album Carousel Item: $page")
                val album = items[page]
                FeaturedCarouselItem(
                    itemTitle = album.title,
                    itemImage = album.artworkUri,
                    itemSize = album.songCount,
                    onMoreOptionsClick = { onMoreOptionsClick(album) },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onClick(album.id) }
                )
            }
        }
    }
}

/**
 * Composable for Featured Playlists Carousel. Displays a horizontal pager for the items given
 * @param pagerState defines the state of the pager
 * @param items the list of items to define per page
 * @param onClick actions to perform when clicking on a pager item
 * @param onMoreOptionsClick actions to perform when clicking on a pager item's MoreOptions btn
 * @param modifier defines any modifiers to apply to carousel
 */
@Composable
fun FeaturedPlaylistsCarousel(
    pagerState: PagerState,
    items: PersistentList<PlaylistInfo>,
    onClick: (PlaylistInfo) -> Unit,
    onMoreOptionsClick: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Featured Playlist Carousel START")
    Column(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            val horizontalPadding = (this.maxWidth - ITEM_IMAGE_CARD_SIZE) / 2
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 16.dp,
                ),
                pageSpacing = 24.dp,
                pageSize = PageSize.Fixed(ITEM_IMAGE_CARD_SIZE)
            ) { page ->
                val playlist = items[page]
                Log.i(TAG, "Generating Playlist Carousel Item: $page")
                FeaturedCarouselItem(
                    itemTitle = playlist.name,
                    itemImage =
                        if (playlist.songCount == 0) Uri.parse("")
                        else playlist.playlistImage[0], // FixMe: need this to account for the 4block set of images
                    itemSize = playlist.songCount,
                    onMoreOptionsClick = { onMoreOptionsClick(playlist) },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onClick(playlist) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedCarouselItem(
    itemTitle: String = "",
    itemImage: Uri,
    itemSize: Int = 0,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Featured Carousel Item START: $itemTitle")
    Column(modifier = modifier) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .size(ITEM_IMAGE_CARD_SIZE)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = itemImage,
                contentDescription = itemTitle,
                modifier = Modifier.listItemIconMod(ITEM_IMAGE_CARD_SIZE, MaterialTheme.shapes.medium),
            )

            // Song Count in bottom left of item image
            Text(
                text = quantityStringResource(R.plurals.songs, itemSize, itemSize),
                maxLines = 1,
                minLines = 1,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.songCountCard(MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = itemTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium, //titleSmall,
                modifier = Modifier.padding(start = SMALL_PADDING).weight(1f,true)
            )
            MoreOptionsBtn(onClick = onMoreOptionsClick)
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
            modifier = Modifier
                .size(ITEM_IMAGE_CARD_SIZE, ITEM_IMAGE_CARD_SIZE + SUBTITLE_HEIGHT)
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        )
    }
}