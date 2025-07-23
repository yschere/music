package com.example.music.ui.shared

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
import com.example.music.domain.testing.PreviewPlayerSongs
import com.example.music.domain.testing.getArtistData
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.PlayerSong
import com.example.music.domain.player.model.toPlayerSong
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.CompLightPreview
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 */

/**
 * Original Song List Item
 */
@Composable
fun SongListItem(
    song: SongInfo,
    onClick: (SongInfo) -> Unit, // navigateToPlayer
    onMoreOptionsClick: (SongInfo) -> Unit, //want to use for moreOptionsModal,
    // would I also need the context for where this is being clicked?
    // it would be used for determining which modal options to show
    // (what are the different contexts that need to be covered?)
    //onQueueSong: (SongInfo) -> Unit, // addToQueue
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    showTrackNumber: Boolean,
    modifier: Modifier = Modifier,
) {
    /* //want one row to show all items
    //want song title on separate row from extra info items (artist, album, duration)
    //need to include show__ boolean options with Item, like showAlbumImage and showSummary
    //remove the play and queue buttons, shift the album image to the far left, keep moreVert on the far right
    */

    Box(modifier = modifier.padding(4.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            //color = MaterialTheme.colorScheme.background,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(song) }, //this is how navigateToPlayer should be used for each song ListItem, as the passed in onClick event
        ) {
            SongListItemRow(
                song = song,
                onMoreOptionsClick = onMoreOptionsClick,
                //onQueueSong = onQueueSong,
                isListEditable = isListEditable,
                showArtistName = showArtistName,
                showAlbumImage = showAlbumImage,
                showAlbumTitle = showAlbumTitle,
                showTrackNumber = showTrackNumber,
                modifier = modifier,
            )

            /* //this iteration had the padding within song list item row as part of the row surrounding the call itself
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                SongListItemRow(
                    song = song,
                    //artist = artist,
                    album = album,
                    //onClick = onClick,
                    isListEditable = false,
                    showArtistName = true,
                    showAlbumImage = true,
                    showAlbumTitle = true,
                    modifier = modifier,
                )
            }*/
        }
    }
}

/**
 * PlayerSong supported version
 */
/*@Composable
fun SongListItem(
    song: PlayerSong,
    onClick: (PlayerSong) -> Unit, //use for navigateToPlayer
    //onMoreOptionsClick: (PlayerSong) -> Unit, //want to use for moreOptionsModal,
        // would I also need the context for where this is being clicked?
        // it would be used for determining which modal options to show
        // (what are the different contexts that need to be covered?)
    //onQueueSong: (PlayerSong) -> Unit,
    isListEditable: Boolean = false,
    showArtistName: Boolean = false,
    showAlbumImage: Boolean = false,
    showAlbumTitle: Boolean = false,
    showTrackNumber: Boolean = false,
    modifier: Modifier = Modifier,
) {
    /* //want one row to show all items
        //want song title on separate row from extra info items (artist, album, duration)
        //need to include show__ boolean options with Item, like showAlbumImage and showSummary
        //the album image to the far left, moreOptions on the far right
    */
    Box(modifier = modifier.padding(4.dp)) { //outermost layer with padding of 4 for separation between other song list items
        Surface( //second most layer, contains onclick action and background color
            shape = MaterialTheme.shapes.large,
            //color = MaterialTheme.colorScheme.background,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(song) }, //this is how navigateToPlayer should be used for each song ListItem, as the passed in onClick event
        ) {
            SongListItemRow( //design content of song list item
                song = song,
                isListEditable = isListEditable,
                showArtistName = showArtistName,
                showAlbumImage = showAlbumImage,
                showAlbumTitle = showAlbumTitle,
                showTrackNumber = showTrackNumber,
                modifier = modifier//.padding(4.dp),
            )
        }
    }
}*/

/**
 * Original Song List Item Content
 */
@Composable
private fun SongListItemRow(
    song: SongInfo,
    onMoreOptionsClick: (SongInfo) -> Unit,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    showTrackNumber: Boolean,
    modifier: Modifier = Modifier,
) {
    //for now keep the list of properties as is
    //later want to be able to support having the context of where
    //the item is being viewed as what determines which properties get shown
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        /* // ********* UI Logic Expectations: *********
            // for properties that can be null, replace them with empty string
            // --expected possible null properties: duration ... when the fuck was this null, trackNumber

            // want to show Album Image on all screens except edit maybe, but choosing song artwork if it exists, if not check for album artwork and show that
            // --expect edit (playlist order?) screen to have showAlbumImage as false or not have this param passed
            // --expect home.songs, library.songs, albumDetails, genreDetails, artistDetails, playlistDetails, composerDetails to have showAlbumImage as true

            // want to show track number on album screen for sure, maybe/maybe not for playlist screen
            // --expect albumDetails to have showTrackNumber as true
            // --undecided on playlistDetails
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
        // TODO change this so it's not as pronounced, nor shifts over the rest of the row content
        if (showTrackNumber) {
            Text(
                text = (song.trackNumber ?: 0).toString(), //TODO: FOUND, one place where song property is needed that PlayerSong does not need. original code: song.albumTrackNumber from SongInfo with album context, still the same in SongListItem(songInfo, albumInfo)
                minLines = 1,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            )
        }

        // Check if album image needs to be shown
        if (showAlbumImage) {
            SongListItemImage(
                albumImage = song.albumTitle,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        }

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
                    text = setSubText(song, showAlbumTitle, showArtistName),// song.setSubTitle(showArtistName, showAlbumTitle),
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )

                /* //old duration
                    val duration = song.duration
                    Text(
                        text = when {
                            duration != null -> {
                                // If we have the duration, we combine the date/duration via a
                                // formatted string
                                stringResource(
                                    R.string.song_date_duration,
                                    duration.toMinutes().toInt()
                                )
                            }
                            // Otherwise we just use the date
                            else -> MediumDateFormatter.format(song.dateLastPlayed)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                    )
                */
            }
        }

        IconButton( //more options button
            //modifier = Modifier.padding(0.dp),
            onClick = { onMoreOptionsClick(song) }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more), //stringResource(R.string.icon_more) + "for song " + song.title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * PlayerSong supported version
 */
