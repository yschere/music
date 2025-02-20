package com.example.music.ui.library.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.model.PlaylistInfo
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

/**
 * Playlist Items Lazy List Scope Generator.
 * Provides header item with a count of the playlist given, and
 * generates a column of playlists, with each playlist item shown as a row.
 */
/*fun LazyListScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.playlists, playlists.size, playlists.size)
            ) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.playlists, playlists.size, playlists.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(playlists) { item ->
        PlaylistItemRow(
            playlist = item,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/

/**
 * Playlist Items Lazy Grid Scope Generator.
 * Provides header item with a count of the playlist given, and
 * generates a grid of playlists, with each playlist item shown as a card.
 */
fun LazyGridScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier,
) {

    fullWidthItem {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.playlists, playlists.size, playlists.size)
            ) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.playlists, playlists.size, playlists.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(playlists){ item ->
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            modifier = Modifier,
            onClick = { navigateToPlaylistDetails(item) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                PlaylistItemBoxHeader(item)
                PlaylistItemBoxFooter(item)
            }
        }
    }
}

/**
 * Create a composable view of a Playlist in a row form
 */
@Composable
private fun PlaylistItemRow(
    playlist: PlaylistInfo,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.padding(4.dp)){
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,//surfaceContainer,
            onClick = { navigateToPlaylistDetails(playlist) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                AlbumImage(
                    modifier = modifier
                        .size(56.dp)
                        //.fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    albumImage = 2,//albumImageId,
                    contentDescription = playlist.name
                )

                Column(modifier.weight(1f)){
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

                IconButton( //more options button
                    //modifier = Modifier.padding(0.dp),
                    onClick = { /* TODO */ }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
                ) {
                    Icon( //more options icon
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.cd_more),
                        //tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private val FEATURED_PLAYLIST_IMAGE_SIZE_DP = 160.dp

@Composable
private fun PlaylistItemBoxFooter(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.requiredWidth(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
    ) {
        Text(
            text = playlist.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically).weight(1f,false)//.fillMaxWidth()
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(4.dp).weight(1f,false)
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

@Composable
private fun PlaylistItemBoxHeader(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomStart
    ){
        AlbumImage(
            modifier = modifier
                .size(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
                .clip(MaterialTheme.shapes.medium),
            albumImage = 2,//albumImageId,
            contentDescription = playlist.name
        )

        Text(
            text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            //color = MaterialTheme.colorScheme.surface,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .border(1.dp,color = Color.Transparent, shape = MusicShapes.small)
                .background(
                    Color.DarkGray,
                    shape = MusicShapes.small
                )
                .padding(2.dp)
        )
    }
}

@Composable
private fun TopPlaylistRowItem(
    playlistName: String,
    //playlistImageId: String,
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
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                albumImage = 2,//albumImageId,
                contentDescription = playlistName
            )
        }

        Text(
            text = playlistName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PlaylistItemPreviewRow() {
    MusicTheme {
        PlaylistItemRow(
            playlist = PreviewPlaylists[0],
            navigateToPlaylistDetails = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistItemPreviewBox() {
    MusicTheme {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            modifier = Modifier,
            onClick = {}
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                PlaylistItemBoxHeader(PreviewPlaylists[2])
                PlaylistItemBoxFooter(PreviewPlaylists[2])
            }
        }
    }
}