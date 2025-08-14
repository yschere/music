package com.example.music.ui.library.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.outlined.PlaylistAddCheck
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.model.PlaylistInfo
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.shared.ActionItem
import com.example.music.ui.shared.ActionOptionRow
import com.example.music.ui.shared.CreatePlaylistBottomModal
import com.example.music.ui.shared.LibrarySortSelectionBottomModal
import com.example.music.ui.shared.MoreOptionModalHeader
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Playlist Items Lazy List Scope Generator.
 * Provides header item with a count of the playlist given, and
 * generates a column of playlists, with each playlist item shown as a row.
 */
/*fun LazyListScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.playlists, playlists.size, playlists.size)
            ) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.playlists, playlists.size, playlists.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(playlists) { item ->
        PlaylistItemRow(
            playlist = item,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/

/**
 * Playlist Items Lazy Grid Scope Generator.
 * Provides header item with a count of the playlist given, and
 * generates a grid of playlists, with each playlist item shown as a card.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LazyGridScope.playlistItems(
    playlists: List<PlaylistInfo>,
    //showBottomSheet: Boolean,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    var showSheet = false
    var something: PlaylistInfo = PlaylistInfo()

    //section 1: header
    fullWidthItem {
        // ******** var  for modal remember here
        var showBottomSheet by remember { mutableStateOf(false) }
        showSheet = showBottomSheet
        var onAdd by remember { mutableStateOf(false) }
        var onSort by remember { mutableStateOf(false) }
        //var onSelect by remember { mutableStateOf(false) }

        var showContext by rememberSaveable { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = """\s[a-z]""".toRegex()
                    .replace(quantityStringResource(R.plurals.playlists, playlists.size, playlists.size)) {
                        it.value.uppercase()
                    },
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f, true)
            )

            // add icon
            IconButton(onClick = {
                showBottomSheet = true
                showSheet = showBottomSheet
                onAdd = true
            }) { // navigate to multiselect across all songs
                Icon(
                    imageVector = Icons.Filled.Add,//want this to be sort icon // Icons.Default.Add,
                    contentDescription = stringResource(R.string.icon_add_to_playlist),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // sort icon
            IconButton(onClick = {
                showBottomSheet = true
                showSheet = showBottomSheet
                showContext = true
            }) {//onSortClick) { // showBottomSheet = true
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                    contentDescription = stringResource(R.string.icon_sort),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            // multi-select icon
            IconButton(onClick = {
                //navigateToMulti-Select screen

            }) { // navigate to multiselect across playlist songs
                Icon(
                    imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                    contentDescription = stringResource(R.string.icon_multi_select),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        if(showBottomSheet) {
            //showSheet = showBottomSheet
            if (onAdd) {
                // create new playlist prompt modal
                CreatePlaylistBottomModal(
                    onDismissRequest = {
                        showBottomSheet = false
                        //showSheet = showBottomSheet
                        onAdd = false
                    },
                    coroutineScope = coroutineScope,
                )
            }
            if (showContext) {
                // sort list prompt modal
                LibrarySortSelectionBottomModal(
                    onDismissRequest = {
                        showBottomSheet = false
                        //showSheet = showBottomSheet
                        showContext = false
                   },
                    coroutineScope = coroutineScope,
                    libraryCategory = LibraryCategory.Playlists,
                )
            }
            //showSheet = showBottomSheet
        }
    }

    items(
        items = playlists,
        span = { GridItemSpan(1) }
    ){ item ->
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            modifier = Modifier,
            onClick = { navigateToPlaylistDetails(item) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                PlaylistItemBoxHeader(item)
                PlaylistItemBoxFooter(playlist = item, onMoreOptionsClick = { playlist ->

                    coroutineScope.launch {
                        showSheet = true
                        something = playlist
                        /*PlaylistMoreOptionsBottomModal(
                            onDismissRequest = {
                                showSheet = false
                            },
                            coroutineScope = coroutineScope,
                            playlist = playlist,
                            navigateToPlaylistDetails = navigateToPlaylistDetails,
                        )*/
                    }
                })
            }
        }
    }

    fullWidthItem {
        if (showSheet) {
            PlaylistMoreOptionsBottomModal(
                onDismissRequest = {
                    showSheet = false
                },
                coroutineScope = coroutineScope,
                playlist = something,
                navigateToPlaylistDetails = navigateToPlaylistDetails,
            )
        }
    }
}

/**
 * Create a composable view of a Playlist in a row form
 */
