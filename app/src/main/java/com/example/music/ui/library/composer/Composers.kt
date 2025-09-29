package com.example.music.ui.library.composer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.ICON_SIZE
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.testing.PreviewComposers
import com.example.music.domain.model.ComposerInfo
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

private const val TAG = "Library Composers"

/**
 * Overloaded version of lazy list for artistItems
 */
fun LazyListScope.composerItems(
    composers: List<ComposerInfo>,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    onComposerMoreOptionsClick: (ComposerInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {}
) {
    Log.i(TAG, "Lazy List START")

    // Item Count, Sort btn, Multi-Select btn row
    item {
        ItemCountAndSortSelectButtons(
            id = R.plurals.composers,
            itemCount = composers.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Composer List
    items(
        items = composers
    ) { composer ->
        ComposerListItem(
            composer = composer,
            navigateToComposerDetails = { navigateToComposerDetails(composer) },
            onMoreOptionsClick = { onComposerMoreOptionsClick(composer) },
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

/**
 * Overloaded version of lazy grid for artistItems - default grid style
 */
fun LazyGridScope.composerItems(
    composers: List<ComposerInfo>,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    onComposerMoreOptionsClick: (ComposerInfo) -> Unit,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Log.i(TAG, "Lazy Grid START")

    // Item Count, Sort btn, Multi-Select btn row
    fullWidthItem{
        ItemCountAndSortSelectButtons(
            id = R.plurals.composers,
            itemCount = composers.size,
            onSortClick = onSortClick,
            onSelectClick = onSelectClick,
            modifier = Modifier.padding(horizontal = SMALL_PADDING)
        )
    }

    // Composer List
    items(
        items = composers,
        span = { GridItemSpan(maxLineSpan) }
    ) { composer ->
        ComposerListItem(
            composer = composer,
            navigateToComposerDetails = { navigateToComposerDetails(composer) },
            onMoreOptionsClick = { onComposerMoreOptionsClick(composer) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ComposerListItem(
    composer: ComposerInfo,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    hasBackground: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color =
                if (hasBackground) MaterialTheme.colorScheme.surfaceContainer
                else Color.Transparent,
            onClick = { navigateToComposerDetails(composer) }
        ) {
            ComposerListItemRow(
                composer = composer,
                onMoreOptionsClick = onMoreOptionsClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ComposerListItemRow(
    composer: ComposerInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = CONTENT_PADDING)
            .padding(start = CONTENT_PADDING),
    ) {
        ComposerListItemIcon(
            composer = composer.name,
            modifier = Modifier
                .size(ICON_SIZE)
                .clip(MaterialTheme.shapes.small),
        )

        Column(modifier.weight(1f)){
            Text(
                text = composer.name,
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
                    text = quantityStringResource(R.plurals.songs, composer.songCount, composer.songCount),
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

        }

        MoreOptionsBtn(onClick = onMoreOptionsClick)
    }
}

/**
 * Composable for drawing the Composer Item Icon to contain the first initial of a composer's name
 */
@Composable
private fun ComposerListItemIcon(
    composer: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Text(
            text = composer[0].toString(),
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
fun PreviewComposerItem() {
    MusicTheme {
        ComposerListItem(
            composer = PreviewComposers[0],
            navigateToComposerDetails = {},
            onMoreOptionsClick = {},
            modifier = Modifier
        )
    }
}