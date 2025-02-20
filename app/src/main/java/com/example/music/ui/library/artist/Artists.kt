package com.example.music.ui.library.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.model.ArtistInfo
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

/**
 * Artist Items Lazy List Scope Generator.
 * Provides header item with a count of the artist given, and
 * generates a column of artists, with each artist item shown as a row.
 */
/*fun LazyListScope.artistItems(
    artists: List<ArtistInfo>,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    //playerSongs: List<PlayerSong>, //TODO: PlayerSong support
//    navigateToPlayer: (SongInfo) -> Unit,
//    onQueueSong: (PlayerSong) -> Unit
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.artists, artists.size, artists.size)
            ) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.artists, artists.size, artists.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(artists) { item ->
        ArtistListItem(
            //what is needed for the artist list navigation use case
            artist = item, //TODO: PlayerSong support
            navigateToArtistDetails = navigateToArtistDetails,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}*/

/**
 * Playlist Items Lazy Grid Scope Generator.
 * Provides header item with a count of the playlist given, and
 * generates a column of playlists, with each playlist item shown as a row.
 */
fun LazyGridScope.artistItems(
    artists: List<ArtistInfo>,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    //playerSongs: List<PlayerSong>, //TODO: PlayerSong support
//    navigateToPlayer: (SongInfo) -> Unit,
    //onQueueSong: (PlayerSong) -> Unit
) {

    fullWidthItem {
        Text(
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.artists, artists.size, artists.size)) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.artists, artists.size, artists.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(artists) { item ->
        ArtistListItem(
            artist = item, //TODO: PlayerSong support
            navigateToArtistDetails = navigateToArtistDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ArtistListItem(
    artist: ArtistInfo,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) { //outermost layer with padding of 4 for separation between other song list items
        Surface( //second most layer, contains onclick action and background color
            shape = MaterialTheme.shapes.large,
            //color = MaterialTheme.colorScheme.background,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { navigateToArtistDetails(artist) }, //this is how navigateToPlayer should be used for each song ListItem, as the passed in onClick event
        ) {
            ArtistListItemRow( //design content of song list item
                artist = artist,
                modifier = modifier//.padding(4.dp),
            )
        }
    }
}

@Composable
private fun ArtistListItemRow(
    artist: ArtistInfo,
    modifier: Modifier = Modifier,
) {
    Row( //third layer, contains layout logic and information for content
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {

        ArtistListItemIcon(
            artist = artist.name, //placeholder
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Column(modifier.weight(1f)) {
            Text(
                text = artist.name,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Row(
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                if (artist.albumCount != null) { //if showArtistName is true
                    Text(
                        text = quantityStringResource(R.plurals.albums, artist.albumCount!!, artist.albumCount!!),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
                if (artist.songCount != null) {
                    Text(
                        text = " • " + quantityStringResource(R.plurals.songs, artist.songCount!!, artist.songCount!!),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
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

@Composable
private fun ArtistListItemIcon(
    artist: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))){
        Text(
            text = artist[0].toString(), //TODO: FOUND, one place where song property is needed that PlayerSong does not need. original code: song.albumTrackNumber from SongInfo with album context, still the same in SongListItem(songinfo, albumInfo)
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxSize().padding(vertical = 15.dp),
        )
    }
}