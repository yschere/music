package com.example.music.ui.shared

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.ITEM_IMAGE_CARD_SIZE
import com.example.music.designsys.theme.ITEM_IMAGE_ROW_SIZE
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.quantityStringResource

@Composable
fun AlbumListItem(
    album: AlbumInfo,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    cardOrRow: Boolean = true,
    hasBackground: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (cardOrRow) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.Transparent,
                onClick = { navigateToAlbumDetails(album) }
            ) {
                AlbumItemCard(
                    album = album,
                    onMoreOptionsClick = onMoreOptionsClick,
                    modifier = Modifier,
                )
            }
        } else {
            Surface(
                shape = MaterialTheme.shapes.large,
                color =
                    if (hasBackground) MaterialTheme.colorScheme.surfaceContainer
                    else Color.Transparent,
                onClick = { navigateToAlbumDetails(album) }
            ) {
                AlbumItemRow(
                    album = album,
                    onMoreOptionsClick = onMoreOptionsClick,
                    modifier = Modifier,
                )
            }
        }
    }
}

/**
 * Create a composable view of an Album in a card form
 */
@Composable
fun AlbumItemCard(
    album: AlbumInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(ITEM_IMAGE_CARD_SIZE)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            AlbumImage(
                albumImage = album.artworkUri,
                contentDescription = album.title,
                modifier = Modifier
                    .size(ITEM_IMAGE_CARD_SIZE)
                    .clip(MaterialTheme.shapes.medium),
            )

            // Song Count in bottom left of album image
            Text(
                text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape
                    )
                    .padding(4.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = album.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(4.dp).weight(1f,true)
            )

            // More Options btn
            MoreOptionsBtn(onClick = onMoreOptionsClick)
        }
    }
}

/**
 * Create a composable view of an Album in a row form
 */
@Composable
fun AlbumItemRow(
    album: AlbumInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = CONTENT_PADDING)
            .padding(start = CONTENT_PADDING),
    ) {
        AlbumImage(
            albumImage = album.artworkUri,
            contentDescription = album.title,
            modifier = Modifier
                .size(ITEM_IMAGE_ROW_SIZE)
                .clip(MaterialTheme.shapes.medium),
        )

        Column(modifier.weight(1f)) {
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
                    text =
                        if (album.albumArtistName != null) album.albumArtistName + " • "
                        else "",
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        MoreOptionsBtn(onClick = onMoreOptionsClick)
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
//@Preview( name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES )
@Composable
private fun AlbumListItem_CARDPreview() {
    MusicTheme {
        AlbumListItem(
            album = PreviewAlbums[0],
            navigateToAlbumDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier,
            cardOrRow = true,
        )
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Composable
private fun AlbumListItem_ROWPreview() {
    MusicTheme {
        AlbumListItem(
            album = PreviewAlbums[4],
            navigateToAlbumDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier,
            cardOrRow = false,
        )
    }
}
