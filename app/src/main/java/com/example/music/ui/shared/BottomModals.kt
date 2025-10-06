package com.example.music.ui.shared

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.data.repository.AlbumSortList
import com.example.music.data.repository.albumSortOrderList
import com.example.music.data.repository.artistSortOrderList
import com.example.music.data.repository.composerSortOrderList
import com.example.music.data.repository.genreSortOrderList
import com.example.music.data.repository.playlistSortOrderList
import com.example.music.data.repository.songSortOrderList
import com.example.music.data.util.FLAG
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.DEFAULT_PADDING
import com.example.music.designsys.theme.ICON_SIZE
import com.example.music.designsys.theme.ITEM_IMAGE_ROW_SIZE
import com.example.music.designsys.theme.LIST_ITEM_HEIGHT
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.albumdetails.AlbumSongSortOptions
import com.example.music.ui.artistdetails.ArtistAlbumSortOptions
import com.example.music.ui.artistdetails.ArtistSongSortOptions
import com.example.music.ui.genredetails.GenreSongSortOptions
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.player.PlayerModalActions
import com.example.music.ui.playlistdetails.PlaylistSongSortOptions
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.frontTextPadding
import com.example.music.util.listItemIconMod
import com.example.music.util.modalHeaderPadding
import com.example.music.util.modalPadding
import com.example.music.util.quantityStringResource
import com.example.music.util.textHeightPadding

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
            .modalPadding()
    ) {
        Icon(
            imageVector = item.icon,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = stringResource(item.contentDescription),
            modifier = Modifier.padding(SMALL_PADDING)
        )
        Text(
            text = item.name,
            modifier = Modifier.frontTextPadding()
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
        modifier = Modifier.fillMaxWidth().modalHeaderPadding(),
    ) {
        // either item image or item first initial
        when (item) {
            is SongInfo -> { HeaderImageIcon(item.artworkUri, item.title) }
            is PlaylistInfo -> { HeaderInitialIcon(item.name) }
            is GenreInfo -> { HeaderInitialIcon(item.name) }
            is ComposerInfo -> { HeaderInitialIcon(item.name) }
            is ArtistInfo -> { HeaderInitialIcon(item.name) }
            is AlbumInfo -> { HeaderImageIcon(item.artworkUri, item.title) }
        }

        // item name/title and item extraInfo
        Column(Modifier.frontTextPadding().weight(1f)) {
            Text(
                text = title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = when(item) {
                        is SongInfo -> { item.setSubtitle() }
                        is PlaylistInfo -> { item.setSubtitle() }
                        is ArtistInfo -> { item.setSubtitle() }
                        is AlbumInfo -> { item.setSubtitle() }
                        is ComposerInfo -> { item.setSubtitle() }
                        is GenreInfo -> { item.setSubtitle() }
                        else -> { "" /* TODO need some error handling */ }
                    },
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.textHeightPadding(),
                )
            }
        }

        if(item is SongInfo) { InfoBtn(onClick = onInfoClick) }
        // still not sure how to show song details with onInfoClick
    }
}

/**
 * More Options Modal Header - Header Item Image. Creates the image icon used for
 * SongInfo, AlbumInfo
 */
@Composable
internal fun HeaderImageIcon(
    artworkUri: Uri,
    contentDescription: String = "",
) {
    AlbumImage(
        albumImage = artworkUri,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.listItemIconMod(ITEM_IMAGE_ROW_SIZE, MaterialTheme.shapes.small)
    )
}

/**
 * More Options Modal Header - Header Item First Initial. Creates the icon used for
 * ArtistInfo, ComposerInfo, GenreInfo, PlaylistInfo
 */
@Composable
internal fun HeaderInitialIcon(
    name: String = "",
) {
    Row(
        modifier = Modifier.listItemIconMod(ICON_SIZE, MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ){
        Text(
            text = name[0].toString(),
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.CenterVertically)
        )
    }
}

/**
 * More Options Modal Header - SongInfo Subtitle text
 */
internal fun SongInfo.setSubtitle(): String =
    if ((this.artistName != "") && (this.albumTitle != ""))
        this.artistName + " • " + this.albumTitle
    else this.artistName + this.albumTitle

/**
 * More Options Modal Header - AlbumInfo Subtitle text
 */
