package com.example.music.ui.shared

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.component.AlbumImageBm
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.ICON_SIZE
import com.example.music.designsys.theme.ITEM_IMAGE_ROW_SIZE
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.util.frontTextPadding
import com.example.music.util.listItemIconMod
import com.example.music.util.listItemRowPadding
import com.example.music.util.textHeightPadding

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
    hasBackground: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color =
                if (hasBackground) MaterialTheme.colorScheme.surfaceContainer
                else Color.Transparent,
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
                modifier = Modifier,
            )
        }
    }
}

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
        modifier = Modifier.listItemRowPadding(),
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
            ReorderItemBtn(
                onClick = {},
                title = song.title,
            )
        }

        //show the track number of the song
        if (showTrackNumber) {
            SongListItemNumber(
                number = song.trackNumber,
                modifier = Modifier.size(ICON_SIZE - CONTENT_PADDING, ICON_SIZE)
            )
        }

        // Check if album image needs to be shown
        if (showAlbumImage) {
            SongListItemImage(
                artworkUri = song.artworkUri,
            //SongListItemImageBm(
                //artworkBitmap = song.artworkBitmap,
                description = song.title,
                modifier = Modifier.listItemIconMod(ITEM_IMAGE_ROW_SIZE, MaterialTheme.shapes.small),
            )
        }

        // Song Title
        // Artist, Album, duration
        Column(modifier.frontTextPadding().weight(1f)) {
            Text(
                text = song.title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = song.setSubText(
                        showArtistName = showArtistName,
                        showAlbumTitle = showAlbumTitle
                    ),
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.textHeightPadding(),
                )
            }
        }

        MoreOptionsBtn(onClick = onMoreOptionsClick)
    }
}

@Composable
private fun SongListItemImage(
    artworkUri: Uri,
    description: String = "",
    modifier: Modifier = Modifier
) {
    AlbumImage(
        albumImage = artworkUri,
        contentDescription = description,
        modifier = modifier,
    )
}

@Composable
private fun SongListItemImageBm(
    artworkBitmap: Bitmap?,
    description: String = "",
    modifier: Modifier = Modifier
) {
    AlbumImageBm(
        albumImage = artworkBitmap,
        contentDescription = description,
        modifier = modifier,
    )
}

@Composable
private fun SongListItemNumber(
    number: Int?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = number?.toString() ?: "-",
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.CenterVertically)
        )
    }
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
    subTitle = subTitle.plus(this.duration.formatString())
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
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            //modifier = Modifier,
        )
    }
}

//@CompDarkPreview
@Composable
private fun SongListItem_ShowAllPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[0],
            onClick = {},
            onMoreOptionsClick = {},
            isListEditable = true,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = true,
            //modifier = Modifier,
        )
    }
}

@CompLightPreview
@Composable
private fun SongListItem_AlbumDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[5],
            onClick = {},
            onMoreOptionsClick = {},
            isListEditable = false,
            showAlbumImage = false,
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
            isListEditable = true,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showTrackNumber = false,
            //modifier = Modifier,
        )
    }
}
