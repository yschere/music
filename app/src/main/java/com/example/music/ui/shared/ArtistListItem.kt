package com.example.music.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.music.R
import com.example.music.designsys.theme.ICON_SIZE
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.testing.PreviewArtists
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.frontTextPadding
import com.example.music.util.listItemIconMod
import com.example.music.util.listItemRowPadding
import com.example.music.util.quantityStringResource
import com.example.music.util.textHeightPadding

/**
 * Composable for an Artist Item in a list
 * @param artist defines the domain model for an artist item
 * @param navigateToArtistDetails defines the actions for
 * navigating to the artist's details screen when the item is clicked
 * @param onMoreOptionsClick defines the actions for opening the
 * MoreOptions menu modal when the MoreOptions icon is clicked
 * @param hasBackground defines if the item should have a background color or not
 * @param modifier defines any modifiers to apply to item
 */
@Composable
fun ArtistListItem(
    artist: ArtistInfo,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    hasBackground: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color =
                if (hasBackground) MaterialTheme.colorScheme.surfaceContainer
                else Color.Transparent,
            onClick = { navigateToArtistDetails(artist) },
        ) {
            ArtistListItemRow(
                artist = artist,
                onMoreOptionsClick = onMoreOptionsClick,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun ArtistListItemRow(
    artist: ArtistInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.listItemRowPadding(),
    ) {
        ArtistListItemIcon(artist = artist.name)
        Column(Modifier.frontTextPadding().weight(1f)) {
            Text(
                text = artist.name,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = quantityStringResource(R.plurals.albums, artist.albumCount, artist.albumCount) + " • ",
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.textHeightPadding(),
                )
                Text(
                    text = quantityStringResource(R.plurals.songs, artist.songCount, artist.songCount),
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.textHeightPadding(),
                )
            }
        }
        MoreOptionsBtn(onClick = onMoreOptionsClick)
    }
}

@Composable
private fun ArtistListItemIcon(
    artist: String,
) {
    Row(
        modifier = Modifier
            .listItemIconMod(ICON_SIZE, MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Text(
            text = artist[0].toString(),
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.CenterVertically),
        )
    }
}

@Preview
@Composable
fun PreviewArtistItem() {
    MusicTheme {
        ArtistListItem(
            artist = PreviewArtists[0],
            navigateToArtistDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier
        )
    }
}
