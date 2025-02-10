package com.example.music.ui.library.song

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.GetAlbumDataUseCase
import com.example.music.model.AlbumInfo
import com.example.music.model.LibraryInfo
import com.example.music.model.SongInfo
import com.example.music.model.SongSortModel
import com.example.music.player.model.PlayerSong
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.shared.SongListItem
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

fun LazyListScope.songItems(
    //library: LibraryInfo,
    songSortModel: SongSortModel,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit
) {
    //val songs = songSortModel.songs
    val songs = playerSongs //TODO: PlayerSong support
    item {
//        Text(
//            text = stringResource(id = R.string.all_songs),
//            modifier = Modifier.padding(
//                start = Keyline1,
//                top = 16.dp,
//            ),
//            style = MaterialTheme.typography.titleLarge,
//        )
        Text(
            text = if (songs.size == 1) "${songs.size} song" else "${songs.size} songs",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(
        songs, //library
        //key = { it.id } //key = { it.song.id } //there are lists like playlists that can have multiple of the same song, so don't reference them by id
    ) { item ->
        //val album = getAlbumDataUseCase(item)

        SongListItem(
            //song = item.song,
            //album = item.album,
            //onClick = navigateToPlayer,
            song = item, //TODO: PlayerSong support
            onClick = {},
            onQueueSong = onQueueSong,
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showDuration = true,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

fun LazyGridScope.songItems(
    //library: LibraryInfo,
    songSortModel: SongSortModel,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit
) {
    //val songs = songSortModel.songs
    val songs = playerSongs //TODO: PlayerSong support

    fullWidthItem {
//        Text(
//            text = stringResource(id = R.string.all_songs),
//            modifier = Modifier.padding(
//                start = Keyline1,
//                top = 16.dp,
//            ),
//            style = MaterialTheme.typography.headlineLarge,
//        )
        Text(
            text = if (songs.size == 1) "${songs.size} song" else "${songs.size} songs",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(
        songs, //library
        key = { it.id } //key = { it.song.id }
    ) { item ->
        SongListItem(
            //song = item.song,
            //album = item.album,
            //onClick = navigateToPlayer,
            song = item, //TODO: PlayerSong support
            onClick = {},
            onQueueSong = onQueueSong,
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showDuration = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
