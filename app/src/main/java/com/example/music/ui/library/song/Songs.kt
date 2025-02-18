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
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.shared.SongListItem
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

/**
 * Overloaded version of lazy list for songItems
 */
fun LazyListScope.songItems(
    songs: List<SongInfo>,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.songs, songs.size, songs.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(playerSongs) { item ->
        SongListItem(
            song = item, //TODO: PlayerSong support
            onClick = { navigateToPlayerSong(item) },
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

/**
 * Overloaded version of lazy grid for songItems
 */
fun LazyGridScope.songItems(
    songs: List<SongInfo>,
    playerSongs: List<PlayerSong>, //TODO: PlayerSong support
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit
) {
    fullWidthItem {
        Text(
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.songs, songs.size, songs.size),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(playerSongs) { item ->
        SongListItem(
            song = item, //TODO: PlayerSong support
            onClick = { navigateToPlayerSong(item) },
            //onQueueSong = {},
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


/*



package com.example.music.ui.home.library

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
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.model.LibraryInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.SongListItem
import com.example.music.util.fullWidthItem

fun LazyListScope.libraryItems(
    library: LibraryInfo, //held 'latest songs'
    navigateToPlayer: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit
) {
    item {
        Text(
            text = stringResource(id = R.string.all_songs),
            modifier = Modifier.padding(
                start = Keyline1,
                top = 16.dp,
            ),
            style = MaterialTheme.typography.titleLarge,
        )
    }

    items(
        library,
        key = { it.song.id }
    ) { item ->
        SongListItem(
            song = item.song,
            album = item.album,
            onClick = navigateToPlayer,
            onQueueSong = onQueueSong,
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

fun LazyGridScope.libraryItems(
    library: LibraryInfo,
    navigateToPlayer: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit
) {
    fullWidthItem {
        Text(
            text = stringResource(id = R.string.all_songs),
            modifier = Modifier.padding(
                start = Keyline1,
                top = 16.dp,
            ),
            style = MaterialTheme.typography.headlineLarge,
        )
    }

    items(
        library,
        key = { it.song.id }
    ) { item ->
        SongListItem(
            song = item.song,
            album = item.album,
            onClick = navigateToPlayer,
            onQueueSong = onQueueSong,
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}




 */