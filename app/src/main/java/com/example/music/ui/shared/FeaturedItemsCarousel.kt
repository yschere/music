package com.example.music.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.example.music.designsys.component.AlbumImage
import com.example.music.model.AlbumInfo
import kotlinx.collections.immutable.PersistentList


private val FEATURED_ALBUM_IMAGE_SIZE_DP = 160.dp

@Composable
private fun FeaturedAlbums(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: Using BoxWithConstraints is not quite performant since it requires 2 passes to compute
    // the content padding. This should be revisited once a carousel component is available.
    // Alternatively, version 1.7.0-alpha05 of Compose Foundation supports `snapPosition`
    // which solves this problem and avoids this calculation altogether. Once 1.7.0 is
    // stable, this implementation can be updated.
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding = (this.maxWidth - FEATURED_ALBUM_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_ALBUM_IMAGE_SIZE_DP)
        ) { page ->
            val album = items[page]
            FeaturedAlbumCarouselItem(
                albumImage = 1,//album.artwork!!,
                albumTitle = album.title,
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

@Composable
private fun FeaturedAlbumCarouselItem(
    albumTitle: String,
    //albumImage: String,
    albumImage: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Box(
            Modifier
                .size(FEATURED_ALBUM_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = albumImage,
                contentDescription = albumTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        Text(
            text = albumTitle,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}