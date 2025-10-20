package com.example.music.ui.shared

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.MARGIN_PADDING
import com.example.music.designsys.theme.MINI_PLAYER_HEIGHT
import com.example.music.designsys.theme.SCROLL_FAB_BOTTOM_PADDING
import com.example.music.designsys.theme.SMALL_PADDING

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
            .shadow(elevation = 3.dp, shape = CircleShape)
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
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            // align FAB to bottom right in landscape, and bottom center in portrait
            .align(alignment =
                if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) Alignment.BottomEnd
                else Alignment.BottomCenter
            )
            // isActive means button needs extra clearance for MiniPlayer
            .padding(bottom =
                if (isActive) MINI_PLAYER_HEIGHT + SCROLL_FAB_BOTTOM_PADDING
                else SCROLL_FAB_BOTTOM_PADDING
            )
            // when in landscape, places fab away from right edge of screen by specific amount
            .padding(end =
                if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) 120.dp
                else 0.dp
            ),
        // Start the slide from 40 (pixels) above where the content is supposed to go, to produce a parallax effect
        // Animate scale from 0f to 1f using the top center as the pivot point.
        enter = slideInVertically(initialOffsetY = { -40 }) +
            expandVertically(expandFrom = Alignment.Top) +
            scaleIn(transformOrigin = TransformOrigin(0.5f, 0f)) +
            fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() +
            shrinkVertically() +
            fadeOut() +
            scaleOut(targetScale = 1.2f),
    ) {
        DrawIconBtn(
            icon = Icons.Filled.KeyboardDoubleArrowUp,
            description = stringResource(R.string.icon_scroll_to_top),
            onClick = onClick,
            btnModifier = Modifier
                .shadow(elevation = 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            tint = MaterialTheme.colorScheme.inversePrimary,
        )
    }
}

/**
 * More button that will navigate to a longer list of the shown items
 * @param onClick defines the action to take when the button is clicked
 * @param modifier defines any modifiers to apply to button
 */
@Composable
fun RowScope.NavToMoreBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        contentPadding = ButtonDefaults.TextButtonContentPadding,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary,
        ),
        modifier = modifier.padding(horizontal = MARGIN_PADDING)
            .align(Alignment.CenterVertically),
    ) {
        Text(
            text = "More",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * More button that will navigate to a longer list of the shown items
 * @param onClick defines the action to take when the button is clicked
 * @param modifier defines any modifiers to apply to button
 */
@Composable
fun RowScope.ShowLessBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(horizontal = MARGIN_PADDING)
            .align(Alignment.CenterVertically),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary,
        ),
    ) {
        Icon (
            imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
            contentDescription = null,
        )
        Text(
            text = "Back",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * More button that will navigate to a longer list of the shown items
 * @param onClick defines the action to take when the button is clicked
 * @param modifier defines any modifiers to apply to button
 */
@Composable
fun RowScope.ShowMoreBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(horizontal = MARGIN_PADDING)
            .align(Alignment.CenterVertically),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary,
        ),
    ) {
        Text(
            text = "More",
            style = MaterialTheme.typography.titleMedium
        )
        Icon (
            imageVector = Icons.Filled.KeyboardDoubleArrowRight,
            contentDescription = null,
        )
    }
}

/**
 * Play button that will begin playback for the list of shown items on the screen
 * @param onClick defines the action to take when the button is clicked
 * @param modifier defines any modifiers to apply to button
 */
@Composable
fun RowScope.PlayBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DrawTextBtn(
        onClick = onClick,
        icon = Icons.Filled.PlayArrow,
        description = stringResource(R.string.icon_play),
        btnModifier = modifier
            .padding(horizontal = CONTENT_PADDING)
            .weight(0.5f),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary,
        ),
        text = "PLAY"
    )
}

/**
 * Shuffle button that will begin and shuffle playback for the list of shown items on the screen
 * @param onClick defines the action to take when the button is clicked
 * @param modifier defines any modifiers to apply to button
 */
@Composable
fun RowScope.ShuffleBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DrawTextBtn(
        onClick = onClick,
        icon = Icons.Filled.Shuffle,
        description = stringResource(R.string.icon_shuffle),
        btnModifier = modifier
            .padding(horizontal = CONTENT_PADDING)
            .weight(0.5f),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary,
        ),
        text = "SHUFFLE"
    )
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
fun ClearFieldBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Filled.Clear,
        description = stringResource(id = R.string.icon_clear_field),
        onClick = onClick,
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
        icon = Icons.AutoMirrored.Filled.PlaylistAdd,
        description = stringResource(R.string.icon_add_to_playlist),
        onClick = onClick,
    )
}

@Composable
fun InfoBtn(
    onClick: () -> Unit,
) {
    DrawIconBtn(
        icon = Icons.Filled.Info,
        description = stringResource(R.string.icon_song_details),
        onClick = onClick,
        iconModifier = Modifier.padding(SMALL_PADDING)
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
                    targetValue =
                        if (isFave) 0.dp
                        else 1.dp,
                    label = "shadow"
                ).value,
                shape = MaterialTheme.shapes.small
            )
            .background(
                color = animateColorAsState(
                    targetValue = when {
                        isFave -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceContainerHighest
                    },
                    label = "background"
                ).value,
                shape = CircleShape
            )
            .padding(SMALL_PADDING),
        tint = animateColorAsState(
            targetValue = when {
                isFave -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.primary
            },
            label = "tint"
        ).value,
    )
}


/***********************************************************************************************
 *
 * ********** Base Buttons **********
 *
 **********************************************************************************************/

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

@Composable
private fun DrawTextBtn(
    icon: ImageVector,
    text: String,
    description: String? = null,
    onClick: () -> Unit,
    btnModifier: Modifier = Modifier,
    colors: ButtonColors,
) {
    Button(
        onClick = onClick,
        modifier = btnModifier,
        colors = colors,
    ) {
        Icon (
            imageVector = icon,
            contentDescription = description,
        )
        Text(
            text = text,
        )
    }
}
