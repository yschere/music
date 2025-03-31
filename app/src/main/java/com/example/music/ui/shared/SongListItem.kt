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
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.CompLightPreview
import java.time.Duration

/**
 * Original Song List Item
 */
@Composable
fun SongListItem(
    song: SongInfo,
    //artist: ArtistInfo,
    album: AlbumInfo,
    onClick: (SongInfo) -> Unit,
    //onQueueSong: (PlayerSong) -> Unit,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
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
                    //artist = artist,
                    album = album,
                    isListEditable = isListEditable,
                    showArtistName = showArtistName,
                    showAlbumImage = showAlbumImage,
                    showAlbumTitle = showAlbumTitle,
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
@Composable
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
}

/**
 * Original Song List Item Content
 */
@Composable
private fun SongListItemRow(
    song: SongInfo,
    //artist: ArtistInfo,
    album: AlbumInfo,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    modifier: Modifier = Modifier,
) {
    //for now keep the list of properties as is
    //later want to be able to support having the context of where
    //the item is being viewed as what determines which properties get shown
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        /* //want the logic to be:
            //want to show Album Image on NOT album list screens, NOT edit list screen. so expect showAlbumImage to be false coming from AlbumDetailsScreen or any EditListScreen
            //so should be true for GenreScreen, ArtistScreen, SongsList, PlaylistScreen, (?)ComposerScreen
            //AND if showAlbumImage is true, then isListEditable should be false
            //then if showAlbumImage is false, use else to check if isListEditable is true. if true, place edit icon btn where image would be
            //if isListEditable is false, show trackNumber?? not sure what to do here
         */

        if (showAlbumImage) {
            SongListItemImage(
                album = album,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        } else {
            if (isListEditable) {
                Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
                IconButton(
                    onClick = {},
                    //enabled = isListEditable,
                    modifier = Modifier,
                ) {
                    Icon( //item sort icon, when item will be draggable TBD
                        imageVector = Icons.Outlined.Reorder,
                        contentDescription = stringResource(R.string.icon_reorder),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            } else {
                //show the item's track number i guess?
                Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
                Text(
                    text = song.trackNumber.toString(),
                    minLines = 1,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                )
            }
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
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                if (showArtistName) { //if showArtistName is true
                    Text(
                        text = getArtistData(song.artistId!!).name,//artist.name,//
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                } else { //if showArtist is false, show nothing?? //TODO

                }
                if (showAlbumTitle) {//(showArtistName) { //if showArtistName is true
                    Text(
                        text = if (showArtistName) " • " + album.title else album.title,
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
                Text(
                    text = if (showArtistName || showAlbumTitle) " • " + song.duration.formatStr() else song.duration.formatStr(),
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)//, horizontal = 8.dp),
                )

            }

        }

        IconButton( //more options button
            //modifier = Modifier.padding(0.dp),
            onClick = { /* TODO */ }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * PlayerSong supported version
 */
@Composable
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
                album = song.albumTitle,
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
}

@Composable
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
}

@Composable
private fun SongListItemImage(
    album: String,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        //albumImage = album.artwork!!,
        //albumImage = R.drawable.bpicon,
        contentDescription = null,
        modifier = modifier,
    )
}

@CompLightPreview
@Composable
private fun SongListItem_GeneralPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewPlayerSongs[0],
            //artist = PreviewArtists[0],
            //album = PreviewAlbums[0],
            onClick = {},
            //onQueueSong = {},
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            //modifier = Modifier,
        )
    }
}

@CompDarkPreview
@Composable
private fun SongListItem_ShowAllPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewPlayerSongs[0],
            //artist = PreviewArtists[0],
            //album = PreviewAlbums[0],
            onClick = {},
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

@CompLightPreview
@Composable
private fun SongListItem_AlbumDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewPlayerSongs[5],
            onClick = {},
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
            song = PreviewPlayerSongs[8],
            //artist = PreviewArtists[3],
            //album = PreviewAlbums[6],
            onClick = {},
            //onQueueSong = {},
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            //modifier = Modifier,
        )
    }
}

//@CompLightPreview
@Composable
private fun SongListItem_PlaylistDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewPlayerSongs[3],
            //artist = PreviewArtists[2],
            //album = PreviewAlbums[2],
            onClick = {},
            //onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            //modifier = Modifier,
        )
    }
}

//@CompLightPreview
@CompDarkPreview
@Composable
private fun SongListItem_EditListPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewPlayerSongs[2],
            //artist = PreviewArtists[2],
            //album = PreviewAlbums[2],
            onClick = {},
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