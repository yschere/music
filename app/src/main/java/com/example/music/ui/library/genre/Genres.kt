package com.example.music.ui.library.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.domain.testing.PreviewGenres
import com.example.music.model.GenreInfo
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

fun LazyListScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
) {
    item {
        Text(
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.genres, genres.size, genres.size)) {
                it.value[1].uppercase()
            },// { index, c -> if (("""\s[a-z]""".toRegex()}.toString(),//  ("""\s[a-z]""".toRegex()), // \s[a-z]
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(genres) { item ->
        GenreListItem(
            genre = item,
            navigateToGenreDetails = navigateToGenreDetails,
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

fun LazyGridScope.genreItems(
    genres: List<GenreInfo>,
    navigateToGenreDetails: (GenreInfo) -> Unit,
) {
    fullWidthItem {
        Text(
            //text = quantityStringResource(R.plurals.genres, genres.size, genres.size), //original
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.genres, genres.size, genres.size)) {
                it.value.uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    items(genres) { item ->
        GenreListItem(
            genre = item,
            navigateToGenreDetails = navigateToGenreDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GenreListItem(
    genre: GenreInfo,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) { //outermost layer with padding of 4 for separation between other song list items
        Surface(
            //second most layer, contains onclick action and background color
            shape = MaterialTheme.shapes.large,
            //color = MaterialTheme.colorScheme.background,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { navigateToGenreDetails(genre) }, //this is how navigateToPlayer should be used for each song ListItem, as the passed in onClick event
        ) {
            GenreListItemRow( //design content of song list item
                genre = genre,
                modifier = modifier//.padding(4.dp),
            )
        }
    }
}


@Composable
private fun GenreListItemRow(
    genre: GenreInfo,
    modifier: Modifier = Modifier,
) {
    Row( //third layer, contains layout logic and information for content
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {

        GenreListItemIcon(
            genre = genre.name, //placeholder
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Column(modifier.weight(1f)) {
            Text(
                text = genre.name,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Row(
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                /*if (genre.albumCount != null) { //if showArtistName is true
                    Text(
                        text = quantityStringResource(R.plurals.albums, genre.albumCount!!, genre.albumCount!!),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }*/
                Text(
                    text = quantityStringResource(R.plurals.songs, genre.songCount, genre.songCount),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
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
private fun GenreListItemIcon(
    genre: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))){
        Text(
            text = genre[0].toString(), //TODO: FOUND, one place where song property is needed that PlayerSong does not need. original code: song.albumTrackNumber from SongInfo with album context, still the same in SongListItem(songinfo, albumInfo)
            minLines = 1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxSize().padding(vertical = 15.dp),
        )
    }
}

@Preview
@Composable
fun PreviewGenreItem() {
    MusicTheme {
        GenreListItem(
            genre = PreviewGenres[0],
            navigateToGenreDetails = {}
        )
    }
}


/*


package com.example.music.ui.home.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.model.AlbumGenreFilterResult
import com.example.music.model.AlbumInfo
import com.example.music.model.FilterableGenresModel
import com.example.music.model.GenreInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.home.genre.albumGenre
import com.example.music.util.fullWidthItem

//works by taking the filterableGenresModel (houses the options of genres to select, as well as the selected genre) and with albumGenreFilterResult, will output the list of top albums and all songs that are within the selected genre
//TODO determine if discover section is needed (might not be but work exploring if there's anything that can be repurposed
fun LazyListScope.discoverItems(
    filterableGenresModel: FilterableGenresModel,
    albumGenreFilterResult: AlbumGenreFilterResult,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    onGenreSelected: (GenreInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit,
) {
    if (filterableGenresModel.isEmpty) {
        // TODO: empty state
        return
    }

    item {
        Spacer(Modifier.height(8.dp))

        AlbumGenreTabs(
            filterableGenresModel = filterableGenresModel,
            onGenreSelected = onGenreSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
    }

    albumGenre(
        albumGenreFilterResult = albumGenreFilterResult,
        navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToPlayer = navigateToPlayer,
        //onTogglePodcastFollowed = onTogglePodcastFollowed,
        onQueueSong = onQueueSong,
    )
}

fun LazyGridScope.discoverItems(
    filterableGenresModel: FilterableGenresModel,
    albumGenreFilterResult: AlbumGenreFilterResult,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    onGenreSelected: (GenreInfo) -> Unit,
    onQueueSong: (PlayerSong) -> Unit,
) {
    if (filterableGenresModel.isEmpty) {
        // TODO: empty state
        return
    }

    fullWidthItem {
        Spacer(Modifier.height(8.dp))

        AlbumGenreTabs(
            filterableGenresModel = filterableGenresModel,
            onGenreSelected = onGenreSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
    }

    albumGenre(
        albumGenreFilterResult = albumGenreFilterResult,
        navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToPlayer = navigateToPlayer,
        onQueueSong = onQueueSong,
    )
}

@Composable
private fun AlbumGenreTabs(
    filterableGenresModel: FilterableGenresModel,
    onGenreSelected: (GenreInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = filterableGenresModel.genres.indexOf(
        filterableGenresModel.selectedGenre
    )
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Keyline1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(
            items = filterableGenresModel.genres,
            key = { i, genre -> genre.id }
        ) { index, genre ->
            ChoiceChipContent(
                text = genre.name,
                selected = index == selectedIndex,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                onClick = { onGenreSelected(genre) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.cd_selected_genre),
                    modifier = Modifier.height(18.dp)
                )
            }
        },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        border = null,
        modifier = modifier,
    )
}



 */



/*


package com.example.music.ui.home.genre

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

fun LazyListScope.albumGenre(
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
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

fun LazyGridScope.albumGenre(
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
                albumTitle = album.title,
                albumImageId = "image",//album.imageUrl,
                //onToggleFollowClicked = { onTogglePodcastFollowed(podcast) },
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
fun PreviewGenreAlbums() {
    MusicTheme {
        GenreAlbums(
            topAlbums = PreviewAlbums,
            navigateToAlbumDetails = {},
        )
    }
}

@Preview
@Composable
fun PreviewSongListItem() {
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}




 */