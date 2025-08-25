package com.example.music.ui.shared

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.testing.getSongData
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.player.PlayerSlider
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration

/** Changelog:
 *
 * 4/2/2025 - Revised PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong. This doesn't mean full
 * removal like the other, non-Player screens because Bottom Modals also contains
 * BottomPlayer, which does need PlayerSong as the supporting model.
 *
 * 4/9/2025 - Updated AlbumMoreOptions, ComposerMoreOptions, GenreMoreOptions, PlaylistMoreOptions
 * to use ActionOptionRow for displaying their individual object's actions. And cleaned up some
 * of the commented out code that's not needed anymore.
 *
 * 4/14/2024 - Created CustomDragHandle to create a drag handle that takes up less space.
 * Updated the bottom modals' padding for column structure and list items so the onPress, onClick
 * highlight covers the full width of the item.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

//More Options Modal Content - Action Options Row Composable
// contains the Action Item to display and the onClick action to be performed when the row is clicked
// ActionItem contains the icon, name, and contentDescription of the action to perform
@Composable
fun ActionOptionRow(
    item: Pair<ActionItem, ()->Unit>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.second }
            .height(56.dp)
            .padding(horizontal = 24.dp)
    ) {
        Icon(
            imageVector = item.first.icon,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = stringResource(item.first.contentDescription),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = item.first.name,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

//More Options Modal Header
@Composable
fun MoreOptionModalHeader(
    title: String = "", // item's name or title
    item: Any, // one of the info items ... should these Info types actually come from a base class?
    //contentDescription: String = "", // also likely item's name or title, currently an item's artwork
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
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
        Column(Modifier.padding(8.dp).weight(1f)) {
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
                        //songSubtext(item)
                    }

                    is PlaylistInfo -> {
                        item.setSubtitle()
                        //songCountSubtext(item)
                    }

                    is ArtistInfo -> {
                        item.setSubtitle()
                        //artistSubtext(item)
                    }

                    is AlbumInfo -> {
                        item.setSubtitle()
                        //songCountSubtext(item)
                    }

                    is ComposerInfo -> {
                        item.setSubtitle()
                        //songCountSubtext(item)
                    }

                    is GenreInfo -> {
                        item.setSubtitle()
                        //songCountSubtext(item)
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

        // if item is a PlayerSong, include info icon to pop up song details
        if( item is SongInfo ) {
            IconButton( onClick = {} ) { // whatever way to show song details, still not sure how yet
                Icon(
                    imageVector = Icons.Filled.Info,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = stringResource(R.string.icon_song_details),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

//More Options Modal Header - Header Item Image
// used for SongInfo, PlayerSong, AlbumInfo (maybe should also be fore PlaylistInfo)
@Composable
fun HeaderImage(
    artworkUri: Uri,
    contentDescription: String = "",
) {
    AlbumImage(
        albumImage = artworkUri, //when this is working, it should be the artwork
        contentDescription = contentDescription, //song.artwork or song.title when this is working
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(56.dp)
            .clip(MusicShapes.medium) // or extraSmall
    )
}

//More Options Modal Header - Header Item First Initial
// used for ArtistInfo, ComposerInfo, GenreInfo
@Composable
fun HeaderInitial(
    name: String = ""
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(MusicShapes.medium)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))){
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

//More Options Modal Header - Song Item Subtitle text
fun SongInfo.setSubtitle(): String =
    if ((this.artistName != "") && (this.albumTitle != "")) {
        this.artistName + " • " + this.albumTitle
    } else {
        (this.artistName) + (this.albumTitle)
    }

//More Options Modal Header - Artist Item Subtitle text
@Composable
fun ArtistInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.albums, this.albumCount, this.albumCount) +
            " • " +
            quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

//More Options Modal Header - Playlist Item Subtitle text
@Composable
fun PlaylistInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

//More Options Modal Header - Composer Item Subtitle text
@Composable
fun ComposerInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

//More Options Modal Header - Genre Item Subtitle text
@Composable
fun GenreInfo.setSubtitle(): String =
    quantityStringResource(R.plurals.songs, this.songCount, this.songCount)

//More Options Modal Header - Album Item Subtitle text
@Composable
fun AlbumInfo.setSubtitle(): String =
    if (this.albumArtistId == null)
        quantityStringResource(R.plurals.songs, this.songCount, this.songCount)
    else {
        this.albumArtistName + " • " +
            quantityStringResource(R.plurals.songs, this.songCount, this.songCount)
    }

@Composable
fun CustomDragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .width(30.dp)
            .height(4.dp)
            .clip(MusicShapes.small)
            .background(MaterialTheme.colorScheme.onBackground)
    )
}

/**
 * Other ideas that could be done thru bottom modal
 * -edit Artist / Composer / Genre tags (because its just editing the name
 * -edit Playlist details (depends if description is a thing)
 * -view song info (media metadata)
 */

