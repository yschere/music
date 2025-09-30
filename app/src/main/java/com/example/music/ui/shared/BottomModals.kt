package com.example.music.ui.shared

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.DEFAULT_PADDING
import com.example.music.designsys.theme.ICON_SIZE
import com.example.music.designsys.theme.ITEM_IMAGE_ROW_SIZE
import com.example.music.designsys.theme.LIST_ITEM_HEIGHT
import com.example.music.designsys.theme.MODAL_CONTENT_PADDING
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.player.PlayerModalActions
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.InfoBtn
import com.example.music.util.quantityStringResource

private const val TAG = "Bottom Modal"

/***********************************************************************************************
 *
 * ********** BOTTOM MODAL SUPPORTING COMPOSABLE FUNCTIONS ***********
 *
 **********************************************************************************************/

/**
 * More Options Modal Content - Action Options Row Composable. Contains the Action Item to
 * display and the onClick action to be performed when the row is clicked. ActionItem contains
 * the icon, name, and contentDescription of the action to perform.
 */
@Composable
private fun ActionOptionRow(
    item: ActionItem,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .height(LIST_ITEM_HEIGHT)
            .clickable { onClick() }
            .padding(horizontal = MODAL_CONTENT_PADDING)
    ) {
        Icon(
            imageVector = item.icon,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = stringResource(item.contentDescription),
            modifier = Modifier.padding(SMALL_PADDING)
        )
        Text(
            text = item.name,
            modifier = Modifier.padding(start = CONTENT_PADDING)
        )
    }
}

/**
 * More Options Modal Header
 * @param title name or title of the item
 * @param item the info object to show in the header
 * @param onInfoClick specifically for SongInfo when clicking on the Info icon button
 */
@Composable
private fun MoreOptionModalHeader(
    title: String = "",
    item: Any,
    onInfoClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = MODAL_CONTENT_PADDING, vertical = DEFAULT_PADDING)
    ) {
        // either item image or item first initial
        when (item) {
            is SongInfo -> {
                HeaderImage(item.artworkUri, item.title)
            }
            is PlaylistInfo -> {
                HeaderInitial(item.name)
            }
            is GenreInfo -> {
                HeaderInitial(item.name)
            }
            is ComposerInfo -> {
                HeaderInitial(item.name)
            }
            is ArtistInfo -> {
                HeaderInitial(item.name)
            }
            is AlbumInfo -> {
                HeaderImage(item.artworkUri, item.title)
            }
        }

        // item name/title and item extraInfo
        Column(Modifier.padding(start = CONTENT_PADDING).weight(1f)) {
            Text(
                text = title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = when(item) {
                    is SongInfo -> {
                        item.setSubtitle()
                    }

                    is PlaylistInfo -> {
                        item.setSubtitle()
                    }

                    is ArtistInfo -> {
                        item.setSubtitle()
                    }

                    is AlbumInfo -> {
                        item.setSubtitle()
                    }

                    is ComposerInfo -> {
                        item.setSubtitle()
                    }

                    is GenreInfo -> {
                        item.setSubtitle()
                    }

                    else -> {
                        "" // or some error handling
                    }
                },
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if(item is SongInfo) {
            InfoBtn(onClick = onInfoClick)
            // whatever way to show song details, still not sure how yet
        }
    }
}

/**
 * More Options Modal Header - Header Item Image. Creates the image icon used for
 * SongInfo, AlbumInfo
 */
@Composable
internal fun HeaderImage(
    artworkUri: Uri,
    contentDescription: String = "",
) {
    AlbumImage(
        albumImage = artworkUri,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(ITEM_IMAGE_ROW_SIZE)
            .clip(shapes.small)
    )
}

/**
 * More Options Modal Header - Header Item First Initial. Creates the icon used for
 * ArtistInfo, ComposerInfo, GenreInfo, PlaylistInfo
 */
@Composable
internal fun HeaderInitial(
    name: String = ""
) {
    Box(
        modifier = Modifier
            .size(ICON_SIZE)
            .clip(shapes.small)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ){
        Text(
            text = name[0].toString(),
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 15.dp),
        )
    }
}

/**
 * More Options Modal Header - SongInfo Subtitle text
 */
internal fun SongInfo.setSubtitle(): String =
    if ((this.artistName != "") && (this.albumTitle != ""))
        this.artistName + " • " + this.albumTitle
    else
        this.artistName + this.albumTitle