/*@Composable
private fun SongListItemRow(
    song: PlayerSong,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    showTrackNumber: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row( //third layer, contains layout logic and information for content
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        /* // ********* UI Logic Expectations: *********
            // for properties that can be null, replace them with empty string
            // --expected possible null properties: duration ... when the fuck was this null, trackNumber

            // want to show Album Image on all screens except edit maybe, but choosing song artwork if it exists, if not check for album artwork and show that
            // --expect edit (playlist order?) screen to have showAlbumImage as false or not have this param passed
            // --expect home.songs, library.songs, albumDetails, genreDetails, artistDetails, playlistDetails, composerDetails to have showAlbumImage as true

            // want to show track number on album screen for sure, maybe/maybe not for playlist screen
            // --expect albumDetails to have showTrackNumber as true
            // --undecided on playlistDetails
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
        if (showTrackNumber) {
            Text(
                text = (song.trackNumber?: "").toString(), //TODO: FOUND, one place where song property is needed that PlayerSong does not need. original code: song.albumTrackNumber from SongInfo with album context, still the same in SongListItem(songInfo, albumInfo)
                minLines = 1,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            )
        }

        // Check if album image needs to be shown
        if (showAlbumImage) {
            SongListItemImage(
                albumImage = song.albumTitle,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        }

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
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                //if showArtistName is true
                if (showArtistName) {
                    Text(
                        text = song.artistName,//original code: getArtistData(song.artistId!!).name, //based on song: SongInfo, get PreviewData's PreviewArtist
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }

                //if showAlbumTitle is true
                if (showAlbumTitle) {
                    Text(
                        //text = if (showArtistName) " • " + album.title else album.title, //original code, based on album: AlbumInfo
                        text =
                            if (showArtistName && song.artistName != "")
                                " • " + song.albumTitle
                            else
                                song.albumTitle,
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }

                //duration
                Text(
                    text =
                        if ((showArtistName && song.artistName != "") || (showAlbumTitle && song.albumTitle != ""))
                            " • " + song.duration.formatStr()
                        else
                            song.duration.formatStr(),
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)//, horizontal = 8.dp),
                )

                /* //old duration
                    val duration = song.duration
                    Text(
                        text = when {
                            duration != null -> {
                                // If we have the duration, we combine the date/duration via a
                                // formatted string
                                stringResource(
                                    R.string.song_date_duration,
                                    duration.toMinutes().toInt()
                                )
                            }
                            // Otherwise we just use the date
                            else -> MediumDateFormatter.format(song.dateLastPlayed)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                    )
                */
            }
        }

        IconButton( //more options button
            //modifier = Modifier.padding(0.dp),
            onClick = { /* TODO */ }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more) + "for song " + song.title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}*/

/*@Composable
private fun SongListItemImage(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        //albumImage = album.artwork!!,
        //albumImage = R.drawable.bpicon,
        contentDescription = null,
        modifier = modifier,
    )
}*/

@Composable
private fun SongListItemImage(
    albumImage: String,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        //albumImage = album.artwork!!,
        //albumImage = R.drawable.bpicon,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
private fun setSubText(song: SongInfo, showArtistName: Boolean, showAlbumTitle: Boolean): String {
    var subTitle = ""
    if (showArtistName && song.artistId.toInt() != -1) {
        subTitle = subTitle.plus(song.artistName )
        subTitle = subTitle.plus(" • ")
    }
    if (showAlbumTitle && song.albumId.toInt() != -1) {
        subTitle = subTitle.plus( song.albumTitle )
        subTitle = subTitle.plus(" • ")
    }
    subTitle = subTitle.plus(song.duration.formatStr())
    return subTitle
}
/*
@Composable
private fun SongInfo.setSubTitle(
    showArtistName: Boolean,
    showAlbumTitle: Boolean,
): String {
    //logic to use:
    // if showArtistName and id is valid: show artist name
    // if showAlbumTitle and id is valid: show album title
    // always include duration

    // build subTitle:
    var subTitle = ""
    if (showArtistName && this.artistId.toInt() != -1) {
        subTitle = subTitle.plus(this.artistName + " • ")
    }
    if (showAlbumTitle && this.albumId.toInt() != -1) {
        subTitle = subTitle.plus( this.albumTitle + " • ")
    }
    subTitle = subTitle.plus(this.duration.formatStr())
    return subTitle
}*/


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
            //song = PreviewPlayerSongs[0],
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
            showAlbumTitle = false,
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