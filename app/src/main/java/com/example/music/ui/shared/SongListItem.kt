/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.ui.shared

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.DensityLarge
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.component.HtmlTextContainer
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.testing.getSongData
import com.example.music.domain.testing.getAlbumData
import com.example.music.domain.testing.getArtistData
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.player.formatString
import com.example.music.ui.theme.MusicTheme
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SongListItem(
    song: SongInfo,
    //artist: ArtistInfo,
    album: AlbumInfo,
    onClick: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    showDuration: Boolean,
    modifier: Modifier = Modifier,
) {
    //want one row to show all items
    //want song title on separate row from extra info items (artist, album, duration)
    //need to include show__ boolean options with Item, like showAlbumImage and showSummary
    //remove the play and queue buttons, shift the album image to the far left, keep moreVert on the far right

    Box(modifier = modifier.padding(8.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            //color = MaterialTheme.colorScheme.background,
            color = MaterialTheme.colorScheme.surfaceContainer, //FOR SOME REASON surfaceContainer is the only one that changes with light/dark mode
            onClick = { onClick(song) },
            //modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {

            //TODO: determine if the call to SongListItem is for every individual item at a time or is being done on the list as a whole
            //TODO: this would change whether or not the item row call
            // has a Column surrounding it HERE or
            // if that Column has to be around every invocation of SongListItem
            // currently have AlbumDetailsScreen call this within its viewModel which has it sitting inside a LazyVerticalGrid
            SongListItemRow(
                    song = song,
                    //artist = artist,
                    album = album,
                    onClick = onClick,
                    isListEditable = isListEditable,
                    showArtistName = showArtistName,
                    showAlbumImage = showAlbumImage,
                    showAlbumTitle = showAlbumTitle,
                    showDuration = showDuration,
                    modifier = modifier,
                )

            //this iteration had the padding within songlistitemrow as part of the row surrounding the call itself
//            Row(
//                //modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
//            ) {
//                SongListItemRow(
//                    song = song,
//                    artist = artist,
//                    album = album,
//                    onClick = onClick,
//                    isListEditable = false,
//                    showArtistName = true,
//                    showAlbumImage = true,
//                    showAlbumTitle = true,
//                    showDuration = true,
//                    modifier = modifier,
//                )
//            }

            //this is the original iteration of song list item that is the episode item modified
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//            ) {
//                // Top Part
//                SongListItemHeader(
//                    song = song,
//                    album = album,
//                    showAlbumImage = showAlbumImage,
//                    showSummary = showSummary,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//
//                // Bottom Part
//                SongListItemFooter(
//                    song = song,
//                    album = album,
//                    onQueueSong = onQueueSong,
//                )
//            }
        }
    }
}

