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
            showDuration = true,
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
            showDuration = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
