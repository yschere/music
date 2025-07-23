package com.example.music.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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


@Composable
fun JumpToTopFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.smallShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
//    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
//    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    Icon(
        Icons.Filled.KeyboardDoubleArrowUp,
        //Icons.Filled.VerticalAlignTop,
        contentDescription = "Jump to top of screen",
        modifier = modifier.clip(shape).size(40.dp),
        //modifier = Modifier.size(FloatingActionButtonDefaults.SmallIconSize),
    )
}



//likely the section where the Floating Action Button is placed in a Composable
//
//    val listState = rememberLazyListState()
//    // The FAB is initially shown. Upon scrolling past the first item we hide the FAB by using a
//    // remembered derived state to minimize unnecessary compositions.
//    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
//
//    Scaffold(
//        floatingActionButton = {
//            SmallFloatingActionButton(
//                modifier =
//                Modifier.animateFloatingActionButton(
//                    visible = fabVisible,
//                    alignment = Alignment.BottomEnd
//                ),
//                onClick = { /* do something */ },
//            ) {
//                Icon(
//                    Icons.Filled.KeyboardDoubleArrowUp,
//                    //Icons.Filled.VerticalAlignTop,
//                    contentDescription = "Jump to top of screen",
//                    modifier = modifier.clip(shape).size(),
//                    //modifier = Modifier.size(FloatingActionButtonDefaults.SmallIconSize),
//                )
//            }
//        },
//        floatingActionButtonPosition = FabPosition.End,
//    ) {
//        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
//            for (index in 0 until 100) {
//                item { Text(text = "List item - $index", modifier = Modifier.padding(24.dp)) }
//            }
//        }
//    }

/*
//visibility logic for button that is supposed to be visible once not at top of the list
AnimatedVisibility(visible = !isAtTopOfList) {
        ScrollToTopButton()
    }
 */