/** Idea for modal: to show action items like a dropdown menu when an item's more options btn is clicked/pressed.
 *  First, need to be able to call it from any screen that has a more options btn.
 *  Second, need it to show context dependent on the item context
 *  aka what type of object the more options btn press was from.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    song: SongInfo,
    navigateToPlayer: (SongInfo) -> Unit = {},
    // would likely need the other navigateTo screens here too

    //do i need a context variable? like if this was from PlaylistDetails, or ArtistDetails, or Home, or Library
    // could this be done with the navController? or does it have to be a string? or an enum?
    context: String = "",
    //showBottomSheet: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, //{showBottomSheet = false}
        sheetState = sheetState, //rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor, //MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor, //MaterialTheme.colorScheme.background,
        scrimColor = scrimColor, //MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier,
        ) {
            // header section
            MoreOptionModalHeader(song.title, song)

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            val songActions = arrayListOf(
                Pair(Actions.PlayItem) { navigateToPlayer(song) },
                Pair(Actions.PlayItemNext) {},
                Pair(Actions.AddToPlaylist) {},
                Pair(Actions.AddToQueue) {},
            )

            // action items, shown items are dependent on this being a song item
            songActions.forEach { item ->
                ActionOptionRow(item)
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            // if the song has an artist name and current screen is not ArtistDetails
            if (song.artistName != "" && context != "ArtistDetails")
                ActionOptionRow( Pair(Actions.GoToArtist) {  } ) //navigateToArtistDetails()

            // if the song has an album title and current screen is not AlbumDetails
            if (song.albumTitle != "" && context != "AlbumDetails")
                ActionOptionRow( Pair(Actions.GoToAlbum) {  } ) //navigateToAlbumDetails()

//            if (song.genreName != "" && context != "GenreDetails")
//                ActionOptionRow( Pair(Actions.GoToGenre) {} )

//            if (song.composerName != "" && context != "ComposerDetails")
//                ActionOptionRow( Pair(Actions.GoToComposer) {} )

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            ActionOptionRow( Pair(Actions.EditSongTags) {} ) //onClick action would go into lambda edit song tags

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            if (context == "PlaylistDetails")
                ActionOptionRow( Pair(Actions.RemoveFromPlaylist) {} )

            if (context == "Queue")
                ActionOptionRow( Pair(Actions.RemoveFromQueue) {} )

            ActionOptionRow( Pair(Actions.DeleteFromLibrary) {} )

            Button( // close btn
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    album: AlbumInfo,
    context: String,
    //AlbumDetails context? ArtistDetails context?
    //navigateToAlbumDetails: (AlbumInfo) -> Unit, //usage depends on content: if on library.albums, albumDetails, artistDetails. aka not necessary for moreOptions on albumDetails
    //navigateToArtistDetails: (ArtistInfo) -> Unit,
    //navigateToPlayer: (SongInfo) -> Unit = {},
    //showBottomSheet: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,//{showBottomSheet = false}
        sheetState = sheetState,//rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor,//MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor,//MaterialTheme.colorScheme.background,
        scrimColor = scrimColor,//MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier,
        ) {
            // header section
            MoreOptionModalHeader(album.title, album)

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            val albumActions = arrayListOf(
                Pair(Actions.PlayItem) {  }, //play(album)
                Pair(Actions.ShuffleItem) {  }, //shuffle(album)
                Pair(Actions.AddToPlaylist) {  }, //addToPlaylist(album)
                Pair(Actions.AddToQueue) {  }, //addToQueue(album)
            )

            // action items, shown items are dependent on this being a song item
            albumActions.forEach { item ->
                ActionOptionRow(item)
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            // if album has album artist and not already on ArtistDetails screen
            if (album.albumArtistId != null && context != "ArtistDetails")
                ActionOptionRow( Pair(Actions.GoToAlbumArtist) {} ) // { navigateToArtistDetails(albumArtistId) }

            // if in artistDetails, in library.Albums,
            if (context != "AlbumDetails")
                ActionOptionRow( Pair(Actions.GoToAlbum) {} ) // { navigateToAlbumDetails(albumId) or navigateToAlbumDetails(album) }

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            ActionOptionRow( Pair(Actions.EditAlbumTags) {} ) //onClick action would go into lambda edit album tags

            Button(
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,//.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    artist: ArtistInfo,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit = {},
    context: String = "",
    //showBottomSheet: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,//{showBottomSheet = false}
        sheetState = sheetState,//rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor,//MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor,//MaterialTheme.colorScheme.background,
        scrimColor = scrimColor,//MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {

        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier,
        ) {
            // header section
            MoreOptionModalHeader(artist.name, artist)

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            val artistActions = arrayListOf(
                Pair(Actions.PlayItem) { /* probably call controller here, pass in artist songs */ }, // play(artist)
                Pair(Actions.ShuffleItem) { /* probably call controller here, pass in artist songs */ }, // shuffle(artist)
                Pair(Actions.AddToPlaylist) { /* probably call controller here, pass in artist songs */ }, //addToPlaylist(artist)
                Pair(Actions.AddToQueue) { /* probably call controller here, pass in artist songs */ }, //addToQueue(artist)
            )

            // action items, shown items are dependent on this being an artist item
            artistActions.forEach { item ->
                ActionOptionRow(item)
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            // if on library.artists
            if (context != "ArtistDetails") {
                ActionOptionRow(Pair(Actions.GoToArtist) {
                    navigateToArtistDetails(artist)
                }) // { navigateToArtistDetails(artist.id) }
                HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }

            ActionOptionRow(Pair(Actions.EditArtistTags) {}) //onClick action in the lambda

            Button(
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,//.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposerMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    composer: ComposerInfo,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit = {},
    context: String = "",
    //showBottomSheet: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,//{showBottomSheet = false}
        sheetState = sheetState,//rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor,//MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor,//MaterialTheme.colorScheme.background,
        scrimColor = scrimColor,//MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {

        Column (
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // header section
            MoreOptionModalHeader(composer.name, composer)

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            val composerActions = listOf(
                Pair(Actions.PlayItem) { /* probably call controller here, pass in composer songs */ }, // play(composer)
                Pair(Actions.ShuffleItem) { /* probably call controller here, pass in composer songs */ }, // shuffle(composer)
                Pair(Actions.AddToPlaylist) { /* probably call controller here, pass in composer songs */ }, //addToPlaylist(composer)
                Pair(Actions.AddToQueue) { /* probably call controller here, pass in composer songs */ }, //addToQueue(composer)
            )

            // action items, shown items are dependent on this being a song item
            composerActions.forEach { item ->
                ActionOptionRow(item)
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            // if on library.composers
            if (context != "ComposerDetails") {
                ActionOptionRow(Pair(Actions.GoToComposer) {
                    navigateToComposerDetails(composer)
                }) // { navigateToComposerDetails(composer.id) }

                HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }

            ActionOptionRow( Pair(Actions.EditComposerTags) {} )

            Button(
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,//.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    genre: GenreInfo,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit = {},
    context: String = "",
    //showBottomSheet: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,//{showBottomSheet = false}
        sheetState = sheetState,//rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor,//MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor,//MaterialTheme.colorScheme.background,
        scrimColor = scrimColor,//MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {

        Column (
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // header section
            MoreOptionModalHeader(genre.name, genre)

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            val genreActions = listOf(
                Pair(Actions.PlayItem) { /* probably call controller here, pass in genre songs */ }, // play(genre)
                Pair(Actions.ShuffleItem) { /* probably call controller here, pass in genre songs */ }, // shuffle(genre)
                Pair(Actions.AddToPlaylist) { /* probably call controller here, pass in genre songs */ }, //addToPlaylist(genre)
                Pair(Actions.AddToQueue) { /* probably call controller here, pass in genre songs */ }, //addToQueue(genre)
            )

            // action items, shown items are dependent on this being a song item
            genreActions.forEach { item ->
                ActionOptionRow(item)
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            // if on library.genres
            if (context != "GenreDetails") {
                ActionOptionRow(Pair(Actions.GoToGenre) {
                    navigateToGenreDetails(genre)
                }) // { navigateToGenreDetails(genre.id) }
                HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }

            ActionOptionRow(Pair(Actions.EditGenreTags) {})

            Button(
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,//.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    coroutineScope: CoroutineScope,
    playlist: PlaylistInfo,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit = {},
    context: String = "",
    //showBottomSheet: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,//{showBottomSheet = false}
        sheetState = sheetState,//rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor,//MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor,//MaterialTheme.colorScheme.background,
        scrimColor = scrimColor,//MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {

        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier//.padding(horizontal = 24.dp),
        ) {
            // header section
            MoreOptionModalHeader(playlist.name, playlist)

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            val playlistActions = listOf(
                Pair(Actions.PlayItem) { /* probably call controller here, pass in playlist songs */ }, // play(playlist)
                Pair(Actions.ShuffleItem) { /* probably call controller here, pass in playlist songs */ }, // shuffle(playlist)
                Pair(Actions.AddToPlaylist) { /* probably call controller here, pass in playlist songs */ }, //addToPlaylist(playlist)
                Pair(Actions.AddToQueue) { /* probably call controller here, pass in playlist songs */ }, //addToQueue(playlist)
            )

            // action items, shown items are dependent on this being a song item
            playlistActions.forEach { item ->
                ActionOptionRow(item)
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

            // if on library.playlists
            if (context != "PlaylistDetails") {
                ActionOptionRow( Pair(Actions.GoToPlaylist) {
                    navigateToPlaylistDetails(playlist)
                }) // { navigateToPlaylistDetails(playlist.id) }
                HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }

            ActionOptionRow(Pair(Actions.EditPlaylistTags) {})
            ActionOptionRow(Pair(Actions.EditPlaylistOrder) {})

            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            ActionOptionRow( Pair(Actions.ExportPlaylist) { /*  */ } )
            ActionOptionRow( Pair(Actions.DeletePlaylist) { /* */ } )

            Button(
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

/**
 * Preemptively adding this since will likely need more options for the queue
 * just not sure how or in what capacity
 *
 * Actual content of this would either need to be dependent on:
 * 1 - is this modal being used for the sake of the current song?
 * 2 - is this modal being used to operate on the queue itself
 * 3 - is this modal being used to operate on the list of songs in the queue
 * 4 - how is the queue itself being displayed in UI
 * each of these contexts would need slightly different actions/modal content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    coroutineScope: CoroutineScope,
    queue: String = "", // placeholder
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,//{showBottomSheet = false}
        sheetState = sheetState,//rememberModalBottomSheetState(skipPartiallyExpanded = false,),
        contentColor = contentColor,//MaterialTheme.colorScheme.onBackground,
        containerColor = containerColor,//MaterialTheme.colorScheme.background,
        scrimColor = scrimColor,//MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
        properties = properties, //ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        dragHandle = { CustomDragHandle() }
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier,
        ) {
            Row(
                //verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Now Playing")
            }
            HorizontalDivider( thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp) )

            ActionOptionRow( Pair(Actions.AddToPlaylist) {} )
            ActionOptionRow( Pair(Actions.ClearQueue) {} )
            ActionOptionRow( Pair(Actions.SaveQueueToPlaylist) {} )

            Button(
                onClick = {
                    showBottomSheet = false
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = MusicShapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

/**
 * Bottom Modal for Library Screen to use for sorting on the library list's Sort btn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySortSelectionBottomModal(
    onDismissRequest: () -> Unit,// = { var showBottomSheet = false },

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    libraryCategory: LibraryCategory,
){
    //var showBottomSheet by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentColor = contentColor,
        containerColor = containerColor,
        scrimColor = scrimColor,
        properties = properties,
        dragHandle = { CustomDragHandle() }
    ) {
        LazyColumn (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
        ) {
            item {
                Text(
                    text = "Sort by",
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            item {
                //list of radio buttons, set of options determined by context
                when (libraryCategory) {

                    LibraryCategory.Albums -> {
                        //sorting on library.albums screen
                        RadioGroupSet(
                            listOf(
                                "Title",
                                "Album Artist",
                                "Date Last Played",
                                "Song Count"
                            )
                        )
                    }

                    LibraryCategory.Artists -> {
                        //sorting on library.artists screen
                        RadioGroupSet(listOf("Name", "Album Count", "Song Count"))
                    }

                    LibraryCategory.Composers -> {
                        //sorting on library.composers screen
                        RadioGroupSet(listOf("Name", "Song Count"))
                    }

                    LibraryCategory.Genres -> {
                        //sorting on library.genres screen
                        RadioGroupSet(listOf("Name", "Song Count"))
                    }

                    LibraryCategory.Playlists -> {
                        //sorting on library.playlists screen
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

                    LibraryCategory.Songs -> {
                        //sorting on library.songs screen
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
            }

            item { HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) }

            // radio buttons for selecting ascending or descending
            item { RadioGroupSet(listOf("Ascending", "Descending")) }

            item {
                Row {
                    //cancel/exit btn
                    Button(
                        onClick = {
                            //showBottomSheet = false
                            coroutineScope.launch {
                                sheetState.hide()
                                //showBottomSheet = false
                            }
                        },
                        colors = buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        shape = MusicShapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.5f)
                    ) {
                        Text("CANCEL")
                    }

                    //apply btn
                    Button(
                        onClick = {
                            //showBottomSheet = false
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                        },
                        colors = buttonColors(
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        shape = MusicShapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.5f)
                    ) {
                        Text("APPLY")
                    }
                }
            }
        }
    }
}

/**
 * Bottom Modal for Details Screens to use for selected Details item's Sort btn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSortSelectionBottomModal(
    onDismissRequest: () -> Unit,// = { var showBottomSheet = false },

    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
    content: String = "", // item(s) to be sorted
    context: String = "", // screen containing item(s) to sort
){
    // var showBottomSheet by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentColor = contentColor,
        containerColor = containerColor,
        scrimColor = scrimColor,
        properties = properties,
        dragHandle = { CustomDragHandle() }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
        ) {
            item {
                Text(
                    text = "Sort by",
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            //list of radio buttons, set of options determined by context
            item {
                when (content) {

                    "AlbumInfo" -> { // is AlbumInfo
                        //sorting on artist details -> album list screen
                        RadioGroupSet(listOf("Album Title", "Song Count"))
                    }

                    "SongInfo" -> { // is SongInfo
                        //sorting on album details screen -> "Title, Track Number" -found in albumRepo, songRepo
                        if (context == "AlbumDetails")
                            RadioGroupSet(listOf("Title", "Track number"))

                        //sorting on artist details screen -> "Song Title, Album Title" -songRepo
                        if (context == "ArtistDetails")
                            RadioGroupSet(listOf("Title", "Album Title"))

                        //sorting on composer details screen -> "Title -songRepo, date last played -composerRepo
                        if (context == "ComposerDetails")
                            RadioGroupSet(listOf("Title", "Date Last Played"))

                        //sorting on genre details screen -> "Title, date last played -songRepo, genreRepo
                        if (context == "GenreDetails")
                            RadioGroupSet(listOf("Title", "Date Last Played"))

                        //sorting on playlist details screen -> "Title, track number
                        if (context == "PlaylistDetails")
                            RadioGroupSet(listOf("Title", "Track number"))
                    }

                    else -> {
                        Text("this is not a valid object to pass")
                    }
                }
            }

            item { HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) }

            //radio buttons for selecting ascending or descending
            item { RadioGroupSet(listOf("Ascending", "Descending")) }

            item {
                Row {
                    //cancel/exit btn
                    Button(
                        onClick = {
                            //showBottomSheet = false
                            coroutineScope.launch {
                                sheetState.hide()
                                //showBottomSheet = false
                            }
                        },
                        colors = buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        shape = MusicShapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.5f)
                    ) {
                        Text("CANCEL")
                    }

                    //apply btn
                    Button(
                        onClick = {
                            //showBottomSheet = false
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                        },
                        colors = buttonColors(
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        shape = MusicShapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.5f)
                    ) {
                        Text("APPLY")
                    }
                }
            }
        }
    }
}

/**
 * Bottom modal for creating Playlists, from either Add to playlist -> New playlist prompt or on Library.Playlists -> Add btn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistBottomModal(
    onDismissRequest: () -> Unit,

    sheetState: SheetState = rememberModalBottomSheetState(),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),

    coroutineScope: CoroutineScope,
) {
    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    val errorMessage = "Name cannot be empty"
    val state = rememberTextFieldState()
    var isError by rememberSaveable { mutableStateOf(false) }

    // required text field validation functions
    fun validate(text: CharSequence) {
        isError = text.isEmpty()
    }
    fun isInvalid(text: CharSequence): Boolean = text.isEmpty()
    val createEnabled = remember { derivedStateOf { isInvalid(nameText) } }

    LaunchedEffect(Unit) {
        snapshotFlow { state.text }.collect{ validate(it) }
    }

    //var showBottomSheet by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest, //onDismissRequest = { showBottomSheet = false },
        sheetState = sheetState,
        contentColor = contentColor,
        containerColor = containerColor,
        scrimColor = scrimColor,
        properties = properties,
        dragHandle = { CustomDragHandle() }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(horizontal = 24.dp)//, vertical = 16.dp)
        ) {
            fullWidthItem {
                Text(
                    text = "Create New Playlist",
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            fullWidthItem {
                //playlist name text field
                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        validate(nameText)
                    },
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        Row {
                            Text(
                                text = if (isError) errorMessage else "",
                                modifier = Modifier.clearAndSetSemantics {},
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardActions = KeyboardActions { validate(nameText) },
                    shape = shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        //unfocusedIndicatorColor = Color.Green, //border line
                        cursorColor = MaterialTheme.colorScheme.primary,
                        //unfocusedTextColor = MaterialTheme.colorScheme.onBackground, //seems like default color
                        //unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    label = { Text("Playlist Name") },
                )
            }

            fullWidthItem {
                //playlist description text field, NOT REQUIRED
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    singleLine = true,
                    maxLines = 3,
                    shape = shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    label = { Text("Description") },
                )
                //TextField()
                //text field for playlist name, *required
                //text field for playlist description, not required
            }

            fullWidthItem {
                Row {
                    //cancel btn
                    Button(
                        onClick = { //still doesn't do the showBottomSheet thing correctly since it's in different file
                            //showBottomSheet = false
                            coroutineScope.launch {
                                sheetState.hide()
                                //showBottomSheet = false
                            }
                        },
                        colors = buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        shape = MusicShapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.5f)
                    ) {
                        Text("CANCEL")
                    }

                    //create playlist btn
                    Button(
                        onClick = { //still doesn't do the showBottomSheet thing correctly since it's in different file
                            //showBottomSheet = false
                            coroutineScope.launch {
                                sheetState.hide()
                                //showBottomSheet = false
                            }
                        },
                        enabled = !createEnabled.value,
                        colors = buttonColors(
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        shape = MusicShapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.5f)
                    ) {
                        Text("CREATE")
                    }
                }
            }
        }
    }
}

// TODO: determine if this should remain using PlayerSong or be changed to SongInfo
//  initial idea is to remain as is - 4/2/2025
//  making it SongInfo - 7/23/2025
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    song: SongInfo, //actually ... should this be SongInfo or PlayerSong, since the context is related to the Player
    isPlaying: Boolean = true,
    navigateToPlayer: (SongInfo) -> Unit = {}, //consequently, should this be using PlayerSong since it is indeed related to the Player
    navigateToQueue: () -> Unit = {},
    sheetState: BottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(
            SheetState(
                skipPartiallyExpanded = false,
                density = Density(1f,1f),
                initialValue = if (isPlaying) SheetValue.PartiallyExpanded else SheetValue.Hidden,
                skipHiddenState = isPlaying,
            )
        ),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    modifier: Modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
    content: @Composable () -> Unit = {},
) {
    /**
     * Goal: to create the bottom bar that will appear when a song is in play while the user is in the app
     * will only show when in play, otherwise is not visible nor interact-able
     */
    /*val sheetState = rememberBottomSheetScaffoldState(
        SheetState(
            skipPartiallyExpanded = false,
            density = Density(1f,1f),
            initialValue = if (isPlaying) SheetValue.PartiallyExpanded else SheetValue.Hidden,
            skipHiddenState = isPlaying,
        ),
    )*/
    BottomSheetScaffold(
        scaffoldState = BottomSheetScaffoldState(
            bottomSheetState = sheetState.bottomSheetState,//SheetState(initialValue = SheetValue.Expanded, skipPartiallyExpanded = true, density = Density(1f,1f)),
            snackbarHostState = sheetState.snackbarHostState,//SnackbarHostState(),
        ),
        sheetContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        sheetDragHandle = {
            if (sheetState.bottomSheetState.hasExpandedState) {
                CustomDragHandle()
            } else
                null
        },
        sheetShape = MusicShapes.extraSmall,
        sheetContent = {
            if (sheetState.bottomSheetState.hasPartiallyExpandedState)
                BottomSheetPlayer(
                    song = song,
                    isPlaying = isPlaying,
                    navigateToPlayer = navigateToPlayer,
                    navigateToQueue = navigateToQueue,
                    onPlayPress = {  },
                    onPausePress = {  },
                    modifier = modifier,
                )
            else if (sheetState.bottomSheetState.hasExpandedState)
                BottomSheetFullPlayer(
                    song = song,
                    isPlaying = isPlaying,
                    navigateToPlayer = navigateToPlayer,
                    navigateToQueue = navigateToQueue,
                    onPlayPress = {  },
                    onPausePress = {  },
                    onNext = {  },
                    onPrevious = {  },
                    modifier = modifier,
                )
        },
        modifier = modifier,//Modifier.windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        content()
    }
}

// TODO: same concern as BottomSheet above
@Composable
fun BottomSheetPlayer(
    song: SongInfo,
    isPlaying: Boolean = true,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToQueue: () -> Unit = {},
    onPlayPress: () -> Unit = {},
    onPausePress: () -> Unit = {},
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    val sideButtonsModifier = Modifier
        .size(sideButtonSize)
        .background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    val primaryButtonModifier = Modifier
        .size(playerButtonSize)
        .background(
            color = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxWidth(),
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { navigateToPlayer(song) },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                HeaderImage(song.artworkUri, song.title)
                Column(Modifier.padding(8.dp).weight(1f)) {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = song.setSubtitle(),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                //Spacer(modifier = Modifier.weight(1f))
                if (isPlaying) {
                    //determined that the current state is playing (isPlaying is true)
                    Image(
                        imageVector = Icons.Filled.Pause,
                        contentDescription = stringResource(R.string.pb_pause),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = primaryButtonModifier
                            .padding(8.dp)
                            .clickable {
                                onPausePress()
                            }
                    )
                } else {
                    //determined that the current state is paused (isPlaying is false)
                    Image(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.pb_play),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = primaryButtonModifier
                            .padding(8.dp)
                            .clickable {
                                onPlayPress()
                            }
                    )
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = stringResource(R.string.icon_queue),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = sideButtonsModifier
                            .padding(8.dp)
                            .clickable {
                                navigateToQueue()
                            }
                    )
                }
            }
        }
    }
}

// TODO: same concern as above
@Composable
fun BottomSheetFullPlayer(
    song: SongInfo,
    isPlaying: Boolean = true,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToQueue: () -> Unit = {},
    onPlayPress: () -> Unit = {},
    onPausePress: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    val sideButtonsModifier = Modifier
        .size(sideButtonSize)
        .background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    val primaryButtonModifier = Modifier
        .size(playerButtonSize)
        .background(
            color = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { navigateToPlayer(song) },
        ) {
            Column {
                //track info row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    HeaderImage(song.artworkUri, song.title)
                    Column(Modifier.padding(8.dp).weight(1f)) {
                        Text(
                            text = song.title,
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = song.setSubtitle(),
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = stringResource(R.string.icon_queue),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = sideButtonsModifier
                                .padding(8.dp)
                                .clickable {
                                    navigateToQueue()
                                }
                        )
                    }
                }

                //player buttons row
                BottomSheetPlayerButtons(
                    //removed private modifier to borrow this fun for BottomModals
                    hasNext = true,
                    isPlaying = isPlaying,
                    onPlayPress = onPlayPress,
                    onPausePress = onPausePress,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    modifier = Modifier,
                    primaryButtonModifier = primaryButtonModifier,
                    sideButtonsModifier = sideButtonsModifier,
                    //need a state saver to handle button interactions
                )

                //slider row
                PlayerSlider(
                    timeElapsed = Duration.ZERO,
                    songDuration = song.duration,
                    onSeekingStarted = {},
                    onSeekingFinished = {},
                )
            }
        }
    }
}

@Composable
fun BottomSheetPlayerButtons(
    hasNext: Boolean = false,
    isPlaying: Boolean = true,
    onPlayPress: () -> Unit = {},
    onPausePress: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    modifier: Modifier = Modifier,
    sideButtonsModifier: Modifier,
    primaryButtonModifier: Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //Image for Skip back to previous button
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.pb_skip_previous),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = sideButtonsModifier
                .clickable(enabled = true, onClick = onPrevious)
                //.clickable(enabled = isPlaying, onClick = onPrevious)
                //.alpha(if (isPlaying) 1f else 0.25f)
        )

        if (isPlaying) {
            //determined that the current state is playing (isPlaying is true)
            Image(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(R.string.pb_pause),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable {
                        onPausePress()
                    }
            )
        } else {
            //determined that the current state is paused (isPlaying is false)
            Image(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.pb_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable {
                        onPlayPress()
                    }
            )
        }

        //skip to next playable button
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.pb_skip_next),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = sideButtonsModifier
                .clickable(enabled = true, onClick = onNext)
                //.clickable(enabled = hasNext, onClick = onNext)
                //.alpha(if (hasNext) 1f else 0.25f)
        )
    }
}

