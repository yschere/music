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
