package com.example.music.ui.library.genre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewSongs
import com.example.music.model.AlbumGenreFilterResult
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
//called from Discover, used to output the list of albums
// and songs within selected genre
fun LazyListScope.genre(
    albumGenreFilterResult: AlbumGenreFilterResult,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit,
) {
    item {
        GenreAlbums(
            topAlbums = albumGenreFilterResult.topAlbums,
            navigateToAlbumDetails = navigateToAlbumDetails,
        )
    }

    val songs = albumGenreFilterResult.songs
    items(songs, key = { it.song.id }) { item ->
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
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

fun LazyGridScope.genre(
    albumGenreFilterResult: AlbumGenreFilterResult,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit,
) {
    fullWidthItem {
        GenreAlbums(
            topAlbums = albumGenreFilterResult.topAlbums,
            navigateToAlbumDetails = navigateToAlbumDetails,
        )
    }

    val songs = albumGenreFilterResult.songs
    items(songs, key = { it.song.id }) { item ->
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

@Composable
private fun GenreAlbums(
    topAlbums: List<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
) {
    GenreAlbumRow(
        albums = topAlbums,
        navigateToAlbumDetails = navigateToAlbumDetails,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GenreAlbumRow(
    albums: List<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = Keyline1,
            top = 8.dp,
            end = Keyline1,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            items = albums,
            key = { it.id }
        ) { album ->
            TopAlbumRowItem(
                albumTitle = album.title,//album.title,
                albumImageId = "image",//album.imageUrl,
                modifier = Modifier
                    .width(128.dp)
                    .clickable {
                        navigateToAlbumDetails(album)
                    }
            )
        }
    }
}

@Composable
private fun TopAlbumRowItem(
    albumTitle: String,
    albumImageId: String,
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
                contentDescription = albumTitle
            )
        }

        Text(
            text = albumTitle,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewGenre() {
    MusicTheme {
        GenreAlbums(
            topAlbums = PreviewAlbums,
            navigateToAlbumDetails = {},
        )
    }
}

@Preview
@Composable
fun PreviewSongList() {
    MusicTheme {
        SongListItem(
            song = PreviewSongs[0],
            album = PreviewAlbums[0],
            onClick = { },
            onQueueSong = { },
            isListEditable = false,
            showArtistName = true,
            showAlbumImage = true,
            showAlbumTitle = true,
            showDuration = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
