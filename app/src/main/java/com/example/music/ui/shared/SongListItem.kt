package com.example.music.ui.shared

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Reorder
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.component.AlbumImageBm
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.CompLightPreview
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Original Song List Item
 */
@Composable
fun SongListItem(
    song: SongInfo,
    onClick: (SongInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    isListEditable: Boolean = false,
    showArtistName: Boolean = false,
    showAlbumImage: Boolean = false,
    showAlbumTitle: Boolean = false,
    showTrackNumber: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(song) },
        ) {
            SongListItemRow(
                song = song,
                onMoreOptionsClick = onMoreOptionsClick,
                isListEditable = isListEditable,
                showArtistName = showArtistName,
                showAlbumImage = showAlbumImage,
                showAlbumTitle = showAlbumTitle,
                showTrackNumber = showTrackNumber,
                modifier = modifier,
            )
        }
    }
}

/**
 * Original Song List Item Content
 */
@Composable
private fun SongListItemRow(
    song: SongInfo,
    onMoreOptionsClick: () -> Unit,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    showTrackNumber: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp),
    ) {
        /* // ********* UI Logic Expectations: *********
            // for properties that can be null, replace them with empty string
            // --expected possible empty, null properties: artist, album, duration, trackNumber, artworkUri

            // want to show Album Image on all screens except edit maybe, but choosing song artwork if it exists, if not check for album artwork and show that
            // --expect edit (playlist order?) screen to have showAlbumImage as false or not have this param passed
            // --expect home.songs, library.songs, albumDetails, genreDetails, artistDetails, playlistDetails, composerDetails to have showAlbumImage as true
            // --when a song doesn't have uri / artworkBitmap saved, there will be default empty artwork used instead

            // want to show track number on album screen only
            // --expect albumDetails to have showTrackNumber as true
            // --expect all other screens to have showTrackNumber as false, or not have param passed

            // do not want to show album title on albumDetails screen

            // not sure how/when this will be implemented but multi-select could appear in a couple ways
            //  and want to account for it somehow. current question is if it will be using this songListItem
            //  screen or if it will be its own composable. and with that, how the checkboxes needed will
            //  be implemented. at least, i don't want reorder, track number visible with checkbox. not sure
            //  yet about album artwork
            // --nothing to expect yet, need to determine implementation for multi-select
        */

        // list edit-ability would most likely be for queue list and playlist, when editing list is selected
        if (isListEditable) { // Check if this means songs is meant to be in an editable/movable list
            //Box(modifier = modifier.size(56.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
            IconButton(
                onClick = {},
                enabled = true,
                modifier = Modifier.padding(start = 0.dp)//size(56.dp),
            ) {
                Icon( // reorder song icon
                    imageVector = Icons.Outlined.Reorder,
                    contentDescription = stringResource(R.string.icon_reorder) + " for song " + song.title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        //show the track number of the song
        // FixMe: change this so it's not as pronounced, nor shifts over the rest of the row content
        if (showTrackNumber) {
            Text(
                text =
                    if (song.trackNumber == null) ""
                    else song.trackNumber.toString(),
                minLines = 1,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(10.dp),
            )
        }

        // Check if album image needs to be shown
        if (showAlbumImage) {
            SongListItemImage(
                artworkUri = song.artworkUri,
            //SongListItemImageBm(
                //artworkBitmap = song.artworkBitmap,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        }

        // Song Title
        // Artist, Album, duration
        Column(modifier.weight(1f)) {
            Text(
                text = song.title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                Text(
                    text = song.setSubText(showAlbumTitle, showArtistName),
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }

        // More Options btn
        IconButton(onClick = onMoreOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun SongListItemImage(
    artworkUri: Uri,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        albumImage = artworkUri,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
private fun SongListItemImageBm(
    artworkBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    AlbumImageBm(
        albumImage = artworkBitmap,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
private fun SongInfo.setSubText(
    showArtistName: Boolean,
    showAlbumTitle: Boolean,
): String {
    var subTitle = ""
    if (showArtistName && this.artistId != 0L) {
        subTitle = subTitle.plus(this.artistName + " • ")
    }
    if (showAlbumTitle && this.albumId != 0L) {
        subTitle = subTitle.plus( this.albumTitle + " • ")
    }
    subTitle = subTitle.plus(this.duration.formatStr())
    return subTitle
}

@CompLightPreview
@Composable
private fun SongListItem_GeneralPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[2],
            onClick = {},
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            //modifier = Modifier,
        )
    }
}

@CompDarkPreview
@Composable
private fun SongListItem_ShowAllPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[0],
            onClick = {},
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = true,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = true,
            //modifier = Modifier,
        )
    }
}

//@CompLightPreview
@Composable
private fun SongListItem_AlbumDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[5],
            onClick = {},
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = false,
            showTrackNumber = true,
            //modifier = Modifier,
        )
    }
}

//@CompLightPreview
@Composable
private fun SongListItem_AllSongsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[8],
            //artist = PreviewArtists[3],
            //album = PreviewAlbums[6],
            onClick = {},
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            //modifier = Modifier,
        )
    }
}

//@CompLightPreview
@Composable
private fun SongListItem_PlaylistDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[3],
            onClick = {},
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = true,
            //modifier = Modifier,
        )
    }
}

//@CompLightPreview
//@CompDarkPreview
@Composable
private fun SongListItem_EditListPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[2],
            onClick = {},
            onMoreOptionsClick = {},
            //onQueueSong = {},
            isListEditable = true,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            //modifier = Modifier,
        )
    }
}

/*private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}*/

fun Duration.formatStr(): String {
    val minutes = this.toMinutes().toString().padStart(1, '0')
    val seconds = (this.toSeconds() % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}