@Composable
private fun AlbumInfo.setSubtitle(): String =
    if (this.albumArtistId == null) quantityStringResource(R.plurals.songs, this.songCount, this.songCount)
    else this.albumArtistName + " • " + quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

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
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
private fun RadioGroupSet(
    radioOptions: List<String>,
    initialValue: String,
    onOptionSelect: (String) -> Unit = {},
    //radio button content: @Composable () -> Unit,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[radioOptions.indexOf(initialValue)]) }
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
                        onClick = {
                            Log.i(TAG, "current option: $option")
                            onOptionSelected(option)
                            onOptionSelect(option)
                        },
                        role = Role.RadioButton
                    )
                    .modalPadding()
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
                    modifier = Modifier.frontTextPadding(),
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
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                modifier = Modifier.modalPadding()
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
                modifier = Modifier.modalPadding()
            )

            // if the song has an artist name and current screen is not ArtistDetails
            if (song.artistName != "" && context != "ArtistDetails")
                ActionOptionRow(Actions.GoToArtist, songActions.goToArtist)

            // if the song has an album title and current screen is not AlbumDetails
            if (song.albumTitle != "" && context != "AlbumDetails")
                ActionOptionRow(Actions.GoToAlbum, songActions.goToAlbum)

            /* //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
            //ActionOptionRow( Pair(Actions.EditSongTags) {} ) //onClick action would go into lambda edit song tags

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
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
                modifier = Modifier.modalPadding()
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
                modifier = Modifier.modalPadding()
            )

            // if album has album artist and not already on ArtistDetails screen
            if (album.albumArtistId != null && context != "ArtistDetails")
                ActionOptionRow(Actions.GoToAlbumArtist, albumActions.goToAlbumArtist)

            // if in artistDetails, in library.Albums,
            if (context != "AlbumDetails")
                ActionOptionRow(Actions.GoToAlbum, albumActions.goToAlbum)

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
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
                modifier = Modifier.modalPadding()
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
                    modifier = Modifier.modalPadding()
                )
                ActionOptionRow(Actions.GoToArtist, artistActions.goToArtist)
            }

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
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
                modifier = Modifier.modalPadding()
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
                    modifier = Modifier.modalPadding()
                )
                ActionOptionRow(Actions.GoToComposer, composerActions.goToComposer)
            }
            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
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
                modifier = Modifier.modalPadding()
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
                    modifier = Modifier.modalPadding()
                )
                ActionOptionRow(Actions.GoToGenre, genreActions.goToGenre)
            }

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
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
                modifier = Modifier.modalPadding()
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
                    modifier = Modifier.modalPadding()
                )
                ActionOptionRow(Actions.GoToPlaylist, playlistActions.goToPlaylist)
            }

            /* //ActionOptionRow( Actions.EditPlaylistTags, {} )
            //ActionOptionRow( Actions.EditPlaylistOrder, {} )

            //HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.modalPadding())
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
                modifier = Modifier.modalPadding()
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
                modifier = Modifier.modalPadding()
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

    //queueList
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
//            Text(
//                text = "Now Playing",
//                textAlign = TextAlign.Left,
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.fillMaxWidth().modalHeaderPadding(),
//            )
//            HorizontalDivider(
//                thickness = 1.dp,
//                color = Color.Gray,
//                modifier = Modifier.modalPadding()
//            )

            val actions = arrayListOf(
                Pair(Actions.ClearQueue, queueActions.clearQueue),
                Pair(Actions.SaveQueueToPlaylist, queueActions.saveQueueToPlaylist)
            )

            actions.forEach { item -> ActionOptionRow(item.first, item.second) }
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
 * @param currSortPair the currently selected sort order for modal to show
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySortSelectionBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    onClose: () -> Unit = {},
    onApply: (String, Boolean) -> Unit = {_, _ -> },
    libraryCategory: LibraryCategory,
    currSortPair: Pair<String, Boolean>,
){
    // temporary variables for bottom modal to store sort pair values
    var sortColumn = currSortPair.first
    var isAscending = currSortPair.second

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
                modifier = Modifier.fillMaxWidth().modalHeaderPadding(),
            )

            if (FLAG) Log.i(TAG, "Library Sort Modal:\n" +
                    "Library Tab -> $libraryCategory\n" +
                    "Sort pair -> $sortColumn + $isAscending")

            when (libraryCategory) {
                LibraryCategory.Albums -> {
                    RadioGroupSet(
                        radioOptions = AlbumSortList,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
                LibraryCategory.Artists -> {
                    RadioGroupSet(
                        radioOptions = artistSortOrderList,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
                LibraryCategory.Composers -> {
                    RadioGroupSet(
                        radioOptions = composerSortOrderList,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
                LibraryCategory.Genres -> {
                    RadioGroupSet(
                        radioOptions = genreSortOrderList,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
                LibraryCategory.Playlists -> {
                    RadioGroupSet(
                        radioOptions = playlistSortOrderList,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
                LibraryCategory.Songs -> {
                    RadioGroupSet(
                        radioOptions = songSortOrderList,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.modalPadding()
            )

            RadioGroupSet(
                radioOptions = listOf("Ascending", "Descending"),
                initialValue = if (currSortPair.second) "Ascending" else "Descending",
                onOptionSelect = { newIsAsc -> isAscending = (newIsAsc == "Ascending") },
            )

            Row {
                CloseModalBtn(
                    onClick = onClose,
                    text = "CANCEL",
                    modifier = Modifier.weight(0.5f)
                )
                ApplyModalBtn(
                    onClick = {
                        Log.i(TAG, "After Apply clicked:\n" +
                            "new sort col: $sortColumn\n" +
                            "new asc/desc: $isAscending")
                        onApply(sortColumn, isAscending)
                    },
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
 * @param currSortPair the currently selected sort order for modal to show
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSortSelectionBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

    onClose: () -> Unit = {},
    onApply: (String, Boolean) -> Unit = {_, _ -> },
    content: String = "",
    context: String = "",
    currSortPair: Pair<String, Boolean>,
){
    // temporary variables for bottom modal to store sort pair values
    var sortColumn = currSortPair.first
    var isAscending = currSortPair.second

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
                modifier = Modifier.fillMaxWidth().modalHeaderPadding(),
            )

            if (FLAG) Log.i(TAG, "Details Sort Modal:\n" +
                "Context -> $context\n" +
                "Content -> $content\n" +
                "Sort pair -> $sortColumn + $isAscending")

            when (content) {
                "AlbumInfo" -> { //sorting on artist details screen -> album carousel
                    RadioGroupSet(
                        radioOptions = ArtistAlbumSortOptions,
                        initialValue = currSortPair.first,
                        onOptionSelect = { newCol -> sortColumn = newCol},
                    )
                }
                "SongInfo" -> {
                    when (context) {
                        "AlbumDetails" -> {
                            RadioGroupSet(
                                radioOptions = AlbumSongSortOptions,
                                initialValue = currSortPair.first,
                                onOptionSelect = { newCol -> sortColumn = newCol},
                            )
                        }
                        "ArtistDetails" -> {
                            RadioGroupSet(
                                radioOptions = ArtistSongSortOptions,
                                initialValue = currSortPair.first,
                                onOptionSelect = { newCol -> sortColumn = newCol},
                            )
                        }
                        "ComposerDetails" -> { //sorting on composer details screen **NOT IN USE
                            RadioGroupSet(
                                radioOptions = listOf(
                                    "Title",
                                    "Artist",
                                    "Album",
                                    "Date Added",
                                    "Date Modified",
                                    "Duration",
                                ),
                                initialValue = currSortPair.first,
                                onOptionSelect = { newCol -> sortColumn = newCol},
                            )
                        }
                        "GenreDetails" -> {
                            RadioGroupSet(
                                radioOptions = GenreSongSortOptions,
                                initialValue = currSortPair.first,
                                onOptionSelect = { newCol -> sortColumn = newCol},
                            )
                        }
                        "PlaylistDetails" -> {
                            RadioGroupSet(
                                radioOptions = PlaylistSongSortOptions,
                                initialValue = currSortPair.first,
                                onOptionSelect = { newCol -> sortColumn = newCol},
                            )
                        }
                        else -> { Text("this is not a valid context screen to pass") }
                    }
                }
                else -> { Text("This is not a valid content type to pass") }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.modalPadding()
            )

            RadioGroupSet(
                radioOptions = listOf("Ascending", "Descending"),
                initialValue = if (currSortPair.second) "Ascending" else "Descending",
                onOptionSelect = { newIsAsc -> isAscending = (newIsAsc == "Ascending") },
            )

            Row {
                CloseModalBtn(
                    onClick = onClose,
                    text = "CANCEL",
                    modifier = Modifier.weight(0.5f)
                )
                ApplyModalBtn(
                    onClick = {
                        Log.i(TAG, "After Apply clicked:\n" +
                                "new sort col: $sortColumn\n" +
                                "new asc/desc: $isAscending")
                        onApply(sortColumn, isAscending)
                    },
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

/**
 * Bottom Modal for Settings Screen to display preference options
 */
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
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            Text(
                text = "Create New Playlist:",
                modifier = Modifier.modalHeaderPadding(),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge,
            )

            //playlist name text field, REQUIRED
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
                    .modalPadding()
                    .padding(SMALL_PADDING),
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
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
                    .modalPadding()
                    .padding(SMALL_PADDING),
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
                    // values of nameText, description should be passed backward to data layer through onCreate
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
private fun BottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha=0.5f),
        dragHandle = { CustomDragHandle() },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SystemDarkPreview
@Composable
fun PreviewSortModal() {
    MusicTheme {
        LibrarySortSelectionBottomModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = true,
                density = Density(1f,1f)
            ),
            libraryCategory = LibraryCategory.Genres,
            currSortPair = Pair("Name",false),
        )
        /*DetailsSortSelectionBottomModal(
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
        )*/
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
fun PreviewCreatePlaylistModal() {
    MusicTheme {
        CreatePlaylistBottomModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = true,
                density = Density(1f,1f)
            ),
            onClose = {},
            onCreate = {},
        )
    }
}
