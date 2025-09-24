package com.example.music.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.music.R

/***********************************************************************************************
 *
 * ********** Screen Content Action Buttons **********
 *
 **********************************************************************************************/

/**
 * Add Songs to Playlist btn, shown specifically when viewing PlaylistDetails of a playlist with no songs
 * @param onClick defines the action to take when the button is clicked
 * @param modifier defines any modifiers to apply to button
 */
@Composable
fun AddToPlaylistFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.semantics(mergeDescendants = true){},
) {
    DrawIconBtn(
        icon = Icons.Filled.Add,
        description = stringResource(R.string.icon_add_to_playlist),
        onClick = onClick,
        btnModifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        tint = MaterialTheme.colorScheme.inversePrimary,
    )
}

/**
 * Scroll to top floating action btn that appears after scrolling down beyond first few items
 * @param displayButton defines if the button should be displayed
 * @param isActive defines if the MiniPlayer is currently active
 * @param onClick defines the action to take when the button is clicked
 */
@Composable
fun BoxScope.ScrollToTopFAB(
    displayButton: State<Boolean>,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = displayButton.value,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(
                bottom =
                    if (isActive) 120.dp
                    else 60.dp
            ),
        enter = slideInVertically(
            // Start the slide from 40 (pixels) above where the content is supposed to go, to
            // produce a parallax effect
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + scaleIn(
            // Animate scale from 0f to 1f using the top center as the pivot point.
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f),
    ) {
        DrawIconBtn(
            icon = Icons.Filled.KeyboardDoubleArrowUp,
            description = stringResource(R.string.icon_scroll_to_top),
            onClick = onClick,
            btnModifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.primary),
            tint = MaterialTheme.colorScheme.inversePrimary,
        )
    }
}


/***********************************************************************************************
 *
 * ********** Screen Navigation Icon Buttons **********
 *
 **********************************************************************************************/

@Composable
fun BackNavBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        description = stringResource(id = R.string.icon_back_nav),
        onClick = onClick
    )
}

@Composable
fun NavDrawerBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Outlined.Menu,
        description = stringResource(R.string.icon_nav_drawer),
        onClick = onClick,
    )
}

@Composable
fun QueueBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.AutoMirrored.Filled.QueueMusic,
        description = stringResource(R.string.icon_queue),
        onClick = onClick,
    )
}

@Composable
fun SearchBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Outlined.Search,
        description = stringResource(R.string.icon_search),
        onClick = onClick,
    )
}


/***********************************************************************************************
 *
 * ********** Inline Screen Content Icon Buttons **********
 *
 **********************************************************************************************/

@Composable
fun CreatePlaylistBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Filled.Add,
        description = stringResource(R.string.icon_create_new_playlist),
        onClick = onClick,
    )
}

@Composable
fun AddToPlaylistBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Filled.Add,
        description = stringResource(R.string.icon_add_to_playlist),
        onClick = onClick,
    )
}

@Composable
fun MoreOptionsBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Default.MoreVert,
        description = stringResource(R.string.icon_more),
        onClick = onClick,
    )
}

/**
 * TODO: not sure if this should be icon button yet
 */
@Composable
fun ReorderItemBtn(
    onClick: () -> Unit,
    title: String,
) {
    DrawIconBtn(
        icon = Icons.Outlined.Reorder,
        description = stringResource(R.string.icon_reorder) + " for song " + title,
        onClick = onClick,
    )
}

@Composable
fun MultiSelectBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.semantics(mergeDescendants = true){},
) {
    DrawIconBtn(
        icon = Icons.Filled.Checklist,
        description = stringResource(R.string.icon_multi_select),
        onClick = onClick,
        btnModifier = modifier,
    )
}

@Composable
fun SortBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.semantics(mergeDescendants = true){},
) {
    DrawIconBtn(
        icon = Icons.AutoMirrored.Filled.Sort,
        description = stringResource(R.string.icon_sort),
        onClick = onClick,
        btnModifier = modifier,
    )
}


/**
 * Icon for showing if a song is in the favorites list. NOT IN USE
 */
@Composable
fun ToggleFavoriteBtn(
    isFave: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(
        if (isFave) R.string.icon_fave_remove
        else R.string.icon_fave_add
    )
    DrawIconBtn(
        icon = when {
            isFave -> Icons.Default.Check
            else -> Icons.Default.Add
        },
        description = when {
            isFave -> stringResource(R.string.icon_is_fave)
            else -> stringResource(R.string.icon_is_not_fave)
        },
        onClick = onClick,
        btnModifier = modifier.semantics { onClick(label = clickLabel, action = null) },
        iconModifier = Modifier
            .shadow(
                elevation = animateDpAsState(
                    if (isFave) 0.dp
                    else 1.dp,
                    label = "shadow"
                ).value,
                shape = MaterialTheme.shapes.small
            )
            .background(
                color = animateColorAsState(
                    when {
                        isFave -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceContainerHighest
                    }, label = "background"
                ).value,
                shape = CircleShape
            )
            .padding(4.dp),
        tint = animateColorAsState(
            when {
                isFave -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.primary
            }, label = "tint"
        ).value,
    )
}

@Composable
private fun DrawIconBtn(
    icon: ImageVector,
    description: String? = null,
    onClick: () -> Unit = {},
    btnModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    IconButton(
        onClick = onClick,
        modifier = btnModifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = tint,
            modifier = iconModifier,
        )
    }
}