@Composable
private fun SongListItemRow(
    song: SongInfo,
    //artist: ArtistInfo,
    album: AlbumInfo,
    onClick: (SongInfo) -> Unit,
    isListEditable: Boolean,
    showArtistName: Boolean,
    showAlbumImage: Boolean,
    showAlbumTitle: Boolean,
    showDuration: Boolean,
    modifier: Modifier = Modifier,
) {
    //for now keep the list of properties as is
    //later want to be able to support having the context of where
    //the item is being viewed as what determines which properties get shown
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        //want the logic to be:
            //want to show Album Image on NOT albumlist screens, NOT edit list screen. so expect showAlbumImage to be false coming from AlbumDetailsScreen or any EditListScreen
            //so should be true for GenreScreen, ArtistScreen, SongsList, PlaylistScreen, (?)ComposerScreen
            //AND if showAlbumImage is true, then isListEditable should be false
            //then if showAlbumImage is false, use else to check if isListEditable is true. if true, place edit icon btn where image would be
            //if isListEditable is false, show trackNumber?? not sure what to do here

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
                    enabled = isListEditable,
                    modifier = Modifier,
                ) {
                    Icon( //more options icon
                        imageVector = Icons.Outlined.Reorder,
                        contentDescription = stringResource(R.string.cd_more),
                        //tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                //show the item's track number i guess?
                Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
                Text(
                    text = song.albumTrackNumber.toString(),
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
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                if (showArtistName) { //if showArtistName is true
                    Text(
                        text = getArtistData(song.artistId!!).name,
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
                } else { //if showArtist is false, show nothing?? //TODO

                }
                if (showDuration) { //(showDuration) { //if showDuration is true
                    Text(
                        text = if (showArtistName || showAlbumTitle) " • " + song.duration!!.formatStr() else song.duration!!.formatStr(),
                        maxLines = 1,
                        minLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)//, horizontal = 8.dp),
                    )

                } else { //if showDuration is false, show nothing?? //TODO
//                    val duration = song.duration
//                    Text(
//                        text = when {
//                            duration != null -> {
//                                // If we have the duration, we combine the date/duration via a
//                                // formatted string
//                                stringResource(
//                                    R.string.song_date_duration,
//                                    duration.toMinutes().toInt()
//                                )
//                            }
//                            // Otherwise we just use the date
//                            else -> MediumDateFormatter.format(song.dateLastPlayed)
//                        },
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier
//                            .padding(horizontal = 8.dp)
//                            .weight(1f)
//                    )

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
private fun SongListItemFooter(
    song: SongInfo,
    album: AlbumInfo,
    onQueueSong: (PlayerSong) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp)
                ) { /* TODO */ }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
        )

        val duration = song.duration
        Text(
            text = when {
                duration != null -> {
                    // If we have the duration, we combine the date/duration via a
                    // formatted string
                    stringResource(
                        R.string.song_date_duration,
                        MediumDateFormatter.format(song.dateLastPlayed),
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

        IconButton(
            onClick = {
                onQueueSong(
                    PlayerSong(
                        albumInfo = album,
                        songInfo = song,
                    )
                )
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add),
                //tint = MaterialTheme.colorScheme.onSurfaceVariant,
                tint = MaterialTheme.colorScheme.error,
            )
        }

        IconButton(
            onClick = { /* TODO */ },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
                //tint = MaterialTheme.colorScheme.onSurfaceVariant,
                tint = MaterialTheme.colorScheme.primaryContainer,
            )
        }
    }
}

@Composable
private fun SongListItemHeader(
    song: SongInfo,
    album: AlbumInfo,
    showAlbumImage: Boolean,
    showSummary: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(
            modifier =
            Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = song.title,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            if (showSummary) { //was episode.summary
                HtmlTextContainer(text = song.albumTrackNumber.toString()) {
                    Text(
                        text = it,
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            } else {
                Text(
                    text = album.title,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        if (showAlbumImage) {
            SongListItemImage(
                album = album,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
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

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Preview( name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES )
@Composable
private fun SongListItem_GeneralPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[0],
            //artist = PreviewArtists[0],
            album = PreviewAlbums[0],
            onClick = {},
            onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showDuration = true,
            //modifier = Modifier,
        )
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Composable
private fun SongListItem_AlbumDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[5],
            //artist = PreviewArtists[1],
            album = PreviewAlbums[4],
            onClick = {},
            onQueueSong = {},
            isListEditable = false,
            showAlbumImage = false,
            showArtistName = false,
            showAlbumTitle = true,
            showDuration = true,
            //modifier = Modifier,
        )
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Composable
private fun SongListItem_AllSongsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[8],
            //artist = PreviewArtists[3],
            album = PreviewAlbums[6],
            onClick = {},
            onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showDuration = true,
            //modifier = Modifier,
        )
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Composable
private fun SongListItem_PlaylistDetailsPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[3],
            //artist = PreviewArtists[2],
            album = PreviewAlbums[2],
            onClick = {},
            onQueueSong = {},
            isListEditable = false,
            showAlbumImage = true,
            showArtistName = true,
            showAlbumTitle = true,
            showDuration = true,
            //modifier = Modifier,
        )
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Composable
private fun SongListItem_EditListPreview() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[2],
            //artist = PreviewArtists[2],
            album = PreviewAlbums[2],
            onClick = {},
            onQueueSong = {},
            isListEditable = true,
            showAlbumImage = false,
            showArtistName = true,
            showAlbumTitle = false,
            showDuration = true,
            //modifier = Modifier,
        )
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}

fun Duration.formatStr(): String {
    val minutes = this.toMinutes().toString().padStart(1, '0')
    val seconds = (this.toSeconds() % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}