/**
 * More Options Modal Header - AlbumInfo Subtitle text
 */
@Composable
private fun AlbumInfo.setSubtitle(): String =
    if (this.albumArtistId == null)
        quantityStringResource(R.plurals.songs, this.songCount, this.songCount)
    else
        this.albumArtistName +
            " • " + quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

/**
 * More Options Modal Header - ArtistInfo Subtitle text
 */
@Composable
private fun ArtistInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.albums, this.albumCount, this.albumCount) +
        " • " + quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

/**
 * More Options Modal Header - ComposerInfo Subtitle text
 */
@Composable
private fun ComposerInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

/**
 * More Options Modal Header - GenreInfo Subtitle text
 */
@Composable
private fun GenreInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

/**
 * More Options Modal Header - PlaylistInfo Subtitle text
 */
@Composable
private fun PlaylistInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

@Composable
private fun CustomDragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = DEFAULT_PADDING)
            .width(32.dp)
            .height(SMALL_PADDING)
            .clip(shapes.small)
            .background(MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
private fun RadioGroupSet(
    radioOptions: List<String>,
    //radio button content: @Composable () -> Unit,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.selectableGroup()
    ) {
        radioOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .height(LIST_ITEM_HEIGHT)
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = MODAL_CONTENT_PADDING)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier.padding(SMALL_PADDING),
                    onClick = null, // null recommended for accessibility with screen readers
                )
                // radio button content here
                Text(
                    text = option,
                    color =
                    if (option == selectedOption) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = CONTENT_PADDING),
                )
            }
        }
    }
}

@Composable
private fun CloseModalBtn(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = CircleShape,
        modifier = modifier
            .fillMaxWidth()
            .padding(CONTENT_PADDING)
    ) {
        Text(text)
    }
}

@Composable
private fun ApplyModalBtn(
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = buttonColors(
            contentColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = CircleShape,
        modifier = modifier
            .fillMaxWidth()
            .padding(CONTENT_PADDING)
    ) {
        Text(text)
    }
}


