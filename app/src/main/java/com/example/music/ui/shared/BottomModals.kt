package com.example.music.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.ComposerInfo
import com.example.music.model.GenreInfo
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.theme.MusicTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import okhttp3.Request

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsBottomModal(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(true),
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    containerColor: Color = MaterialTheme.colorScheme.onSecondary,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
//    coroutineScope: CoroutineScope,
    itemInfo: Any, //ambiguous to allow for any of the objects to be passed through for providing context
//    showBottomSheet: Boolean,
) {
//    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(true) }
    /*ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet = false
        },
        sheetState = sheetState
    ) {
        // Sheet content
        Button(onClick = {
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                }
            }
        }) {
            Text("Hide bottom sheet")
        }
    }*/

    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet = false
        },
        sheetState = sheetState,
        contentColor = contentColor,
        containerColor = containerColor,
        scrimColor = scrimColor,
        properties = properties,
    ) {

        when(itemInfo) {

            is AlbumInfo -> {
                //* on album detail's album more option
                Column {
                    Row { //item context
                        AlbumImage(
                            albumImage = 1,
                            contentDescription = itemInfo.title,
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Text(
                            text = itemInfo.title
                        )
                    }
                    //options list:
                    // edit album details (would edit album title, album artwork, album artist)

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Play Album",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Shuffle Album",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Album to Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Album to Queue",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
//                    HorizontalDivider(Modifier.size(1.dp))
                    //options list
                    HorizontalDivider(
                        color = Color.LightGray
                    )
                    // ? go to artist page
                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Go To Album Artist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

            }

            is ArtistInfo -> {

                Column {
                    Row { //item context
                        AlbumImage(
                            albumImage = 1,
                            contentDescription = itemInfo.name,
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Text(
                            text = itemInfo.name
                        )
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Play Artist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Shuffle Artist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Artist to Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Artist to Queue",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                /* //options to show:
                 *  edit artist details: artist name
                 */

            }

            is ComposerInfo -> {

                Column {
                    Row { //item context
                        AlbumImage(
                            albumImage = 1,
                            contentDescription = itemInfo.name,
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Text(
                            text = itemInfo.name
                        )
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Play Composer",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Shuffle Composer",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Composer to Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Composer to Queue",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                /* //options to show:
                 *  edit composer details: composer name
                 */
            }

            is GenreInfo -> {

                Column {
                    Row { //item context
                        AlbumImage(
                            albumImage = 1,
                            contentDescription = itemInfo.name,
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Text(
                            text = itemInfo.name
                        )
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Play Genre",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Shuffle Genre",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Genre to Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Genre to Queue",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                /* //options to show:
                 * edit genre details: genre name
                 */

                //* all genres list - play next, add to queue
            }

            is PlaylistInfo -> {
                Column {
                    Row { //item context
                        AlbumImage(
                            albumImage = 1,
                            contentDescription = itemInfo.name,
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Text(
                            text = itemInfo.name
                        )
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Play Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Shuffle Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Songs to Playlist",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        onClick = { /* transition screen to show list of options */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Add Songs to Queue",
                                textAlign = TextAlign.Left,
                                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                /* //options to show:
                 *  edit playlist details: playlist name, playlist description
                 *  edit playlist songs: reorder? remove song? add song?
                 */

                //* all playlists list - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
                //* playlist page (from all playlists to selected playlist): on my playlist context - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
                //* playlist page (from all playlists to selected playlist): on indiv song context - play next, add to queue, add to playlist, edit playlist, remove from playlist, delete(?), view tags(?)
            }

            is SongInfo -> {
                /* //want to show:
                 *  song title, song artwork
                 * //options to show:
                 *  play song
                 *  shuffle song?
                 *  add song to playlist
                 *  add song to queue
                 *  edit song details: song title, artist, album, genre, composer, year, comment
                 */

                //* all songs list: on song context - play next, add to queue, add to playlist, go to artist, go to album, remove from library, delete from device, view tags(?)

                //* all artist’s songs page (from selected artist to all albums/songs view): on ‘all my songs’ context - shuffle, play next, add to queue, add to playlist
                //* all artist’s songs page (from selected artist to all albums/songs view): on indiv song context - play next, add to queue, add to playlist, go to artist, go to album, delete, view tags(?)
            }

            is PlayerSong -> {
                //on any details screen -> song more options - play next, add to queue, add to playlist, go to artist, delete, view tags(?)


                /* //want to show:
                 *  song title, song artwork
                 * //options to show:
                 *  play artist songs
                 *  shuffle artist songs
                 *  add artist songs to playlist
                 *  add artist songs to queue
                 *  edit artist details: artist name
                 */
            }

            else -> {
                Text("this is not a valid object to pass")
            }
        }
//        Text("HELP")
//        Icon(
//            painter = painterResource(R.drawable.bpicon),
//            contentDescription = "modal",
//            tint = MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f),
//            modifier = Modifier.size(56.dp),
//        )
//        Text("HELP")
//        Icon(
//            painter = painterResource(R.drawable.bpicon),
//            contentDescription = "modal",
//            tint = MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f),
//            modifier = Modifier.size(56.dp),
//        )
    }
}


//should I use more bottom modals for other things?
// like for the sorting category options?
// could make these separated by the name for where they are called, so that the context is that the one calling it is using that particular name/identifier
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySortSelectionBottomModal(
    onDismissRequest: () -> Unit,// = { var showBottomSheet = false },
    sheetState: SheetState = rememberModalBottomSheetState(),
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    containerColor: Color = MaterialTheme.colorScheme.onSecondary,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
//    coroutineScope: CoroutineScope,
    itemInfo: Any,
){
//    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(true) }

    ModalBottomSheet(
        //onDismissRequest = { showBottomSheet = false },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentColor = contentColor,
        containerColor = containerColor,
        scrimColor = scrimColor,
        properties = properties,
    ) {
        Text(
            text = "Sort by",
            modifier = Modifier,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.labelSmall,
        )

        //list of radio buttons, set of options determined by context
        when(itemInfo) {

            is AlbumInfo -> {
                //sorting on library.albums screen
                RadioGroupSet(listOf("Title", "Album Artist", "Date Last Played", "Song Count"))


                /* //want to show:
                 *  album title, album image
                 * //options to show:
                 *  play album
                 *  shuffle album
                 *  add album to playlist
                 *  add album to queue
                 *  edit album details: album title, album artwork, album artist
                 */

                //* all albums list - shuffle, play next, add to queue, add to playlist, go to artist
                //* album’s page (from selected artist to selected album view): on artist name context - shuffle, play next, add to queue, add to playlist, go to artist
                //* album’s page (from selected artist to selected album view): on indiv song context - play next, add to queue, add to playlist, go to artist, delete, view tags(?)
                //* album’s page (from all albums list to selected album view): on artist name context - shuffle, play next, add to queue, add to playlist, go to artist
                //* album’s page (from all albums list to selected album view): on indiv song context - play next, add to queue, add to playlist, go to artist, delete, view tags(?)
            }

            is ArtistInfo -> {
                //sorting on library.artists screen
                RadioGroupSet(listOf("Name", "Album Count", "Song Count"))

                /* //want to show:
                 *  artist name
                 * //options to show:
                 *  play artist songs
                 *  shuffle artist songs
                 *  add artist songs to playlist
                 *  add artist songs to queue
                 *  edit artist details: artist name
                 */

                //* artist’s album list page (from all artists list to selected artist view): on single album context - shuffle, play next, add to queue, add to playlist, go to artist
            }

            is ComposerInfo -> {
                //sorting on library.composers screen
                RadioGroupSet(listOf("Name", "Song Count"))

                /* //want to show:
                 *  artist name
                 * //options to show:
                 *  play artist songs
                 *  shuffle artist songs
                 *  add artist songs to playlist
                 *  add artist songs to queue
                 *  edit artist details: artist name
                 */
            }

            is GenreInfo -> {
                //sorting on library.genres screen
                RadioGroupSet(listOf("Name", "Song Count"))

                /* //want to show:
                 *  artist name
                 * //options to show:
                 *  play artist songs
                 *  shuffle artist songs
                 *  add artist songs to playlist
                 *  add artist songs to queue
                 *  edit artist details: artist name
                 */

                //* all genres list - play next, add to queue
            }

            is PlaylistInfo -> {
                //sorting on library.playlists screen
                RadioGroupSet(listOf("Playlist name", "Date created", "Date last accessed", "Date last played", "Song Count"))


                /* //want to show:
                 *  playlist name, playlist image(s)
                 * //options to show:
                 *  play playlist songs
                 *  shuffle playlist songs
                 *  add playlist songs to playlist
                 *  add playlist songs to queue
                 *  edit playlist details: playlist name, playlist description
                 *  edit playlist songs: reorder? remove song? add song?
                 */

                //* all playlists list - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
                //* playlist page (from all playlists to selected playlist): on my playlist context - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
                //* playlist page (from all playlists to selected playlist): on indiv song context - play next, add to queue, add to playlist, edit playlist, remove from playlist, delete(?), view tags(?)
            }

            is SongInfo -> {
                //just copy what options exist for PlayerSong
                RadioGroupSet(listOf("Song title", "Artist name", "Album title", "Date added", "Date last played"))

                /* //want to show:
                 *  song title, song artwork
                 * //options to show:
                 *  play song
                 *  shuffle song?
                 *  add song to playlist
                 *  add song to queue
                 *  edit song details: song title, artist, album, genre, composer, year, comment
                 */

                //* all songs list: on song context - play next, add to queue, add to playlist, go to artist, go to album, remove from library, delete from device, view tags(?)

                //* all artist’s songs page (from selected artist to all albums/songs view): on ‘all my songs’ context - shuffle, play next, add to queue, add to playlist
                //* all artist’s songs page (from selected artist to all albums/songs view): on indiv song context - play next, add to queue, add to playlist, go to artist, go to album, delete, view tags(?)
            }

            is PlayerSong -> {
                //sorting on library.songs screen
                RadioGroupSet(listOf("Song title", "Artist name", "Album title", "Date added", "Date last played"))

                //sorting on album details screen

                //sorting on artist details screen

                //sorting on composer detail screen
                RadioGroupSet(listOf("Title", "Date last played"))

                //sorting on genre details screen
                RadioGroupSet(listOf("Title", "Date last played"))

                //sorting on playlist details screen
                RadioGroupSet(listOf("Title", "Track number"))



                /* //want to show:
                 *  song title, song artwork
                 * //options to show:
                 *  play artist songs
                 *  shuffle artist songs
                 *  add artist songs to playlist
                 *  add artist songs to queue
                 *  edit artist details: artist name
                 */
            }

            else -> {
                Text("this is not a valid object to pass")
            }
        }

        HorizontalDivider(Modifier.size(1.dp))

        //radio buttons for selecting ascending or descending

        RadioGroupSet(listOf("Ascending", "Descending"))
//        RadioButton(
//            selected = true,
//            onClick = { },
//            modifier = Modifier.size(10.dp),
//            enabled = true,
//            colors = RadioButtonDefaults.colors(
//                selectedColor = MaterialTheme.colorScheme.primary,
//                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
//            ),
//        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSortSelectionBottomModal(
    onDismissRequest: () -> Unit,// = { var showBottomSheet = false },
    sheetState: SheetState = rememberModalBottomSheetState(),
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    containerColor: Color = MaterialTheme.colorScheme.onSecondary,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f),
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
//    coroutineScope: CoroutineScope,
    itemInfo: Any,
){
//    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(true) }

    ModalBottomSheet(
        //onDismissRequest = { showBottomSheet = false },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentColor = contentColor,
        containerColor = containerColor,
        scrimColor = scrimColor,
        properties = properties,
    ) {
        Text(
            text = "Sort by",
            modifier = Modifier,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.labelSmall,
        )

        //list of radio buttons, set of options determined by context
        when(itemInfo) {

            is AlbumInfo -> {
                //sorting on artist details -> album list screen
                RadioGroupSet(listOf("Album Title", "Song Count"))
            }

            is ArtistInfo -> {
                //currently there is no screen that would sort artists within a higher context other than library screen
                // possible future version could use this within composer or genre, but not set up to support

                //sorting if artist is supported on future version for composer/genre details screen
                //RadioGroupSet(listOf("Name", "Song Count"))
            }

            is ComposerInfo -> {
                //currently there is no screen that needs composer sorting outside of library -> composers
            }

            is GenreInfo -> {
                //currently there is no screen that needs genre sorting outside of library -> genres
            }

            is PlaylistInfo -> {
                //currently there is no screen that needs playlist sorting outside of library -> playlists
            }

            is SongInfo -> {
                //sorting on album details screen -> "Title, Track Number" -found in albumRepo, songRepo
                RadioGroupSet(listOf("Title", "Track number"))

                //sorting on artist details screen -> "Song Title, Album Title" -songRepo
                RadioGroupSet(listOf("Title", "Album Title"))

                //sorting on composer details screen -> "Title -songRepo, date last played -composerRepo
                RadioGroupSet(listOf("Title", "Date Last Played"))

                //sorting on genre details screen -> "Title, date last played -songRepo, genreRepo
                RadioGroupSet(listOf("Title", "Date Last Played"))

                //sorting on playlist details screen -> "Title, track number
                RadioGroupSet(listOf("Title", "Track number"))

            }

            is PlayerSong -> {
                //sorting on album details screen -> "Title, Track Number" -found in albumRepo, songRepo
                RadioGroupSet(listOf("Title", "Track number"))

                //sorting on artist details screen -> "Song Title, Album Title" -songRepo
                RadioGroupSet(listOf("Title", "Album Title"))

                //sorting on composer details screen -> "Title -songRepo, date last played -composerRepo
                RadioGroupSet(listOf("Title", "Date Last Played"))

                //sorting on genre details screen -> "Title, date last played -songRepo, genreRepo
                RadioGroupSet(listOf("Title", "Date Last Played"))

                //sorting on playlist details screen -> "Title, track number
                RadioGroupSet(listOf("Title", "Track number"))

            }

            else -> {
                Text("this is not a valid object to pass")
            }
        }

        HorizontalDivider(Modifier.size(1.dp))

        //radio buttons for selecting ascending or descending
        RadioGroupSet(listOf("Ascending", "Descending"))
//        RadioButton(
//            selected = true,
//            onClick = { },
//            modifier = Modifier.size(10.dp),
//            enabled = true,
//            colors = RadioButtonDefaults.colors(
//                selectedColor = MaterialTheme.colorScheme.primary,
//                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
//            ),
//        )
    }
}


@Composable
fun RadioGroupSet(
    radioOptions: List<String>,
) {
//    val radioOptions = listOf("Calls", "Missed", "Friends")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier.fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = text,
                    color = if (text == selectedOption) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.scrim,
                    style = MaterialTheme.typography.bodyLarge,
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
* all artist’s songs page (from selected artist to all albums/songs view): on indiv song context - play next, add to queue, add to playlist, go to artist, go to album, delete, view tags(?)
* album’s page (from selected artist to selected album view): on artist name context - shuffle, play next, add to queue, add to playlist, go to artist
* album’s page (from selected artist to selected album view): on indiv song context - play next, add to queue, add to playlist, go to artist, delete, view tags(?)
* album’s page (from all albums list to selected album view): on artist name context - shuffle, play next, add to queue, add to playlist, go to artist
* album’s page (from all albums list to selected album view): on indiv song context - play next, add to queue, add to playlist, go to artist, delete, view tags(?)
* playlist page (from all playlists to selected playlist): on my playlist context - shuffle, play next, add to queue, add to playlist, edit playlist, delete, export(?)
* playlist page (from all playlists to selected playlist): on indiv song context - play next, add to queue, add to playlist, edit playlist, remove from playlist, delete(?), view tags(?)
 */


@Preview
@Composable
fun PreviewRadioButtons() {
    MusicTheme {
        RadioGroupSet(listOf("Ascending", "Descending"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun PreviewLibrarySortModal() {
    MusicTheme {
        LibrarySortSelectionBottomModal(
            onDismissRequest = {},
//            sheetState = SheetState,
//            coroutineScope = CoroutineScope(currentCoroutineContext()),
            itemInfo = PreviewAlbums[0],
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewMoreOptionsModal() {
    MusicTheme {
        MoreOptionsBottomModal(
            onDismissRequest = {},
//            sheetState: SheetState = rememberModalBottomSheetState(true),
//            contentColor: Color = MaterialTheme.colorScheme.secondary,
//            containerColor: Color = MaterialTheme.colorScheme.onSecondary,
//            scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
//            properties: ModalBottomSheetProperties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    //    coroutineScope: CoroutineScope,
            itemInfo = PreviewAlbums[0],
        )
    }
}