package com.example.music.ui.library.playlist

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.model.PlaylistInfo
import com.example.music.ui.library.LibraryCategory
import com.example.music.ui.shared.ActionItem
import com.example.music.ui.shared.ActionOptionRow
import com.example.music.ui.shared.CreatePlaylistBottomModal
import com.example.music.ui.shared.ItemCountAndPlusSortSelectButtons
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.LibrarySortSelectionBottomModal
import com.example.music.ui.shared.MoreOptionModalHeader
import com.example.music.ui.shared.PlaylistItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "Library Playlists"

/**
 * Overloaded version of lazy list for playlistItems
 */
fun LazyListScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    onPlaylistMoreOptionsClick: (PlaylistInfo) -> Unit,
    onPlusClick: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy List START")

    item {
        ItemCountAndPlusSortSelectButtons(
            id = R.plurals.playlists,
            itemCount = playlists.size,
            createOrAdd = true, // create playlists btn
            onPlusClick = onPlusClick,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    items(
        items = playlists
    ) { playlist ->
        PlaylistItem(
            playlist = playlist,
            navigateToPlaylistDetails = { navigateToPlaylistDetails(playlist) },
            onMoreOptionsClick = { onPlaylistMoreOptionsClick(playlist) },
            cardOrRow = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy grid for playlistItems
 */
fun LazyGridScope.playlistItems(
    playlists: List<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    onPlaylistMoreOptionsClick: (PlaylistInfo) -> Unit,
    onPlusClick: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Plus btn, Sort btn, Multi-Select btn row
    fullWidthItem {
        ItemCountAndPlusSortSelectButtons(
            id = R.plurals.playlists,
            itemCount = playlists.size,
            createOrAdd = true, // create playlists btn
            onPlusClick = onPlusClick,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick
        )
    }

    // Playlist List
    items(
        items = playlists,
        span = { GridItemSpan(maxLineSpan) }
    ) { playlist ->
        PlaylistItem(
            playlist = playlist,
            navigateToPlaylistDetails = { navigateToPlaylistDetails(playlist) },
            onMoreOptionsClick = { onPlaylistMoreOptionsClick(playlist) },
            cardOrRow = true,
        )
    }
}