/***********************************************************************************************
 *
 * ********** MORE OPTIONS BOTTOM MODALS ***********
 *
 **********************************************************************************************/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    song: SongInfo,
    songActions: SongActions,
    onClose: () -> Unit = {},
    context: String = "",
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            MoreOptionModalHeader(song.title, song)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val actions = arrayListOf(
                Pair(Actions.PlayItem, songActions.play),
                Pair(Actions.PlayItemNext, songActions.playNext),
                //Pair(Actions.AddToPlaylist, songActions.addToPlaylist),
                Pair(Actions.AddToQueue, songActions.addToQueue),
            )

            actions.forEach { item -> ActionOptionRow(item.first, item.second) }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            // if the song has an artist name and current screen is not ArtistDetails
            if (song.artistName != "" && context != "ArtistDetails")
                ActionOptionRow(Actions.GoToArtist, songActions.goToArtist)

            // if the song has an album title and current screen is not AlbumDetails
            if (song.albumTitle != "" && context != "AlbumDetails")
                ActionOptionRow(Actions.GoToAlbum, songActions.goToAlbum)

            /* //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //ActionOptionRow( Pair(Actions.EditSongTags) {} ) //onClick action would go into lambda edit song tags

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //if (context == "PlaylistDetails")
                //ActionOptionRow( Pair(Actions.RemoveFromPlaylist) {} )
            //if (context == "Queue")
                //ActionOptionRow( Pair(Actions.RemoveFromQueue) {} )
            //ActionOptionRow( Pair(Actions.DeleteFromLibrary) {} ) */

            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    album: AlbumInfo,
    albumActions: AlbumActions,
    onClose: () -> Unit = {},
    context: String = "",
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            MoreOptionModalHeader(album.title, album)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val actions = arrayListOf(
                Pair(Actions.PlayItem, albumActions.play),
                Pair(Actions.PlayItemNext, albumActions.playNext),
                Pair(Actions.ShuffleItem, albumActions.shuffle),
                //Pair(Actions.AddToPlaylist, albumActions.addToPlaylist),
                Pair(Actions.AddToQueue, albumActions.addToQueue),
            )

            actions.forEach { item -> ActionOptionRow(item.first, item.second) }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            // if album has album artist and not already on ArtistDetails screen
            if (album.albumArtistId != null && context != "ArtistDetails")
                ActionOptionRow(Actions.GoToAlbumArtist, albumActions.goToAlbumArtist)

            // if in artistDetails, in library.Albums,
            if (context != "AlbumDetails")
                ActionOptionRow(Actions.GoToAlbum, albumActions.goToAlbum)

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //ActionOptionRow( Actions.EditAlbumTags, {} )

            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    artist: ArtistInfo,
    artistActions: ArtistActions,
    onClose: () -> Unit = {},
    context: String = "",
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
        ) {
            MoreOptionModalHeader(artist.name, artist)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val actions = arrayListOf(
                Pair(Actions.PlayItem, artistActions.play),
                Pair(Actions.PlayItemNext, artistActions.playNext),
                Pair(Actions.ShuffleItem, artistActions.shuffle),
                //Pair(Actions.AddToPlaylist, artistActions.addToPlaylist),
                Pair(Actions.AddToQueue, artistActions.addToQueue),
            )

            actions.forEach { item -> ActionOptionRow(item.first, item.second) }

            // if on library.artists
            if (context != "ArtistDetails") {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
                )
                ActionOptionRow(Actions.GoToArtist, artistActions.goToArtist)
            }

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //ActionOptionRow( Actions.EditArtistTags, {} ) //onClick action in the lambda
            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposerMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    composer: ComposerInfo,
    composerActions: ComposerActions,
    onClose: () -> Unit = {},
    context: String = "",
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
        ) {
            MoreOptionModalHeader(composer.name, composer)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val actions = listOf(
                Pair(Actions.PlayItem, composerActions.play),
                Pair(Actions.PlayItemNext, composerActions.playNext),
                Pair(Actions.ShuffleItem, composerActions.shuffle),
                //Pair(Actions.AddToPlaylist, composerActions.addToPlaylist),
                Pair(Actions.AddToQueue, composerActions.addToQueue),
            )

            // action items, shown items are dependent on this being a song item
            actions.forEach { item -> ActionOptionRow(item.first, item.second) }


            // if on library.composers
            if (context != "ComposerDetails") {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
                )
                ActionOptionRow(Actions.GoToComposer, composerActions.goToComposer)
            }
            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //ActionOptionRow( Actions.EditComposerTags, {} )

            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    genre: GenreInfo,
    genreActions: GenreActions,
    onClose: () -> Unit = {},
    context: String = "",
) {
    BottomModal (
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier,
        ) {
            MoreOptionModalHeader(genre.name, genre)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val actions = listOf(
                Pair(Actions.PlayItem, genreActions.play),
                Pair(Actions.PlayItemNext, genreActions.playNext),
                Pair(Actions.ShuffleItem, genreActions.shuffle),
                //Pair(Actions.AddToPlaylist, genreActions.addToPlaylist),
                Pair(Actions.AddToQueue, genreActions.addToQueue),
            )

            // action items, shown items are dependent on this being a song item
            actions.forEach { item -> ActionOptionRow(item.first, item.second) }

            // if on library.genres
            if (context != "GenreDetails") {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
                )
                ActionOptionRow(Actions.GoToGenre, genreActions.goToGenre)
            }

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //ActionOptionRow(Actions.EditGenreTags, {})

            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    playlist: PlaylistInfo,
    playlistActions: PlaylistActions,
    onClose: () -> Unit = {},
    context: String = "",
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            MoreOptionModalHeader(playlist.name, playlist)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val actions = listOf(
                Pair(Actions.PlayItem, playlistActions.play),
                Pair(Actions.PlayItemNext, playlistActions.playNext),
                Pair(Actions.ShuffleItem, playlistActions.shuffle),
                //Pair(Actions.AddToPlaylist, playlistActions.addToPlaylist),
                Pair(Actions.AddToQueue, playlistActions.addToQueue),
            )

            // action items, shown items are dependent on this being a song item
            actions.forEach { item -> ActionOptionRow(item.first, item.second) }

            // if on library.playlists
            if (context != "PlaylistDetails") {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
                )
                ActionOptionRow(Actions.GoToPlaylist, playlistActions.goToPlaylist)
            }

            /* //ActionOptionRow( Actions.EditPlaylistTags, {} )
            //ActionOptionRow( Actions.EditPlaylistOrder, {} )

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING))
            //ActionOptionRow( Actions.ExportPlaylist, {} )
            //ActionOptionRow( Actions.DeletePlaylist, {} ) */

            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    song: SongInfo,
    playerModalActions: PlayerModalActions,
    onClose: () -> Unit = {},
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
        ) {
            MoreOptionModalHeader(song.title, song)
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val songActions = arrayListOf(
                //Pair(Actions.PlayItemNext, {}),
                //Pair(Actions.AddToPlaylist, {}),
                Pair(Actions.GoToArtist, playerModalActions.goToArtist),
                Pair(Actions.GoToAlbum, playerModalActions.goToAlbum),
            )

            songActions.forEach { item -> ActionOptionRow(item.first, item.second) }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            val queueActions = arrayListOf(
                Pair(Actions.ClearQueue, playerModalActions.clearQueue),
                Pair(Actions.SaveQueueToPlaylist, playerModalActions.saveQueue)
            )

            queueActions.forEach { item -> ActionOptionRow(item.first, item.second) }
            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    queueActions: QueueActions,
    onClose: () -> Unit = {},
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
        ) {
            Text(
                text = "Now Playing",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = MODAL_CONTENT_PADDING, vertical = DEFAULT_PADDING)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            ActionOptionRow(Actions.AddToPlaylist, queueActions.addToPlaylist)
            ActionOptionRow(Actions.SaveQueueToPlaylist, queueActions.saveQueueToPlaylist)
            ActionOptionRow(Actions.ClearQueue, queueActions.clearQueue)

            CloseModalBtn(
                onClick = onClose,
                text = "CLOSE"
            )
        }
    }
}