@Composable
fun RadioGroupSet(
    radioOptions: List<String>,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .selectableGroup()
    ) {
        radioOptions.forEach { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier.padding(start = 24.dp),
                    onClick = null, // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    color =
                        if (text == selectedOption)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onBackground,
                    //style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}


/*
    more options context menu
    * now playing - add to playlist, go to artist, go to album, clear queue, save queue, view tags(?)
    * navigation sidebar?
    * show queue: on queue context - add to playlist, go to artist, go to album, clear queue, save queue
    * show queue: on song context -  play next, add to playlist, go to artist, go to album, remove from queue, view tags(?)
    * all songs list: on song context - play next, add to queue, add to playlist, go to artist, go to album, remove from library, delete from device, view tags(?)
    * all artists list - none(?)
    * all albums list - shuffle, play next, add to queue, add to playlist, go to artist
    * all genres list - play next, add to queue
    * all playlists list - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
    * artist’s album list page (from all artists list to selected artist view): on single album context - shuffle, play next, add to queue, add to playlist, go to artist
    * all artist’s songs page (from selected artist to all albums/songs view): on ‘all my songs’ context - shuffle, play next, add to queue, add to playlist
    * all artist’s songs page (from selected artist to all albums/songs view): on individual song context - play next, add to queue, add to playlist, go to artist, go to album, delete, view tags(?)
    * album’s page (from selected artist to selected album view): on artist name context - shuffle, play next, add to queue, add to playlist, go to artist
    * album’s page (from selected artist to selected album view): on individual song context - play next, add to queue, add to playlist, go to artist, delete, view tags(?)
    * album’s page (from all albums list to selected album view): on artist name context - shuffle, play next, add to queue, add to playlist, go to artist
    * album’s page (from all albums list to selected album view): on individual song context - play next, add to queue, add to playlist, go to artist, delete, view tags(?)
    * playlist page (from all playlists to selected playlist): on my playlist context - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
    * playlist page (from all playlists to selected playlist): on individual song context - play next, add to queue, add to playlist, edit playlist, remove from playlist, delete(?), view tags(?)
 */

@CompLightPreview
@Composable
fun PreviewRadioButtons() {
    MusicTheme {
        RadioGroupSet(listOf("Ascending", "Descending"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
//@SystemLightPreview
//@SystemDarkPreview
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
            coroutineScope = rememberCoroutineScope(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
//@Preview
//@SystemLightPreview
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
            coroutineScope = rememberCoroutineScope(),
            song = PreviewSongs[0],
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
//@SystemLightPreview
@Composable
fun PreviewBottomSheet() {
    MusicTheme {
        BottomSheet(
            PreviewSongs[0]
        )
    }
}

//@CompLightPreview
//@CompDarkPreview
@Composable
fun PreviewBottomBarPlayer() {
    MusicTheme {
        BottomSheetPlayer(
            song = getSongData(6535),
            isPlaying = true,
            navigateToPlayer = {},
            navigateToQueue = {},
            onPlayPress = {},
            onPausePress = {},
        )
    }
}