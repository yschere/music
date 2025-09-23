package com.example.music.ui.shared

import android.net.Uri
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
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.quantityStringResource

private val FEATURED_PLAYLIST_IMAGE_SIZE_DP = 160.dp

@Composable
fun PlaylistItem(
    playlist: PlaylistInfo,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    cardOrRow: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) {
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
                color = MaterialTheme.colorScheme.surfaceContainer,
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
        modifier = modifier
            .width(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            AlbumImage(
                albumImage = Uri.parse(""), // FixMe: needs Playlist Image generation
                contentDescription = playlist.name,
                modifier = Modifier
                    .size(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
                    .clip(MaterialTheme.shapes.medium),
            )

            // Song Count in bottom left of playlist image
            Text(
                text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
                maxLines = 1,
                minLines = 1,
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
                text = playlist.name,
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
        modifier = Modifier.padding(8.dp),
    ) {
        AlbumImage(
            albumImage = Uri.parse(""), // FixMe: needs Playlist Image generation
            contentDescription = playlist.name,
            modifier = modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium),
        )

        Column(modifier.weight(1f)) {
            Text(
                text = playlist.name,
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
                    text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        // More Options btn
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