@Composable
private fun PlaylistItemRow(
    playlist: PlaylistInfo,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.padding(4.dp)){
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,//surfaceContainer,
            onClick = { navigateToPlaylistDetails(playlist) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                AlbumImage(
                    modifier = modifier
                        .size(56.dp)
                        //.fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    albumImage = 2,//albumImageId,
                    contentDescription = playlist.name
                )

                Column(modifier.weight(1f)){
                    Text(
                        text = playlist.name,
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
                        Text(
                            text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                IconButton( //more options button
                    //modifier = Modifier.padding(0.dp),
                    onClick = {  }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
                ) {
                    Icon( //more options icon
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.icon_more),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

private val FEATURED_PLAYLIST_IMAGE_SIZE_DP = 160.dp

@Composable
private fun PlaylistItemBoxFooter(
    modifier: Modifier = Modifier,
    playlist: PlaylistInfo,
    onMoreOptionsClick: (PlaylistInfo) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.requiredWidth(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
    ) {
        Text(
            text = playlist.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(4.dp).weight(1f,true)
        )

        IconButton(
            onClick = {
                onMoreOptionsClick(playlist)
            },
                //want to trigger bottom modal for PlaylistMoreOptions here
                //want it to have sheetState.show() or isVisible or something
                //want the playlist data to pass to it be from playlist var here
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun PlaylistItemBoxHeader(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomStart
    ){
        AlbumImage(
            modifier = modifier
                .size(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
                .clip(MaterialTheme.shapes.medium),
            albumImage = 2,//albumImageId,
            contentDescription = playlist.name
        )

        Text(
            text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .border(1.dp,color = Color.Transparent, shape = MusicShapes.small)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MusicShapes.small
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun TopPlaylistRowItem(
    playlistName: String,
    //playlistImageId: String,
    //isFollowed: Boolean,
    modifier: Modifier = Modifier,
    //onToggleFollowClicked: () -> Unit,
) {
    Column(
        modifier.semantics(mergeDescendants = true) {}
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                albumImage = 2,//albumImageId,
                contentDescription = playlistName
            )
        }

        Text(
            text = playlistName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false, confirmValueChange = {SheetState.equals(SheetValue.Hidden)}),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrimColor: Color = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    coroutineScope: CoroutineScope,
    playlist: PlaylistInfo,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit = {},
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
    ) {
        Column (
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // header section
            MoreOptionModalHeader(playlist.name, playlist)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                AlbumImage(
                    albumImage = 1,
                    contentDescription = playlist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp)
                        .clip(MaterialTheme.shapes.small)
                )
                Column(Modifier.padding(8.dp)) {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.titleMedium,
                        //color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),//if ((song.artistName != "") && (song.albumTitle != "")) {song.artistName + " • " + song.albumTitle} else {(song.artistName) + (song.albumTitle)},
                        style = MaterialTheme.typography.bodySmall,
                        //color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            val songActions = listOf(
                Pair(ActionItem("Play",Icons.Filled.PlayArrow, R.string.icon_play), {  } ), // play playlist
                Pair(ActionItem("Shuffle", Icons.Filled.Shuffle, R.string.icon_shuffle), {  } ), // shuffle & play playlist
                Pair(ActionItem("Add to Playlist", Icons.AutoMirrored.Filled.PlaylistAdd, R.string.icon_add_to_playlist), {  } ), // add to different playlist
                Pair(ActionItem("Add to Queue", Icons.Filled.Queue, R.string.icon_add_to_queue), {  } ), // add playlist to queue
            )

            // action items, shown items are dependent on this being a song item
            songActions.forEach { item ->
                ActionOptionRow(item)
                /*Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { item.action }
                ) {
                    Icon(
                        imageVector = item.icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = item.name,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }*/
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            val navActions = mutableListOf<Pair<ActionItem,()->Unit>>()
            navActions.add( Pair(ActionItem( "Go to Playlist", Icons.AutoMirrored.Outlined.PlaylistAddCheck, R.string.icon_playlist), {} ) )

            navActions.forEach { item ->
                ActionOptionRow(item)
                /*Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { item.action }
                ) {
                    Icon(
                        imageVector = item.icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = item.name,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }*/
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray) //Export Playlist

            Row(//edit tags
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {  } //edit tags
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = stringResource(R.string.icon_edit),
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Edit Tags", // edit playlist details: name, description, artwork?
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Row(//export
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {  } //edit tags
            ) {
                Icon(
                    imageVector = Icons.Outlined.Download,//Icons.Filled.ImportExport,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = stringResource(R.string.icon_export),
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Export Playlist", // edit playlist details: name, description, artwork?
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

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
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("CLOSE")
            }
        }
    }
}

@Preview
@Composable
fun PlaylistItemPreviewRow() {
    MusicTheme {
        PlaylistItemRow(
            playlist = PreviewPlaylists[0],
            navigateToPlaylistDetails = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistItemPreviewBox() {
    MusicTheme {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            modifier = Modifier,
            onClick = {}
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                PlaylistItemBoxHeader(PreviewPlaylists[2])
                PlaylistItemBoxFooter(Modifier, PreviewPlaylists[2]) {}
            }
        }
    }
}