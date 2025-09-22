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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.music.R

@Composable
fun ToggleFollowPodcastIconButton(
    isFollowed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(if (isFollowed) R.string.pb_play else R.string.pb_pause)
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics {
            onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            imageVector = when {
                isFollowed -> Icons.Default.Check
                else -> Icons.Default.Add
            },
            contentDescription = when {
                isFollowed -> stringResource(R.string.pb_play)
                else -> stringResource(R.string.pb_pause)
            },
            tint = animateColorAsState(
                when {
                    isFollowed -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                }, label = "tint"
            ).value,
            modifier = Modifier
                .shadow(
                    elevation = animateDpAsState(if (isFollowed) 0.dp else 1.dp, label = "shadow").value,
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = animateColorAsState(
                        when {
                            isFollowed -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceContainerHighest
                        }, label = "background"
                    ).value,
                    shape = CircleShape
                )
                .padding(4.dp)
        )
    }
}

/**
 * Scroll to top floating action btn that appears after scrolling down beyond first few items
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
                    if (isActive) 100.dp
                    else 40.dp
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
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardDoubleArrowUp,
                contentDescription = stringResource(R.string.icon_scroll_to_top),
                tint = MaterialTheme.colorScheme.inversePrimary,
            )
        }
    }
}


/***********************************************************************************************
 *
 * ********** Navigation / Top App Bar Icon buttons **********
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
 * ********** Screen Content Icon buttons **********
 *
 **********************************************************************************************/

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
        onClick = onClick
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
        onClick = onClick
    )
}

@Composable
private fun DrawIconBtn(
    icon: ImageVector,
    description: String?,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}
