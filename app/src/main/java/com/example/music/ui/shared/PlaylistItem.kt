package com.example.music.ui.shared

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.ITEM_IMAGE_CARD_SIZE
import com.example.music.designsys.theme.ITEM_IMAGE_ROW_SIZE
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.ui.playlistdetails.PlaylistDetailsThumbnails
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.frontTextPadding
import com.example.music.util.listItemIconMod
import com.example.music.util.listItemRowPadding
import com.example.music.util.quantityStringResource
import com.example.music.util.songCountCard
import com.example.music.util.textHeightPadding

/**
 * Composable for an Playlist Item in a list
 * @param playlist defines the domain model for a playlist item
 * @param navigateToPlaylistDetails defines the actions for
 * navigating to the playlist's details screen when the item is clicked
 * @param onMoreOptionsClick defines the actions to take for opening the
 * MoreOptions menu modal when the MoreOptions icon is clicked
 * @param cardOrRow defines if the item should be in card form (true) or row form (false)
 * @param hasBackground defines if the item should have a background color or not
 * @param modifier defines any modifiers to apply to item
 */
@Composable
fun PlaylistItem(
    playlist: PlaylistInfo,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
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
                onClick = { navigateToPlaylistDetails(playlist) }
            ) {
                PlaylistItemCard(
                    playlist = playlist,
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
                onClick = { navigateToPlaylistDetails(playlist) }
            ) {
                PlaylistItemRow(
                    playlist = playlist,
                    onMoreOptionsClick = onMoreOptionsClick,
                    modifier = Modifier,
                )
            }
        }
    }
}


/**
 * Create a composable view of a Playlist in a card form
 */
@Composable
fun PlaylistItemCard(
    playlist: PlaylistInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(ITEM_IMAGE_CARD_SIZE)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            if (playlist.playlistImage.isEmpty()) {
                AlbumImage(
                    albumImage = Uri.parse(""),
                    contentDescription = playlist.name,
                    modifier = Modifier.listItemIconMod(ITEM_IMAGE_CARD_SIZE, MaterialTheme.shapes.medium)
                )
            } else {
                PlaylistDetailsThumbnails(
                    playlistImage = playlist.playlistImage,
                    imageSize = ITEM_IMAGE_CARD_SIZE / 2,
                )
            }

            // Song Count in bottom left of playlist image
            Text(
                text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
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
                text = playlist.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = SMALL_PADDING).weight(1f,true)
            )
            MoreOptionsBtn(onClick = onMoreOptionsClick)
        }
    }
}

/**
 * Create a composable view of a Playlist in a row form
 */
@Composable
private fun PlaylistItemRow(
    playlist: PlaylistInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.listItemRowPadding(),
    ) {
        AlbumImage(
            albumImage =
                if (playlist.songCount == 0) Uri.parse("")
                else playlist.playlistImage[0], // FixMe: need this to account for the 4block set of images
            contentDescription = playlist.name,
            modifier = Modifier.listItemIconMod(ITEM_IMAGE_ROW_SIZE, MaterialTheme.shapes.small),
        )
        Column(Modifier.frontTextPadding().weight(1f)) {
            Text(
                text = playlist.name,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.textHeightPadding(),
                )
            }
        }
        MoreOptionsBtn(onClick = onMoreOptionsClick)
    }
}

@Preview
@Composable
fun PlaylistItem_CARDPreview() {
    MusicTheme {
        PlaylistItem(
            playlist = PreviewPlaylists[0],
            navigateToPlaylistDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier,
            cardOrRow = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistItem_ROWPreview() {
    MusicTheme {
        PlaylistItem(
            playlist = PreviewPlaylists[0],
            navigateToPlaylistDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier,
            cardOrRow = false,
        )
    }
}