/***********************************************************************************************
 *
 * ********** OBJECT SORTING BOTTOM MODALS ***********
 *
 **********************************************************************************************/

/**
 * Bottom Modal for Library Screen to use for sorting on the library list's Sort btn
 * @param libraryCategory the library tab context for the items to sort
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySortSelectionBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    onClose: () -> Unit = {},
    onApply: () -> Unit = {},
    libraryCategory: LibraryCategory,
){
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            Text(
                text = "Sort by:",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = MODAL_CONTENT_PADDING, vertical = DEFAULT_PADDING)
            )

            //list of radio buttons, set of options determined by context
            when (libraryCategory) {

                //sorting on library.albums screen
                LibraryCategory.Albums -> {
                    RadioGroupSet(
                        listOf(
                            "Title",
                            "Album Artist",
                            "Date Last Played",
                            "Song Count"
                        )
                    )
                }

                //sorting on library.artists screen
                LibraryCategory.Artists -> {
                    RadioGroupSet(
                        listOf(
                            "Name",
                            "Album Count",
                            "Song Count"
                        )
                    )
                }

                //sorting on library.composers screen
                LibraryCategory.Composers -> { RadioGroupSet(listOf("Name", "Song Count")) }

                //sorting on library.genres screen
                LibraryCategory.Genres -> { RadioGroupSet(listOf("Name", "Song Count")) }

                //sorting on library.playlists screen
                LibraryCategory.Playlists -> {
                    RadioGroupSet(
                        listOf(
                            "Playlist name",
                            "Date created",
                            "Date last accessed",
                            "Date last played",
                            "Song Count"
                        )
                    )
                }

                //sorting on library.songs screen
                LibraryCategory.Songs -> {
                    RadioGroupSet(
                        listOf(
                            "Song title",
                            "Artist name",
                            "Album title",
                            "Date added",
                            "Date last played"
                        )
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            // radio buttons for selecting ascending or descending
            RadioGroupSet(listOf("Ascending", "Descending"))

            Row {
                CloseModalBtn(
                    onClick = onClose,
                    text = "CANCEL",
                    modifier = Modifier.weight(0.5f)
                )
                ApplyModalBtn(
                    onClick = onApply,
                    text = "APPLY",
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}

/**
 * Bottom Modal for Details Screens to use for selected Details item's Sort btn
 * @param content the object type of the items to sort // item(s) to be sorted
 * @param context the screen context // screen containing item(s) to sort
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSortSelectionBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    onClose: () -> Unit = {},
    onApply: () -> Unit = {},
    content: String = "",
    context: String = "",
){
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            Text(
                text = "Sort by:",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = MODAL_CONTENT_PADDING, vertical = DEFAULT_PADDING)
            )

            //list of radio buttons, set of options determined by content and context
            when (content) {
                "AlbumInfo" -> {
                    //sorting on artist details -> album list screen
                    RadioGroupSet(listOf("Album Title", "Song Count"))
                }

                "SongInfo" -> {
                    when (context) {
                        //sorting on album details screen -> "Title, Track Number" -found in albumRepo, songRepo
                        "AlbumDetails" -> { RadioGroupSet(listOf("Title", "Track number")) }

                        //sorting on artist details screen -> "Song Title, Album Title" -songRepo
                        "ArtistDetails" -> { RadioGroupSet(listOf("Title", "Album Title")) }

                        //sorting on composer details screen -> "Title -songRepo, date last played -composerRepo
                        "ComposerDetails" -> { RadioGroupSet(listOf("Title", "Date Last Played")) }

                        //sorting on genre details screen -> "Title, date last played -songRepo, genreRepo
                        "GenreDetails" -> { RadioGroupSet(listOf("Title", "Date Last Played")) }

                        //sorting on playlist details screen -> "Title, track number
                        "PlaylistDetails" -> { RadioGroupSet(listOf("Title", "Track number")) }

                        else -> { Text("this is not a valid context screen to pass") }
                    }
                }

                else -> { Text("This is not a valid content type to pass") }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
            )

            //radio buttons for selecting ascending or descending
            RadioGroupSet(listOf("Ascending", "Descending"))

            Row {
                CloseModalBtn(
                    onClick = onClose,
                    text = "CANCEL",
                    modifier = Modifier.weight(0.5f)
                )
                ApplyModalBtn(
                    onClick = onApply,
                    text = "APPLY",
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}


/***********************************************************************************************
 *
 * ********** SETTINGS BOTTOM MODAL ***********
 *
 **********************************************************************************************/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    onClose: () -> Unit,
    onApply: () -> Unit,
    content: @Composable () -> Unit,
) {
    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        content()

        Row {
            CloseModalBtn(
                onClick = onClose,
                text = "CANCEL",
                modifier = Modifier.weight(0.5f)
            )
            ApplyModalBtn(
                onClick = onApply,
                text = "APPLY",
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}

/***********************************************************************************************
 *
 * ********** CREATE PLAYLIST BOTTOM MODAL ***********
 *
 **********************************************************************************************/

/**
 * Bottom modal for creating Playlists, from either Add to playlist -> New playlist prompt
 * or on Library.Playlists -> Add btn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    onClose: () -> Unit = {},
    onCreate: () -> Unit = {},
) {
    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    val state = rememberTextFieldState()

    // required text field validation functions
    val createEnabled = remember { derivedStateOf { nameText.isNotEmpty() } }

    LaunchedEffect(Unit) {
        snapshotFlow { state.text }.collect{
            Log.i(TAG, "current text is: ${it.chars()}")
        }
    }

    BottomModal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = MODAL_CONTENT_PADDING)
        ) {
            Text(
                text = "Create New Playlist",
                modifier = Modifier.padding(start = CONTENT_PADDING),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge,
            )

            //playlist name text field, REQUIRED
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = DEFAULT_PADDING, vertical = SMALL_PADDING),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text("Playlist Name") },
            )

            //playlist description text field, NOT REQUIRED
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                singleLine = true,
                maxLines = 3,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = DEFAULT_PADDING, vertical = SMALL_PADDING),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                label = { Text("Description") },
            )

            Row {
                CloseModalBtn(
                    onClick = onClose,
                    text = "CANCEL",
                    modifier = Modifier.weight(0.5f)
                )
                ApplyModalBtn(
                    onClick = onCreate,
                    enabled = createEnabled.value,
                    text = "CREATE",
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}


/***********************************************************************************************
 *
 * ********** BASE BOTTOM MODAL ***********
 *
 **********************************************************************************************/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = MaterialTheme.colorScheme.background,
        scrimColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        dragHandle = { CustomDragHandle() },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SystemDarkPreview
@Composable
fun PreviewLibrarySortModal() {
    MusicTheme {
        LibrarySortSelectionBottomModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = true,
                density = Density(1f,1f)
            ),
            libraryCategory = LibraryCategory.Genres,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SystemLightPreview
@Composable
fun PreviewMoreOptionsModal() {
    MusicTheme {
        SongMoreOptionsBottomModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = true,
                density = Density(1f,1f)
            ),
            song = PreviewSongs[0],
            songActions = SongActions(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SystemDarkPreview
@Composable
fun PreviewDetailSortModal() {
    MusicTheme {
        DetailsSortSelectionBottomModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = true,
                density = Density(1f,1f)
            ),
            onClose = {},
            onApply = {},
            content = "SongInfo",
            context = "ComposerDetails"
        )
    }
}